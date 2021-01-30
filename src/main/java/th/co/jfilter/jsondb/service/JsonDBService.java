package th.co.jfilter.jsondb.service;

import io.jsondb.JsonDBTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.jfilter.jsondb.model.JsonDBModel;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public abstract class JsonDBService<T extends JsonDBModel> {

    private Class<T> clazz;

    @Autowired
    protected JsonDBTemplate jsonDBTemplate;

    public JsonDBService() {
        this.clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public List<T> findAll(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return jsonDBTemplate.findAll(clazz);
        }
        return jsonDBTemplate.find(generateJxQuery(params), clazz);
    }

    public T findById(String id) {
        return jsonDBTemplate.findById(id, clazz);
    }

    public String generateJxQuery(Map<String, String> params) {

        if (params == null || params.isEmpty()) {
            return "/.";
        }

        boolean isMulti = false;
        StringBuilder jxQueryBuilder = new StringBuilder("/.[");
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (isMulti) {
                jxQueryBuilder.append(" and ");
            }
            jxQueryBuilder.append(param.getKey())
                    .append(String.format("='%s'", param.getValue()));

            isMulti = true;
        }
        jxQueryBuilder.append("]");
        log.info("XPath: " + jxQueryBuilder.toString());
        return jxQueryBuilder.toString();
    }

    public List<T> search(String query) {
        return jsonDBTemplate.find(query, clazz);
    }

    public T insert(T t) {
        int index = jsonDBTemplate.findAll(clazz).size();
        t.setNo(++index);
        jsonDBTemplate.insert(t);
        return t;
    }

    public T update(String id, T t) {
        T data = jsonDBTemplate.findById(id, clazz);
        BeanUtils.copyProperties(t, data,"no");
        jsonDBTemplate.upsert(data);
        return jsonDBTemplate.findById(id, clazz);
    }

    public void delete(String id) {
        String jxQuery = String.format("/.[id='%s']", id);
        jsonDBTemplate.findAllAndRemove(jxQuery, clazz);
    }
}
