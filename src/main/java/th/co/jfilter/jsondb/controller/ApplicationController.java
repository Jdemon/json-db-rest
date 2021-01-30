package th.co.jfilter.jsondb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.co.jfilter.jsondb.constant.JsonDBConstant;
import th.co.jfilter.jsondb.service.ApplicationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;


    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("reload")
    public ResponseEntity reload() {
        log.info("reload database");
        return ResponseEntity.ok(applicationService.reload());
    }

    @GetMapping("{collection}/reload")
    public ResponseEntity reload(@PathVariable String collection) {
        log.info("reload database collection: " + collection);
        return ResponseEntity.ok(applicationService.reload(collection));
    }

    @PostMapping("import/json")
    public ResponseEntity importJSON(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(applicationService.importData(file.getBytes()));
    }

    @GetMapping("export/json")
    public ResponseEntity exportJSON() throws JsonProcessingException {
        String jsonPretty = new ObjectMapper().writeValueAsString(applicationService.export());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(applicationService.downloadFileHeaders(
                        applicationService.getExportFileName(JsonDBConstant.EXTENSION_JSON))
                )
                .body(jsonPretty.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("{collection}/export/json")
    public ResponseEntity exportJSON(@PathVariable String collection) throws JsonProcessingException {
        String jsonPretty = new ObjectMapper().writeValueAsString(applicationService.export(collection));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(applicationService.downloadFileHeaders(
                        applicationService.getExportFileName(collection, JsonDBConstant.EXTENSION_JSON))
                )
                .body(jsonPretty.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("{collection}/export/csv")
    public ResponseEntity exportCSV(@PathVariable String collection) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(applicationService.downloadFileHeaders(
                        applicationService.getExportFileName(JsonDBConstant.EXTENSION_CSV))
                )
                .body(applicationService.exportCSV(collection));
    }
}
