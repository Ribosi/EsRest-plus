package template;

import enums.MetricTypeEnum;

public class MetricFieldDto {
    private String bucketName;
    private String metricField;
    private MetricTypeEnum metricType;

    public MetricFieldDto(String bucketName, String metricField,MetricTypeEnum typeEnum){
        this.bucketName = bucketName;
        this.metricField = metricField;
        this.metricType = typeEnum;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getMetricField() {
        return metricField;
    }

    public void setMetricField(String metricField) {
        this.metricField = metricField;
    }

    public MetricTypeEnum getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricTypeEnum metricType) {
        this.metricType = metricType;
    }
}
