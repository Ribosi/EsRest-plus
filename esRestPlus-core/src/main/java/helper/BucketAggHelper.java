package helper;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.util.StringUtils;
import template.DateIntervalDto;
import template.EsCriteria;
import template.GroupFieldDto;

public class BucketAggHelper {
    public Pair<AggregationBuilder,AggregationBuilder> builderBucket(EsCriteria esCriteria) {
        AggregationBuilder aggregationBuilder = null;
        if (esCriteria.getDateIntervalDto() != null) {
            DateIntervalDto dateIntervalDto = esCriteria.getDateIntervalDto();
            String bucket = dateIntervalDto.getBucketName() == null ? "agg" : dateIntervalDto.getBucketName();
            aggregationBuilder = AggregationBuilders.dateHistogram(bucket).field(dateIntervalDto.getFieldName())
                    .dateHistogramInterval(dateIntervalDto.getDateHistogramInterval()).minDocCount(0).extendedBounds(new ExtendedBounds(dateIntervalDto.getStart(), dateIntervalDto.getEnd()));
            Pair<AggregationBuilder, AggregationBuilder> aggregationBuilderPair = Pair.of(aggregationBuilder, aggregationBuilder);
            return aggregationBuilderPair;
        } else {
            Pair<AggregationBuilder, AggregationBuilder> aggregationBuilderPair = MutablePair.of(null, null);
            if (esCriteria.isISNEEDAGG()) {
                for (int i = 0; i < esCriteria.getGroupByField().size(); i++) {
                    GroupFieldDto groupField = esCriteria.getGroupByField().get(i);
                    String bucket = groupField.getBucketName() == null ? "agg" + i : groupField.getBucketName();
                    if (aggregationBuilder == null) {
                        aggregationBuilder = AggregationBuilders.terms(bucket).field(groupField.getFieldName()).size(Integer.MAX_VALUE).shardSize(Integer.MAX_VALUE);
                        if(groupField.getMissingValue() != null) {
                            ((TermsAggregationBuilder) aggregationBuilder).missing(groupField.getMissingValue());
                        }
                        aggregationBuilderPair = MutablePair.of(aggregationBuilder, aggregationBuilder);
                    } else {
                        AggregationBuilder subAgg = AggregationBuilders.terms(bucket).field(groupField.getFieldName()).size(Integer.MAX_VALUE).shardSize(Integer.MAX_VALUE);
                        if(groupField.getMissingValue() != null) {
                            ((TermsAggregationBuilder) subAgg).missing(groupField.getMissingValue());
                        }
                        aggregationBuilder.subAggregation(subAgg);
                        aggregationBuilderPair.setValue(subAgg);
                        aggregationBuilder = subAgg;
                    }
                }
            }
            return aggregationBuilderPair;
        }
    }


    public DateHistogramInterval aggType(String aggType) {
        if(StringUtils.isEmpty(aggType)){
            return DateHistogramInterval.HOUR;
        }
        switch (aggType){
            case "second":
                return DateHistogramInterval.SECOND;
            case "minute":
                return DateHistogramInterval.MINUTE;
            case "hour":
                return DateHistogramInterval.HOUR;
            case "day":
                return DateHistogramInterval.DAY;
            case "week":
                return DateHistogramInterval.WEEK;
            case "quarter":
                return DateHistogramInterval.QUARTER;
            case "month":
                return DateHistogramInterval.MONTH;
            default:
                break;
        }
        return DateHistogramInterval.HOUR;
    }
}
