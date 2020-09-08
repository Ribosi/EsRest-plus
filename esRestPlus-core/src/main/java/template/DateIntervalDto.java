package template;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

public class DateIntervalDto {
    private String bucketName;
    private String fieldName;
    private DateHistogramInterval dateHistogramInterval;
    private Long start;
    private Long end;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DateHistogramInterval getDateHistogramInterval() {
        return dateHistogramInterval;
    }

    public void setDateHistogramInterval(DateHistogramInterval dateHistogramInterval) {
        this.dateHistogramInterval = dateHistogramInterval;
    }
}
