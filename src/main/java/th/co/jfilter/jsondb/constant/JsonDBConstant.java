package th.co.jfilter.jsondb.constant;

import java.time.format.DateTimeFormatter;

public class JsonDBConstant {
    public static final DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    public static final DateTimeFormatter YYYYMMDDHHMMSS_SSS = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS");

    public static final String EXTENSION_CSV = ".csv";
    public static final String EXTENSION_JSON = ".json";
    public static final String STATUS = "status";
    public static final String SUCCESS = "SUCCESS";
}
