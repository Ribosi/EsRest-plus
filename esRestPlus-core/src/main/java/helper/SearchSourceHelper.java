package helper;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.CollectionUtils;
import template.EsCriteria;
import template.ExistFieldDto;
import template.RangeFiledDto;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class SearchSourceHelper {

    public void builderSearch(EsCriteria esCriteria, SearchSourceBuilder searchSourceBuilder) {
        if (esCriteria.isISNEEDQUERY()) {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            esCriteria.getQueryFieldMap().keySet().forEach(field -> {
                        Object val = esCriteria.getQueryFieldMap().get(field);
                        if (val != null && (Collection.class.isAssignableFrom(val.getClass()) || val.getClass().isArray())) {
                            queryBuilder.filter(QueryBuilders.termsQuery(field, (Collection) val));
                        } else {
                            queryBuilder.filter(QueryBuilders.termQuery(field, val));
                        }
                    }
            );
            esCriteria.getQueryNotEqFieldMap().keySet().forEach(notEqField->{
                Object notEqVal = esCriteria.getQueryNotEqFieldMap().get(notEqField);
                queryBuilder.mustNot(QueryBuilders.termQuery(notEqField, notEqVal));
            });
            if(esCriteria.isISNEEDRANGFILTER()){
                Map<String, RangeFiledDto> rangeMap = esCriteria.getRangeFiledDtoMap();
                esCriteria.getRangeFiledDtoMap().keySet().forEach(field -> {
                            RangeFiledDto  rangeFiledDto = rangeMap.get(field);
                            if(rangeFiledDto.isDate()){
                                queryBuilder.filter(QueryBuilders.rangeQuery(field).from(((Date)rangeFiledDto.getFrom()).getTime(), rangeFiledDto.isIncludeFrom()).to(((Date)rangeFiledDto.getTo()).getTime(), rangeFiledDto.isIncludeTo()));
                            }else {
                                queryBuilder.filter(QueryBuilders.rangeQuery(field).from(rangeFiledDto.getFrom(), rangeFiledDto.isIncludeFrom()).to(rangeFiledDto.getTo(), rangeFiledDto.isIncludeTo()));
                            }
                        }
                );
            }
            if(!CollectionUtils.isEmpty(esCriteria.getExistFields())){
                for(ExistFieldDto existFieldDto:esCriteria.getExistFields()){
                    switch (existFieldDto.getExistType()){
                        case EXIST:
                            queryBuilder.filter(QueryBuilders.existsQuery(existFieldDto.getExistField()));
                            break;
                        case NOT_EXIST:
                            queryBuilder.filter(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(existFieldDto.getExistField())));
                            break;
                    }
                }
            }
            searchSourceBuilder.query(queryBuilder);
        }
    }

}
