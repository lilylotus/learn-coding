package cn.nihility.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JacksonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    private JacksonUtil() {
    }

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE = "yyyy-MM-dd";
    public static final String TIME = "HH:mm:ss";

    private static final ObjectMapper JSON_MAPPER;
    private static final JavaType OBJECT_MAP_TYPE;
    private static final JavaType MAP_LIST_TYPE;

    static {
        JSON_MAPPER = newObjectMapperInstance();
        OBJECT_MAP_TYPE = getJavaType(Map.class, String.class, Object.class);
        MAP_LIST_TYPE = getJavaType(List.class, OBJECT_MAP_TYPE);
    }

    public static ObjectMapper newObjectMapperInstance() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME);
        JavaTimeModule timeModule = new JavaTimeModule();

        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME)));
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE)));
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME)));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE)));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME)));
        mapper.setDateFormat(dateFormat);
        mapper.registerModule(timeModule);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
        mapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        mapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

        return mapper;
    }

    /**
     * 转为 Json字符串
     *
     * @param o 目标对象
     * @return Json字符串
     */
    public static String toJsonString(Object o) {
        try {
            return (null == o ? null : JSON_MAPPER.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            logger.error("对象 [{}] 转 JSON 字符异常", o, e);
        }
        return null;
    }

    /**
     * 转为指定对象
     *
     * @param object 目标对象
     * @param clazz  转换类型
     * @param <T>    转换类型
     * @return 转换类型对象
     */
    public static <T> T toJavaObject(Object object, Class<T> clazz) {
        return JSON_MAPPER.convertValue(object, clazz);
    }

    /**
     * 转为指定对象
     *
     * @param object   目标对象
     * @param javaType 转换类型
     * @param <T>      转换类型
     * @return 转换类型对象
     */
    public static <T> T toJavaObject(Object object, JavaType javaType) {
        return JSON_MAPPER.convertValue(object, javaType);
    }

    /**
     * 转为 泛型集合
     *
     * @param o         目标对象
     * @param listClass 集合类型
     * @param <T>       集合类型
     * @return 泛型集合
     */
    public static <T> List<T> toJavaList(Object o, Class<T> listClass) {
        JavaType javaType = getJavaType(ArrayList.class, listClass);
        return JSON_MAPPER.convertValue(o, javaType);
    }

    /**
     * 转为 泛型集合
     *
     * @param o 目标对象
     * @return 泛型集合
     */
    public static List<Map<String, Object>> toMapList(Object o) {
        return JSON_MAPPER.convertValue(o, MAP_LIST_TYPE);
    }

    /**
     * 转为 Map&lt;String,Object&gt;对象
     *
     * @param o 目标对象
     * @return Map对象
     */
    public static Map<String, Object> toMap(Object o) {
        return JSON_MAPPER.convertValue(o, OBJECT_MAP_TYPE);
    }

    /**
     * 转为 Map&lt;String,Object&gt;对象
     *
     * @param jsonString jason字符串
     * @return Map对象
     */
    public static Map<String, Object> toMap(String jsonString) throws JsonProcessingException {
        return JSON_MAPPER.readValue(jsonString, OBJECT_MAP_TYPE);
    }

    /**
     * 转为 List
     *
     * @param o 目标对象
     * @return List对象
     */
    public static List<Object> toJavaList(Object o) {
        return toJavaList(o, Object.class);
    }

    /**
     * 转换
     *
     * @param o        原始对象
     * @param javaType 类型
     * @param <T>      结果类型
     */
    public static <T> T convert(Object o, JavaType javaType) {
        return JSON_MAPPER.convertValue(o, javaType);
    }

    /**
     * 类型
     *
     * @param collectionClass 主类型类型
     * @param elementClasses  泛型型
     * @return javaType对象
     */
    public static JavaType getJavaType(Class<?> collectionClass, Class<?>... elementClasses) {
        return JSON_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 类型
     *
     * @param collectionClass  主类型类型
     * @param elementJavaTypes 泛型型
     * @return javaType对象
     */
    public static JavaType getJavaType(Class<?> collectionClass, JavaType... elementJavaTypes) {
        return JSON_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementJavaTypes);
    }

    /**
     * 获取JavaType
     */
    public static JavaType getJavaType(Class<?> clazz) {
        return JSON_MAPPER.getTypeFactory().constructType(clazz);
    }

    public static <T> T readJsonString(String jsonString, Class<T> clazz) {
        try {
            return JSON_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            logger.error("解析 JSON 字符串 {} 转对象 {} 异常",
                jsonString, clazz.getName(), e);
        }
        return null;
    }

    /**
     * jsonString 读取为java对象
     *
     * @param jsonString jsonString
     * @param javaType   目标类型
     * @param <T>        目标类型
     * @return 目标对象
     */
    public static <T> T readJsonString(String jsonString, JavaType javaType) {
        try {
            return JSON_MAPPER.readValue(jsonString, javaType);
        } catch (JsonProcessingException e) {
            logger.error("解析 JSON 字符串 {} 转对象 {} 异常",
                jsonString, javaType.getRawClass().getName(), e);
        }
        return null;
    }

    /**
     * jsonString 读取为java对象
     *
     * @param jsonString    jsonString
     * @param typeReference 目标类型
     * @param <T>           目标类型
     * @return 目标对象
     */
    public static <T> T readJsonString(String jsonString, TypeReference<T> typeReference) {
        try {
            return JSON_MAPPER.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("解析 JSON 字符串 {} 转对象异常", jsonString, e);
        }
        return null;
    }


}
