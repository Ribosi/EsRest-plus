package template;

import Excepetion.CriteriaException;
import com.alibaba.fastjson.JSON;
import helper.BucketAggHelper;
import helper.MetricAggHelper;
import helper.SearchSourceHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.avg.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;
import util.DateUtil;

import java.util.*;

public class ElasticSearchAggregation {

    private BucketAggHelper bucketAggHelper = new BucketAggHelper();
    private SearchSourceHelper searchSourceHelper = new SearchSourceHelper();
    private MetricAggHelper metricAggHelper = new MetricAggHelper();

    public List<AggResultDto> selectByCriteria(EsCriteria esCriteria, RestHighLevelClient restHighLevelClient, String[] index, String type) throws CriteriaException {
        if(!esCriteria.checkCorrectAgg()){
            throw new CriteriaException("criteria type not support");
        }
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceHelper.builderSearch(esCriteria,searchSourceBuilder);
            Pair<AggregationBuilder,AggregationBuilder> aggregationBuilderPair = bucketAggHelper.builderBucket(esCriteria);
            AggregationBuilder metricAggBuilder = metricAggHelper.builderMetric(esCriteria,aggregationBuilderPair.getValue());
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggregationBuilderPair.getKey()==null?metricAggBuilder:aggregationBuilderPair.getKey());
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(type);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            List<AggResultDto> result = resolveResponse(searchResponse,esCriteria);
            return result;
        }catch (Throwable throwable){
            throw new CriteriaException("run selectByCriteria error:{}",throwable);
        }
    }

    private  List<AggResultDto> resolveResponse(SearchResponse searchResponse,EsCriteria esCriteria) {
        List<AggResultDto> aggResultDtos = new ArrayList<>();
        if(!esCriteria.isISNEEDAGG()){
            if(esCriteria.getMetricField()==null){
                aggResultDtos.add(new AggResultDto("",searchResponse.getHits().totalHits));
            }else {
                aggResultDtos.add(new AggResultDto("",getMetricValueByResponse(searchResponse,esCriteria.getMetricField())));
            }
        }else if(esCriteria.isISNEEDAGG()&&esCriteria.getDateIntervalDto() == null){
            int i = 0;
            Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().getAsMap();
            ParsedTerms parsedTerms = (ParsedTerms) aggregationMap.get("agg" + (i));
            for (Terms.Bucket bucket : parsedTerms.getBuckets()) {
                aggResultDtos.add(setAggResult(new AggResultDto(((Terms.Bucket) bucket).getKeyAsString(), ((Terms.Bucket) bucket).getDocCount()), bucket, i + 1, esCriteria.getGroupByField().size(), esCriteria));
            }
        }else if(esCriteria.getDateIntervalDto()!= null){
            Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().getAsMap();
            ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) aggregationMap.get("agg");
            List<ParsedDateHistogram.ParsedBucket> buckets = (List<ParsedDateHistogram.ParsedBucket>) parsedDateHistogram.getBuckets();
            for(ParsedDateHistogram.ParsedBucket bucket : buckets){
                DateTime dateTime = (DateTime)bucket.getKey();
                String key = DateUtil.getStrFromDate(new Date(dateTime.getMillis()),dateFormatByAggType(esCriteria.getDateIntervalDto().getDateHistogramInterval()));
                AggResultDto aggResultDto =new AggResultDto(key,bucket.getDocCount());
                if(esCriteria.getMetricField()!=null){
                    Double metricValue = getDateMetricValue(bucket,esCriteria.getMetricField());
                    aggResultDto.setValue(metricValue);
                }
                aggResultDtos.add(aggResultDto);
            }
        }
        return aggResultDtos;
    }

    public AggResultDto setAggResult(AggResultDto aggResultDto,Terms.Bucket bucket,int start,int size,EsCriteria esCriteria){
        if(start == size){
            if(esCriteria.getMetricField()!=null){
                Double value = getMetricValue(bucket,esCriteria.getMetricField());
                aggResultDto.setValue(value);
            }
            return aggResultDto;
        }
        List<AggResultDto> aggResultDtos = new ArrayList<>();
        ParsedTerms parsedTerms = (ParsedTerms) bucket.getAggregations().getAsMap().get("agg"+(start++));
        List<? extends Terms.Bucket> incrBucket = parsedTerms.getBuckets();
        for(int j =0;j<incrBucket.size();j++){
            AggResultDto resultDto = new AggResultDto(((Terms.Bucket) incrBucket.get(j)).getKeyAsString(),((Terms.Bucket) incrBucket.get(j)).getDocCount());
            aggResultDtos.add(resultDto);
            setAggResult(resultDto,incrBucket.get(j),start,size,esCriteria);
        }
        aggResultDto.setBucketAggDto(aggResultDtos);
        return aggResultDto;
    }

    private Double getMetricValue(Terms.Bucket bucket,MetricFieldDto metricFieldDto){
        switch (metricFieldDto.getMetricType()){
            case DISTINCT:
                Cardinality cardinality = bucket.getAggregations().get("distinct");
                return cardinality.getValue()*1.0;
            case SUM:
                ParsedSum parsedSum = bucket.getAggregations().get("sum");
                return parsedSum.getValue();
            case AVG:
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg");
                return parsedAvg.getValue();
        }
        return null;
    }

    private Double getMetricValueByResponse(SearchResponse searchResponse,MetricFieldDto metricFieldDto){
        switch (metricFieldDto.getMetricType()){
            case DISTINCT:
                Cardinality cardinality = searchResponse.getAggregations().get("distinct");
                return cardinality.getValue()*1.0;
            case SUM:
                ParsedSum parsedSum = searchResponse.getAggregations().get("sum");
                return parsedSum.getValue();
            case AVG:
                ParsedAvg parsedAvg = searchResponse.getAggregations().get("avg");
                return parsedAvg.getValue();
        }
        return null;
    }

    private Double getDateMetricValue(ParsedDateHistogram.ParsedBucket bucket,MetricFieldDto metricFieldDto){
        switch (metricFieldDto.getMetricType()){
            case DISTINCT:
                Cardinality cardinality = bucket.getAggregations().get("distinct");
                return cardinality.getValue()*1.0;
            case SUM:
                ParsedSum parsedSum = bucket.getAggregations().get("sum");
                return parsedSum.getValue();
            case AVG:
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg");
                return parsedAvg.getValue();
        }
        return null;
    }


    public String dateFormatByAggType(DateHistogramInterval aggType) {
        if(StringUtils.isEmpty(aggType)){
            return DateUtil.FORMAT_YMDHM;
        }
        if(aggType == DateHistogramInterval.SECOND){
            return DateUtil.FORMAT_YMDHMS;
        }else if(aggType == DateHistogramInterval.MINUTE || aggType ==DateHistogramInterval.HOUR){
            return DateUtil.FORMAT_YMDHM;
        }else {
            return DateUtil.FORMAT_YMD;
        }
    }

    /**
     * 过滤-聚合操作 返回map list
     * @param esCriteria
     * @param restHighLevelClient
     * @param index
     * @param type
     * @return
     * @throws CriteriaException
     */
    public List<Map<String, Object>> aggsAsMap(EsCriteria esCriteria, RestHighLevelClient restHighLevelClient, String[] index, String type) throws CriteriaException {
        if (!esCriteria.isISNEEDAGG()) {
            throw new CriteriaException("not aggregation operation!");
        }
        if (!esCriteria.checkCorrectAgg()) {
            throw new CriteriaException("criteria type not support");
        }
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceHelper.builderSearch(esCriteria, searchSourceBuilder);
            Pair<AggregationBuilder, AggregationBuilder> aggregationBuilderPair = bucketAggHelper.builderBucket(esCriteria);
            metricAggHelper.builderMetric(esCriteria, aggregationBuilderPair.getRight());
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggregationBuilderPair.getLeft());
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(type);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            return parseFromAggResult(searchResponse.getAggregations().asMap());
        } catch (Throwable throwable) {
            throw new CriteriaException("run aggAsMap error:{}", throwable);
        }
    }

    private List<Map<String, Object>> parseFromAggResult(Map<String, Aggregation> aggMap) {
        Queue<Map<String, Aggregation>> aggQueue = new ArrayDeque<>();
        Queue<Map<String, Object>> dataQueue = new ArrayDeque<>();

        aggQueue.offer(aggMap);
        dataQueue.offer(new HashMap<>());

        List<Map<String, Object>> result = new ArrayList<>();

        while (!aggQueue.isEmpty()) {
            Map<String, Aggregation> map = aggQueue.poll();
            final Map<String, Object> lastData = dataQueue.poll();
            map.forEach((field, agg) -> {
                if (agg instanceof ParsedTerms) {
                    ParsedTerms parsedTerms = (ParsedTerms) agg;
                    for (Terms.Bucket bucket : parsedTerms.getBuckets()) {
                        String key = bucket.getKeyAsString();
                        Map<String, Object> newItem = new HashMap<>(lastData);
                        newItem.put(field, key);
                        Map<String, Aggregation> childMap  = bucket.getAggregations().getAsMap();
                        if(childMap == null || childMap.isEmpty()) {
                            result.add(newItem);
                        } else {
                            dataQueue.offer(newItem);
                            aggQueue.offer(childMap);
                        }
                    }

                } else if (agg instanceof ParsedCardinality) {
                    ParsedCardinality parsedCardinality = (ParsedCardinality) agg;
                    long count = parsedCardinality.getValue();
                    Map<String, Object> newItem = new HashMap(lastData);
                    newItem.put(field, count);
                    result.add(newItem);
                } else if(agg instanceof ParsedSingleValueNumericMetricsAggregation) {
                    ParsedSingleValueNumericMetricsAggregation parsedAgg = (ParsedSingleValueNumericMetricsAggregation) agg;
                    double count = parsedAgg.value();
                    Map<String, Object> newItem = new HashMap(lastData);
                    newItem.put(field, count);
                    result.add(newItem);
                }
            });
        }
        return result;
    }

    public List selectListByEsCriteria(EsCriteria esCriteria,Class clazz,RestHighLevelClient restHighLevelClient, String[] index, String type) throws CriteriaException {
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceHelper.builderSearch(esCriteria, searchSourceBuilder);
            searchSourceBuilder.size(10000);
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(type);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            List list = new ArrayList();
            for(int i=0;i<searchHits.length;i++){
                SearchHit searchHit=searchHits[i];
                list.add(JSON.parseObject(searchHit.getSourceAsString(),clazz));
            }
            return list;
        } catch (Throwable throwable) {
            throw new CriteriaException("selectListByEsCriteria error:{}", throwable);
        }
    }
}
