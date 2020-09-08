package template;

import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class BaseElasticSearchService<T> {

    public String[] index;

    public String type;

    public RestHighLevelClient restHighLevelClient = null;

    public abstract List<T> selectByCondition(T t);

    public abstract List<T> selectListByEsCriteria(EsCriteria esCriteria,Class clazz);

    public abstract List<T> selectByConditionWithExtraMap(T t,Map<String,Object> extraMap);

    public abstract long selectCountByCondition(T t, Set<String> keywords);

    public abstract long selectCountByConditionAndExtra(T t, Set<String> keywords,Map<String,Object> extraMap);

    public abstract List selectByConditionWithKeyword(Object o,Set<String> keywords);

    public  abstract List selectByConditionWithExtraMapAndKeywords(Object t,Set<String> keywords,Map<String,Object> extraMap);

    public abstract void insert(T t);

    public abstract Map<String,T> selectIdsAndLists(T t);

    public abstract List<AggResultDto> selectByCriteria(EsCriteria esCriteria);

    public abstract void update(T t, String id);

    public abstract List<Map<String, Object>> aggsAsMaps(EsCriteria esCriteria);

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getIndex() {
        return index;
    }

    public void setIndex(String... index) {
        this.index = index;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }
}
