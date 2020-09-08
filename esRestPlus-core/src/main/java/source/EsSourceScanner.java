package source;

import annotation.EsMappingScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import spring.EsSourceClassPathScanner;

public class EsSourceScanner implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EsMappingScan.class.getName()));
        EsSourceClassPathScanner scanner = new EsSourceClassPathScanner(beanDefinitionRegistry);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        String[] packageName = annoAttrs.getStringArray("basePackage");
        scanner.registerFilters();
        scanner.doScan(packageName);
    }
}
