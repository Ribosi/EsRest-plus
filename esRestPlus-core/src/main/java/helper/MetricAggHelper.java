package helper;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import template.EsCriteria;
import template.MetricFieldDto;

public class MetricAggHelper {
    public AggregationBuilder builderMetric(EsCriteria esCriteria,AggregationBuilder aggregationBuilder) {
        MetricFieldDto metricFieldDto = esCriteria.getMetricField();
        if(metricFieldDto == null){
            return aggregationBuilder;
        }
        if(aggregationBuilder!=null){
            switch (metricFieldDto.getMetricType()){
                case AVG:
                    aggregationBuilder.subAggregation(AggregationBuilders.avg(getBucket(metricFieldDto, "avg")).field(metricFieldDto.getMetricField()));
                    break;
                case SUM:
                    aggregationBuilder.subAggregation(AggregationBuilders.sum(getBucket(metricFieldDto, "sum")).field(metricFieldDto.getMetricField()));
                    break;
                case DISTINCT:
                    aggregationBuilder.subAggregation(AggregationBuilders.cardinality(getBucket(metricFieldDto, "distinct")).field(metricFieldDto.getMetricField()));
                    break;
            }
        }else {
            switch (metricFieldDto.getMetricType()){
                case AVG:
                    aggregationBuilder = AggregationBuilders.avg(getBucket(metricFieldDto, "avg")).field(metricFieldDto.getMetricField());
                    break;
                case SUM:
                    aggregationBuilder = AggregationBuilders.sum(getBucket(metricFieldDto, "sum")).field(metricFieldDto.getMetricField());
                    break;
                case DISTINCT:
                    aggregationBuilder = AggregationBuilders.cardinality(getBucket(metricFieldDto, "distinct")).field(metricFieldDto.getMetricField());
                    break;
            }
        }
        return aggregationBuilder;
    }

    private String getBucket(MetricFieldDto field, String defaultVal) {
        return field.getBucketName() == null ? defaultVal : field.getBucketName();
    }
}
