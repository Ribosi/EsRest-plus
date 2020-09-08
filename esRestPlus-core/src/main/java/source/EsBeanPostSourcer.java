package source;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class EsBeanPostSourcer implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"spring.EsPlusBeanPostProcessor","template.ElasicSearchServiceImpl"};
    }
}
