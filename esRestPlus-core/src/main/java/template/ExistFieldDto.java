package template;

import annotation.Exist;

public class ExistFieldDto {
    private ExistTypeEnum existType;
    private String existField;

    public ExistFieldDto(ExistTypeEnum exist, String fieldName) {
        this.existField=fieldName;
        this.existType=exist;
    }

    public ExistTypeEnum getExistType() {
        return existType;
    }

    public void setExistType(ExistTypeEnum existType) {
        this.existType = existType;
    }

    public String getExistField() {
        return existField;
    }

    public void setExistField(String existField) {
        this.existField = existField;
    }

    public enum  ExistTypeEnum {
        EXIST,NOT_EXIST;
    }
}
