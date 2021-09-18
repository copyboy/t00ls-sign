package cn.deepj.t00ls.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSON 操作工具类
 *
 * @author qingdong.zhang
 * @version 1.0
 * @since 2021-07-21 9:29
 */
public final class Jacksons {

    private static final Logger LOG = LoggerFactory.getLogger(Jacksons.class);

    public static String writeJson(Object object,
                                   final String... ignoreFields) {
        return writeJson(object, null, ignoreFields);
    }

    public static String writeJson(Object object,
                                   PropertyNamingStrategy namingStrategy,
                                   final String... ignoreFields) {
        try {
            return createObjectMapper(namingStrategy, ignoreFields).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T readJson(String jsonString,
                                 Class<T> clazz) {
        return readJson(jsonString, null, clazz);
    }

    public static <T> T readJson(String jsonString,
                                 PropertyNamingStrategy namingStrategy,
                                 Class<T> clazz) {
        try {
            return createObjectMapper(namingStrategy).readValue(jsonString, clazz);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T readJson(String jsonString,
                                 TypeReference<T> typeRef) {
        return readJson(jsonString, null, typeRef);
    }

    public static <T> T readJson(String jsonString,
                                 PropertyNamingStrategy namingStrategy,
                                 TypeReference<T> typeRef) {
        try {
            return createObjectMapper(namingStrategy).readValue(jsonString, typeRef);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T treeToObject(JsonNode tree,
                                     Class<T> objectClass) {
        return treeToObject(tree, null, objectClass);
    }

    public static <T> T treeToObject(JsonNode tree,
                                     PropertyNamingStrategy namingStrategy,
                                     Class<T> objectClass) {
        try {
            return createObjectMapper(namingStrategy).treeToValue(tree, objectClass);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private static ObjectMapper createObjectMapper(PropertyNamingStrategy namingStrategy,
                                                   String... ignoreFields) {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        if (namingStrategy != null) {
            om.setPropertyNamingStrategy(namingStrategy);
        }
        if (null != ignoreFields) {
            om.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                private static final long serialVersionUID = 3816283136243101215L;

                public String[] findPropertiesToIgnore(Annotated ac) {
                    return ignoreFields;
                }
            });
        }
        return om;
    }
}