package th.co.jfilter.jsondb.util;

import io.jsondb.annotation.Document;
import org.reflections.Reflections;

import java.util.*;

public class JsonDBUtils {

    private static Map<String, Class<?>> collectionsMap;

    private static void init(String jsonBasePackage) {
        if (collectionsMap != null) {
            return;
        }
        JsonDBUtils.collectionsMap = new LinkedHashMap<>();
        Reflections reflections = new Reflections(jsonBasePackage);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Document.class);

        for (Class<?> documents : annotated) {
            Document document = documents.getAnnotation(Document.class);
            JsonDBUtils.collectionsMap.put(document.collection(), documents);
        }
    }

    public static List<String> getCollections(String jsonBasePackage) {
        List<String> collections = new ArrayList<>();
        init(jsonBasePackage);
        for (Map.Entry<String, Class<?>> entry : collectionsMap.entrySet()) {
            collections.add(entry.getKey());
        }
        return collections;
    }

    public static List<Class<?>> getClassCollections(String jsonBasePackage) {
        List<Class<?>> collectionClasses = new ArrayList<>();
        init(jsonBasePackage);
        for (Map.Entry<String, Class<?>> entry : collectionsMap.entrySet()) {
            collectionClasses.add(entry.getValue());
        }
        return collectionClasses;
    }

    public static Class<?> getClassCollection(String collection) {
        return collectionsMap.get(collection);
    }
}
