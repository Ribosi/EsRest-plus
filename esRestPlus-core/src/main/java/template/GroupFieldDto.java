package template;

public class GroupFieldDto {
    private String bucketName;
    private String fieldName;
    private Object missingValue;

    public GroupFieldDto(String bucketName, String fieldName){
        this(bucketName, fieldName, null);
    }
    public GroupFieldDto(String bucketName, String fieldName, Object missingValue){
        this.bucketName = bucketName;
        this.fieldName = fieldName;
        this.missingValue = missingValue;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getMissingValue() {
        return missingValue;
    }

    public void setMissingValue(Object missingValue) {
        this.missingValue = missingValue;
    }
}
