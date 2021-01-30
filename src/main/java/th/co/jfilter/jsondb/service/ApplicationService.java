package th.co.jfilter.jsondb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.opendevl.JFlat;
import io.jsondb.JsonDBTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import th.co.jfilter.jsondb.config.JsonDBProperties;
import th.co.jfilter.jsondb.constant.JsonDBConstant;
import th.co.jfilter.jsondb.util.JsonDBUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ApplicationService {

    private ObjectMapper mapper = new ObjectMapper();

    private final JsonDBTemplate jsonDBTemplate;

    private final JsonDBProperties jsonDBProperties;

    public ApplicationService(JsonDBTemplate jsonDBTemplate, JsonDBProperties jsonDBProperties) {
        this.jsonDBTemplate = jsonDBTemplate;
        this.jsonDBProperties = jsonDBProperties;
    }

    public ObjectNode reload() {
        this.jsonDBTemplate.reLoadDB();
        return new ObjectMapper().createObjectNode().put(JsonDBConstant.STATUS, JsonDBConstant.SUCCESS);
    }

    public ObjectNode reload(String collection) {
        this.jsonDBTemplate.reloadCollection(collection);
        return new ObjectMapper().createObjectNode().put(JsonDBConstant.STATUS, JsonDBConstant.SUCCESS);
    }

    public ObjectNode export(String collection) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.set(collection, mapper.valueToTree(jsonDBTemplate.findAll(collection)));
        return objectNode;
    }

    public ObjectNode export() {
        ObjectNode objectNode = mapper.createObjectNode();
        for (String collection : JsonDBUtils.getCollections(jsonDBProperties.getBasePackage())) {
            objectNode.set(collection, mapper.valueToTree(jsonDBTemplate.findAll(collection)));
        }
        return objectNode;
    }

    public byte[] exportCSV(String collection) throws Exception {
        return convertJSONToCSV(mapper.valueToTree(jsonDBTemplate.findAll(collection)));
    }

    private byte[] convertJSONToCSV(JsonNode collectionNodes) throws Exception {
        try {
            String tempPath = "./temp_" + LocalDateTime.now().format(JsonDBConstant.YYYYMMDDHHMMSS_SSS) + ".csv";
            JFlat flatMe = new JFlat(mapper.writeValueAsString(collectionNodes));
            flatMe.json2Sheet().headerSeparator("_").write2csv(tempPath);
            File tempCSV = new File(tempPath);
            byte[] data = FileUtils.readFileToByteArray(tempCSV);
            FileUtils.deleteQuietly(tempCSV);
            return data;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    public ObjectNode importData(byte[] data) throws IOException {

        JsonNode json = mapper.readValue(data, JsonNode.class);

        JsonNode backupNodes = export();

        try {
            importJsonNode(json);
        } catch (Throwable e) {
            log.info(ExceptionUtils.getStackTrace(e));
            importJsonNode(backupNodes);
        }

        return export();
    }

    public HttpHeaders downloadFileHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        return headers;
    }

    public String getExportFileName(String extension) {
        return getExportFileName(StringUtils.EMPTY, extension);
    }

    public String getExportFileName(String collection, String extension) {
        StringBuilder fileName = new StringBuilder("export_");
        if (StringUtils.isNotBlank(collection)) {
            fileName.append(collection)
                    .append("_");
        }
        fileName.append(LocalDateTime.now().format(JsonDBConstant.YYYYMMDDHHMMSS))
                .append(extension);
        return fileName.toString();
    }

    private void importJsonNode(JsonNode json) throws JsonProcessingException {
        for (String collection : JsonDBUtils.getCollections(jsonDBProperties.getBasePackage())) {
            if (!json.hasNonNull(collection)) {
                continue;
            }
            JsonNode collectionNodes = json.findValue(collection);
            jsonDBTemplate.dropCollection(collection);
            jsonDBTemplate.createCollection(collection);
            for (JsonNode collectionData : collectionNodes) {
                log.info("import - " + collection + ": " + mapper.writeValueAsString(collectionData));
                jsonDBTemplate.insert(mapper.convertValue(collectionData, JsonDBUtils.getClassCollection(collection)), collection);
            }

        }
    }

}
