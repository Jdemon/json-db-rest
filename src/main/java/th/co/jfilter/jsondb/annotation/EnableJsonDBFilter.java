package th.co.jfilter.jsondb.annotation;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Import;
import th.co.jfilter.jsondb.config.JsonDBConfiguration;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import({JsonDBConfiguration.class})
public @interface EnableJsonDBFilter {
}
