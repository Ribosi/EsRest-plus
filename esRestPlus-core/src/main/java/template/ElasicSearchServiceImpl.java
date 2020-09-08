package template;

import Excepetion.CriteriaException;
import annotation.EsField;
import annotation.Exist;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class ElasicSearchServiceImpl {

    private final int SELECT_SIZE = 10000;

    private ElasticSearchAggregation elasticSearchAggregation = new ElasticSearchAggregation();

    public List selectByCondition(Object o,RestHighLevelClient restHighLevelClient,String[] index,String type) throws IOException, IllegalAccessException {
        SearchHit[] searchHits = getSearchResponse(o,null,new HashMap<>(),index,type,restHighLevelClient,SELECT_SIZE).getHits();
        List list = new ArrayList();
        for(int i=0;i<searchHits.length;i++){
            SearchHit searchHit=searchHits[i];
            list.add(JSON.parseObject(searchHit.getSourceAsString(),o.getClass()));
        }
        return list;
    }

    public List selectByConditionWithKeyword(Object o,Set<String> keywords,RestHighLevelClient restHighLevelClient,String[] index,String type) throws IOException, IllegalAccessException {
        SearchHit[] searchHits = getSearchResponse(o,keywords,new HashMap<>(),index,type,restHighLevelClient,SELECT_SIZE).getHits();
        List list = new ArrayList();
        for(int i=0;i<searchHits.length;i++){
            SearchHit searchHit=searchHits[i];
            list.add(JSON.parseObject(searchHit.getSourceAsString(),o.getClass()));
        }
        return list;
    }

    public  List selectByConditionWithExtraMap(Object t,Map<String,Object> extraMap,RestHighLevelClient restHighLevelClient,String[] index,String type) throws IOException, IllegalAccessException {
        SearchHit[] searchHits = getSearchResponse(t,null,extraMap,index,type,restHighLevelClient,SELECT_SIZE).getHits();
        List list = new ArrayList();
        for(int i=0;i<searchHits.length;i++){
            SearchHit searchHit=searchHits[i];
            list.add(JSON.parseObject(searchHit.getSourceAsString(),t.getClass()));
        }
        return list;
    }

    public  List selectByConditionWithExtraMapAndKeywords(Object t,Set<String> keywords,Map<String,Object> extraMap,RestHighLevelClient restHighLevelClient,String[] index,String type) throws IOException, IllegalAccessException {
        SearchHit[] searchHits = getSearchResponse(t,keywords,extraMap,index,type,restHighLevelClient,SELECT_SIZE).getHits();
        List list = new ArrayList();
        for(int i=0;i<searchHits.length;i++){
            SearchHit searchHit=searchHits[i];
            list.add(JSON.parseObject(searchHit.getSourceAsString(),t.getClass()));
        }
        return list;
    }

    public long selectCountByCondition(Object a,Set<String> keywords, RestHighLevelClient restHighLevelClient, String[] index, String type) throws IOException, IllegalAccessException {
        return getSearchResponse(a,keywords,null,index,type,restHighLevelClient,0).totalHits;
    }

    public long selectCountByConditionAndExtra(Object a,Set<String> keywords,Map<String,Object> extraMap, RestHighLevelClient restHighLevelClient, String[] index, String type) throws IOException, IllegalAccessException {
        return getSearchResponse(a,keywords,extraMap,index,type,restHighLevelClient,0).totalHits;
    }

    public void insert(Object o,RestHighLevelClient restHighLevelClient,String[] index,String type) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index[0],type);
        indexRequest.source(JSON.toJSONString(o), XContentType.JSON);
        restHighLevelClient.index(indexRequest);
    }

    public Map selectIdsAndLists(Object o,RestHighLevelClient restHighLevelClient,String[] index,String type) throws IOException, IllegalAccessException {
        SearchHit[] searchHits = getSearchResponse(o,null,null,index,type,restHighLevelClient,SELECT_SIZE).getHits();
        Map map = new HashMap();
        for(int i=0;i<searchHits.length;i++){
            SearchHit searchHit=searchHits[i];
            map.put(searchHit.getId(),JSON.parseObject(searchHit.getSourceAsString(),o.getClass()));
        }
        return map;
    }

    public void update(Object o, String id,RestHighLevelClient restHighLevelClient,String index,String type) throws IOException, IllegalAccessException {
        UpdateRequest updateRequest=new UpdateRequest(index,type,id);
        Map<String, Object> map = new HashMap<String,Object>();
        Class<?> clazz = o.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if(field.get(o)==null){
                continue;
            }
            if(field.getType()==Date.class){
                Date value = (Date) field.get(o);
                map.put(fieldName, value.getTime());
            }else {
                Object value = field.get(o);
                map.put(fieldName, value);
            }
        }
        updateRequest.doc(map);
        restHighLevelClient.update(updateRequest);
    }

    private SearchHits getSearchResponse(Object a,Set<String> keywords,Map<String,Object> extraMap,String[] index,String type,RestHighLevelClient restHighLevelClient,int size) throws IllegalAccessException,IOException{
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Class<?> clazz = a.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(a);
            if(value!=null){
                if(field.getAnnotation(EsField.class)!=null){
                    fieldName = field.getAnnotation(EsField.class).name();
                }
                if(!CollectionUtils.isEmpty(keywords)&&keywords.contains(fieldName)){
                    fieldName = fieldName + ".keyword";
                }
                boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName,value));
            }
//            if(field.getAnnotation(Exist.class)!=null) {
//                Exist exist = field.getAnnotation(Exist.class);
//                if(exist.value()){
//                    boolQueryBuilder.filter(QueryBuilders.existsQuery(fieldName));
//                }else {
//                    boolQueryBuilder.filter(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(fieldName)));
//                }
//            }
        }
        if(!CollectionUtils.isEmpty(extraMap)){
            for(String extraKey : extraMap.keySet()){
                if(extraKey.equals("exist")){
                    boolQueryBuilder.filter(QueryBuilders.existsQuery(extraMap.get(extraKey).toString()));
                }else if (extraKey.equals("not_exist")){
                    boolQueryBuilder.filter(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(extraMap.get(extraKey).toString())));
                }else {
                    if(extraMap.get(extraKey) instanceof List){
                        boolQueryBuilder.filter(QueryBuilders.termsQuery(extraKey,(List)extraMap.get(extraKey)));
                    }else {
                        boolQueryBuilder.filter(QueryBuilders.termQuery(extraKey,extraMap.get(extraKey)));
                    }
                }
            }
        }
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(size);
        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        return hits;
    }

    public List<AggResultDto> selectByCriteria(EsCriteria esCriteria, RestHighLevelClient restHighLevelClient, String[] index, String type) throws CriteriaException {
        return elasticSearchAggregation.selectByCriteria(esCriteria,restHighLevelClient,index,type);
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
    public List<Map<String, Object>> aggsAsMaps(EsCriteria esCriteria, RestHighLevelClient restHighLevelClient, String[] index, String type) throws CriteriaException {
        return elasticSearchAggregation.aggsAsMap(esCriteria,restHighLevelClient,index,type);
    }

    public  List selectListByEsCriteria(EsCriteria esCriteria,Class clazz,RestHighLevelClient restHighLevelClient, String[] index, String type)throws CriteriaException{
        return elasticSearchAggregation.selectListByEsCriteria(esCriteria,clazz,restHighLevelClient,index,type);
    }

}
