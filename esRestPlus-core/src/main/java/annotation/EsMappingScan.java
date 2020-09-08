package annotation;


import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import source.EsBeanPostSourcer;
import source.EsSourceScanner;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EsSourceScanner.class,EsBeanPostSourcer.class})
public @interface EsMappingScan {
    String basePackage();

    Class<?> markerInterface() default Class.class;

    Class<? extends Annotation> annotationClass() default Annotation.class;

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
}
