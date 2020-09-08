package spring;

import annotation.EsMapping;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import template.BaseElasticSearchService;

import javax.annotation.Resource;


public class EsPlusBeanPostProcessor implements BeanPostProcessor{

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if(o instanceof BaseElasticSearchService){
            ((BaseElasticSearchService) o).setRestHighLevelClient(restHighLevelClient);
            EsMapping esMapping = EsServiceProxy.EsMapperMap.get(s.toLowerCase());
            ((BaseElasticSearchService) o).setIndex(esMapping.index());
            ((BaseElasticSearchService) o).setType(esMapping.type());
        }
        return o;
    }
}
