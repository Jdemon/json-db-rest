package th.co.jfilter.jsondb.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.co.jfilter.jsondb.model.JsonDBModel;
import th.co.jfilter.jsondb.service.JsonDBService;

import java.util.List;
import java.util.Map;

@RestController
public abstract class JsonDBController<T extends JsonDBModel> {

    protected final JsonDBService<T> service;

    public JsonDBController(JsonDBService<T> service) {
        this.service = service;
    }

    @GetMapping
    public List<T> findAll(@RequestParam(required = false) Map<String, String> params) {
        return service.findAll(params);
    }

    @GetMapping("{id}")
    public T findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping("search")
    public List<T> search(@RequestBody String query) {
        return service.search(query);
    }

    @PostMapping
    public T insert(@RequestBody T t) {
        return service.insert(t);
    }

    @PutMapping("{id}")
    public T update(@PathVariable String id, @RequestBody T t) {
        return service.update(id, t);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pagination")
    public ObjectNode pagination(@RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                 @RequestParam(name = "size", defaultValue = "1000", required = false) Integer size,
                                 @RequestParam(required = false) Map<String, String> params) {
        return service.findPagination(params, page, size);
    }
}
