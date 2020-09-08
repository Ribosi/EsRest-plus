package spring;

import annotation.EsMapping;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;


public class EsServiceFactoryBean<T> implements FactoryBean<T> {

    private Class<T> esServiceInterface;

    public EsServiceFactoryBean(Class<T> esServiceInterface){
        this.esServiceInterface=esServiceInterface;
    }

    @Override
    public T getObject() throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(esServiceInterface);
        enhancer.setCallback(new EsServiceProxy());
        T t = (T) enhancer.create();
        EsMapping esMapping =  esServiceInterface.getAnnotation(EsMapping.class);
        EsServiceProxy.EsMapperMap.put(esServiceInterface.getSimpleName().toLowerCase(),esMapping);
        return t;
    }

    public Class<?> getObjectType() {
        return EsServiceProxy.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
