package spring;


import annotation.EsMapping;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import template.BaseElasticSearchService;
import template.ElasicSearchServiceImpl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class EsServiceProxy implements MethodInterceptor {

    public static ConcurrentHashMap<Method,Method> TEMPLATE_METHODS = new ConcurrentHashMap<>();

    private static ElasicSearchServiceImpl elasicSearchService = new ElasicSearchServiceImpl();

    public static ConcurrentHashMap<Object, EsMapping> EsMapperMap = new ConcurrentHashMap<Object, EsMapping>();

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(TEMPLATE_METHODS.containsKey(method)){
            EsMapping esMapping = EsMapperMap.get(o.getClass().getSimpleName().split("\\$\\$")[0].toLowerCase());
            BaseElasticSearchService baseElasticSearchService = (BaseElasticSearchService) o;
            Object[] newObjects = new Object[objects.length+3];
            for(int i=0;i<objects.length;i++){
                newObjects[i] = objects[i];
            }
            newObjects[objects.length] = baseElasticSearchService.restHighLevelClient;
            newObjects[objects.length+1] = esMapping.index();
            newObjects[objects.length+2] = esMapping.type();
            return TEMPLATE_METHODS.get(method).invoke(elasicSearchService,newObjects);
        }
        return  methodProxy.invokeSuper(o,objects);
    }

}
