package template;

import java.util.List;

public class AggResultDto {
    private String key;
    private Double value;
    private Long count;
    private List<AggResultDto> bucketAggDto;

    public AggResultDto(){}

    public AggResultDto(String key,Long count){
        this.key=key;
        this.count=count;
    }

    public AggResultDto(String key,Double value){
        this.key=key;
        this.value=value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<AggResultDto> getBucketAggDto() {
        return bucketAggDto;
    }

    public void setBucketAggDto(List<AggResultDto> bucketAggDto) {
        this.bucketAggDto = bucketAggDto;
    }

}
