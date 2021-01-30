package th.co.jfilter.jsondb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConfigurationProperties(prefix = "json-db.models")
@Data
public class JsonDBProperties {
    private String location;
    private String basePackage;
    private boolean isMigrate = true;

    public void setLocation(@DefaultValue("./jsondb") String location) {
        this.location = location;
    }

    public void setBasePackage(@DefaultValue("th.co.jfilter.app.model") String basePackage) {
        this.basePackage = basePackage;
    }
}
