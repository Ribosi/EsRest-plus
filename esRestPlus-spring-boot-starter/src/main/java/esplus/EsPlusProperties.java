package esplus;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = EsPlusProperties.ES_PLUS_PREFIX)
public class EsPlusProperties {
    public static final String ES_PLUS_PREFIX = "es-plus";

    private Integer MAX_CONN_TOTAL;

    private Integer MAX_CONN_PER_ROUTE;

    public Integer getMAX_CONN_TOTAL() {
        return MAX_CONN_TOTAL;
    }

    public void setMAX_CONN_TOTAL(Integer MAX_CONN_TOTAL) {
        this.MAX_CONN_TOTAL = MAX_CONN_TOTAL;
    }

    public Integer getMAX_CONN_PER_ROUTE() {
        return MAX_CONN_PER_ROUTE;
    }

    public void setMAX_CONN_PER_ROUTE(Integer MAX_CONN_PER_ROUTE) {
        this.MAX_CONN_PER_ROUTE = MAX_CONN_PER_ROUTE;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    private String[] nodes;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    private String scheme;
}
