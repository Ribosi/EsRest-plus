package esplus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties(EsPlusProperties.class)
@AutoConfigureAfter(EsPlusProperties.class)
public class EsPlusAutoConfiguration {

    private static final Log logger = LogFactory.getLog(EsPlusAutoConfiguration.class);

    private final EsPlusProperties esPlusProperties;

    public EsPlusAutoConfiguration(EsPlusProperties esPlusProperties){
        this.esPlusProperties = esPlusProperties;
    }

    @Bean("restClientBuilder")
    public RestClientBuilder RestClientBuilder() {
        HttpHost[] hosts = Arrays.stream(esPlusProperties.getNodes()).map(this::makeHttpHost)
                .filter(Objects::nonNull).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(hosts);

        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(esPlusProperties.getMAX_CONN_TOTAL()!=null?esPlusProperties.getMAX_CONN_TOTAL():RestClientBuilder.DEFAULT_MAX_CONN_TOTAL);
            httpClientBuilder.setMaxConnPerRoute(esPlusProperties.getMAX_CONN_PER_ROUTE()!=null?esPlusProperties.getMAX_CONN_PER_ROUTE():RestClientBuilder.DEFAULT_MAX_CONN_PER_ROUTE);
            return httpClientBuilder;
        });

        return builder;
    }

    @Bean
    public RestHighLevelClient highLevelClient(@Autowired @Qualifier("restClientBuilder") RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

    private HttpHost makeHttpHost(String s) {
        if(null==s || "".equals(s)){
            logger.error("elasticSearch host is null!");
        }
        String[] address = s.split(":");
        if (address.length == 2) {
            return new HttpHost(address[0], Integer.parseInt(address[1]), esPlusProperties.getScheme());
        } else {
            return null;
        }
    }
}
