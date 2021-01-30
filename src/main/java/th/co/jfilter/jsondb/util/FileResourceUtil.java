package th.co.jfilter.jsondb.util;

import java.io.File;
import java.net.URL;

public class FileResourceUtil {
    public static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }
}
