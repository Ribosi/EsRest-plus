package template;

import enums.MetricTypeEnum;

import java.util.*;

public class EsCriteria {

    private boolean ISNEEDQUERY = false;
    private boolean ISNEEDAGG = false;
    private boolean ISNEEDRANGFILTER = false;

    private Map<String,Object> queryFieldMap = new HashMap<>();
    private Map<String,Object> queryNotEqFieldMap = new HashMap<>();
    private Map<String, template.RangeFiledDto> rangeFiledDtoMap = new HashMap<>();
    private DateIntervalDto dateIntervalDto;
    private List<GroupFieldDto> groupByField = new ArrayList<>();
    private MetricFieldDto metricField;
    private List<ExistFieldDto> existFields = new ArrayList<>();
    private String dictinctQueryField;
    private long AGGLINK ;
    private int order = 0;

    public Map<String, Object> getQueryNotEqFieldMap() {
        return queryNotEqFieldMap;
    }

    public void setQueryNotEqFieldMap(Map<String, Object> queryNotEqFieldMap) {
        this.queryNotEqFieldMap = queryNotEqFieldMap;
    }

    public List<ExistFieldDto> getExistFields() {
        return existFields;
    }

    public void setExistFields(List<ExistFieldDto> existFields) {
        this.existFields = existFields;
    }

    public DateIntervalDto getDateIntervalDto() {
        return dateIntervalDto;
    }

    public void setDateIntervalDto(DateIntervalDto dateIntervalDto) {
        this.dateIntervalDto = dateIntervalDto;
    }

    public boolean isISNEEDQUERY() {
        return ISNEEDQUERY;
    }

    public boolean isISNEEDRANGFILTER() {
        return ISNEEDRANGFILTER;
    }

    public void setISNEEDRANGFILTER(boolean ISNEEDRANGFILTER) {
        this.ISNEEDRANGFILTER = ISNEEDRANGFILTER;
    }

    public void setISNEEDQUERY(boolean ISNEEDQUERY) {
        this.ISNEEDQUERY = ISNEEDQUERY;
    }

    public boolean isISNEEDAGG() {
        return ISNEEDAGG;
    }

    public void setISNEEDAGG(boolean ISNEEDAGG) {
        this.ISNEEDAGG = ISNEEDAGG;
    }

    public Map<String, Object> getQueryFieldMap() {
        return queryFieldMap;
    }

    public void setQueryFieldMap(Map<String, Object> queryFieldMap) {
        this.queryFieldMap = queryFieldMap;
    }

    public Map<String, RangeFiledDto> getRangeFiledDtoMap() {
        return rangeFiledDtoMap;
    }

    public void setRangeFiledDtoMap(Map<String, RangeFiledDto> rangeFiledDtoMap) {
        this.rangeFiledDtoMap = rangeFiledDtoMap;
    }

    public MetricFieldDto getMetricField() {
        return metricField;
    }

    public void setMetricField(MetricFieldDto metricField) {
        this.metricField = metricField;
    }

    public List<GroupFieldDto> getGroupByField() {
        return groupByField;
    }

    public void setGroupByField(List<GroupFieldDto> groupByField) {
        this.groupByField = groupByField;
    }

    public String getDictinctQueryField() {
        return dictinctQueryField;
    }

    public void setDictinctQueryField(String dictinctQueryField) {
        this.dictinctQueryField = dictinctQueryField;
    }

    public long getAGGLINK() {
        return AGGLINK;
    }

    public void setAGGLINK(long AGGLINK) {
        this.AGGLINK = AGGLINK;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public EsCriteria where(){
        ISNEEDQUERY = true;
        return this;
    }

    public EsCriteria eq(String fieldName, Object value){
        queryFieldMap.put(fieldName,value);
        return this;
    }

    public EsCriteria notEq(String fieldName,Object value){
        queryNotEqFieldMap.put(fieldName,value);
        return this;
    }

    public EsCriteria in(String fieldName, Object... values){
        queryFieldMap.put(fieldName,values);
        return this;
    }

    public EsCriteria in(String fieldName, List<Object> values){
        queryFieldMap.put(fieldName,values);
        return this;
    }

    public EsCriteria rangeFilter(String fieldName, Object from, Object to) {
        return rangeFilter(fieldName, from, to, true, true);
    }

    public EsCriteria rangeFilter(String fieldName, Object from, Object to, boolean includeFrom, boolean includeTo) {
        rangeFiledDtoMap.put(fieldName, new RangeFiledDto(from, to, includeFrom, includeTo));
        ISNEEDRANGFILTER = true;
        return this;
    }

    public void distinct(String field){
        distinct(null, field);
    }

    public void distinct(String bucket, String field){
        metricField = new MetricFieldDto(bucket, field, MetricTypeEnum.DISTINCT) ;
        jointLink(0);
    }

    public void distinctQuery(String field){
        dictinctQueryField = field ;
    }

    public void sum(String field){
        sum(null, field);
    }

    public void sum(String bucket, String field){
        metricField = new MetricFieldDto(bucket, field, MetricTypeEnum.SUM) ;
        jointLink(0);
    }

    public void avg(String field){
        avg(null, field);
    }

    public void avg(String bucket, String field){
        metricField = new MetricFieldDto(bucket, field, MetricTypeEnum.AVG) ;
        jointLink(0);
    }

    public EsCriteria groupBy(String fieldName){
        return groupBy(fieldName, null);
    }

    public EsCriteria groupBy(String fieldName, Object missingVal){
        return groupBy(null, fieldName, missingVal);
    }

    public EsCriteria groupBy(String bucket, String fieldName, Object missingVal){
        ISNEEDAGG = true;
        groupByField.add(new GroupFieldDto(bucket, fieldName, missingVal));
        jointLink(1);
        return this;
    }

    public EsCriteria exist(String fieldName){
        existFields.add(new ExistFieldDto(ExistFieldDto.ExistTypeEnum.EXIST,fieldName));
        return this;
    }

    public EsCriteria notExist(String fieldName){
        existFields.add(new ExistFieldDto(ExistFieldDto.ExistTypeEnum.NOT_EXIST,fieldName));
        return this;
    }

    public void  groupByDateInterval(DateIntervalDto dateIntervalDto){
        ISNEEDAGG = true;
        this.dateIntervalDto = dateIntervalDto;
    }

    private void jointLink(int type){
        if(type == 1){
            AGGLINK = OrderConst.ORDER_0.order | OrderConst.fromIndex(10+(++order)).order;
        }else {
            AGGLINK = OrderConst.ORDER_0.order & OrderConst.fromIndex(++order).order;
        }
    }

    public boolean checkCorrectAgg(){
        return OrderConst.checkCorrectAgg(this.getAGGLINK());
    }

    private enum  OrderConst{
        ORDER_0 (0,0b11111111),
        ORDER_1 (1,0b00000000),
        ORDER_2 (2,0b10000000),
       ORDER_3 (3,0b11000000),
        ORDER_4 (4,0b11100000),
        ORDER_5 (5,0b11110000),
        ORDER_6 (6, 0b11111000),
        ORDER_7 (7, 0b11111100),
        ORDER_8 (8, 0b11111110),
        GROUP_ORDER_1 (11,0b10000000),
        GROUP_ORDER_2 (12,0b01000000),
        GROUP_ORDER_3 (13, 0b00100000),
        GROUP_ORDER_4 (14, 0b00010000),
        GROUP_ORDER_5 (15, 0b00001000),
        GROUP_ORDER_6 (16,0b00000100),
        GROUP_ORDER_7 (17, 0b00000010),
        GROUP_ORDER_8 (18, 0b00000001);
        private int index;
        private long order;
        OrderConst(int index,long order){
            this.index = index;
            this.order = order;
        }
        public static OrderConst fromIndex(int index){
            return  Arrays.stream(OrderConst.values()).filter(orderConst->orderConst.index==index).findFirst().orElse(null);
        }

        public static  boolean checkCorrectAgg(long order){
            return ORDER_0.order == order || ORDER_1.order == order || ORDER_2.order == order || ORDER_3.order == order
                    || ORDER_4.order == order || ORDER_5.order == order || ORDER_6.order == order || ORDER_7.order == order || ORDER_8.order == order;
        }
    }
}
