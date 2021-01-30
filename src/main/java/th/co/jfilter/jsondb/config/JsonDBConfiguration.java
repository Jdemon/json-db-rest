package th.co.jfilter.jsondb.config;

import io.jsondb.JsonDBTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import th.co.jfilter.jsondb.util.FileResourceUtil;
import th.co.jfilter.jsondb.util.JsonDBUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableScheduling
@ComponentScan("th.co.jfilter.jsondb")
@Slf4j
public class JsonDBConfiguration {

    @Bean
    public JsonDBTemplate jsonDBTemplate(JsonDBProperties jsonDBProperties) {
        JsonDBTemplate jsonDBTemplate = new JsonDBTemplate(jsonDBProperties.getLocation(),
                jsonDBProperties.getBasePackage());
        if (jsonDBProperties.isMigrate()) {
            List<String> collections = JsonDBUtils.getCollections(jsonDBProperties.getBasePackage());
            for (String collection : collections) {
                if (!jsonDBTemplate.collectionExists(collection)) {
                    jsonDBTemplate.createCollection(collection);
                }
            }
        }
        try {
            File[] fileList = FileResourceUtil.getResourceFolderFiles("data");
            if (fileList != null && fileList.length > 0) {
                for (File data : fileList) {
                    FileUtils.copyFileToDirectory(data, new File(jsonDBProperties.getLocation()));
                }
            }
        } catch (IOException e) {
            log.info("files not found!");
        }
        jsonDBTemplate.reLoadDB();
        return jsonDBTemplate;
    }
}