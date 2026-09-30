package util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.restassured.response.Response;

/**
 * Single Jackson ObjectMapper for the framework - serialises request POJOs to JSON and
 * deserialises response JSON / test data into POJOs.
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /** POJO / Map / JsonNode -> JSON string (request payload). */
    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Unable to serialise " + object.getClass().getSimpleName() + " to JSON: " + e.getMessage(), e);
        }
    }

    /** JSON string -> POJO. */
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Unable to deserialise JSON to " + type.getSimpleName() + ": " + e.getMessage()
                    + "\nJSON: " + json, e);
        }
    }

    /** Response body -> POJO. */
    public static <T> T fromJson(Response response, Class<T> type) {
        return fromJson(response.asString(), type);
    }

    /** JsonNode (e.g. from a test data file) -> POJO. */
    public static <T> T fromJson(JsonNode node, Class<T> type) {
        try {
            return MAPPER.treeToValue(node, type);
        } catch (Exception e) {
            throw new RuntimeException("Unable to map JSON to " + type.getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}
