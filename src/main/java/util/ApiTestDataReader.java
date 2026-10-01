package util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import pojo.booking.Booking;

/**
 * Reads API test data from src/main/resources/testdata/apiTestData.json and maps it to POJOs.
 */
public class ApiTestDataReader {

    private static final String TEST_DATA_FILE = "testdata/apiTestData.json";
    private static JsonNode testData;

    private static synchronized JsonNode getTestData() {
        if (testData == null) {
            try (InputStream in = ApiTestDataReader.class.getClassLoader().getResourceAsStream(TEST_DATA_FILE)) {
                if (in == null) {
                    throw new RuntimeException(TEST_DATA_FILE + " not found on classpath");
                }
                testData = JsonUtil.getMapper().readTree(in);
            } catch (Exception e) {
                throw new RuntimeException("Unable to read " + TEST_DATA_FILE + ": " + e.getMessage(), e);
            }
        }
        return testData;
    }

    public static JsonNode getNode(String key) {
        JsonNode node = getTestData().get(key);
        if (node == null) {
            throw new RuntimeException("Key '" + key + "' not found in " + TEST_DATA_FILE);
        }
        return node;
    }

    public static <T> T get(String key, Class<T> type) {
        return toObject(getNode(key), type);
    }

    public static <T> T toObject(JsonNode node, Class<T> type) {
        return JsonUtil.fromJson(node, type);
    }

    public static List<JsonNode> getRows(String key) {
        List<JsonNode> rows = new ArrayList<>();
        for (JsonNode row : getNode(key)) {
            rows.add(row);
        }
        return rows;
    }

    /** First booking from the "bookings" data set - default payload for tests that just need a booking. */
    public static Booking getDefaultBooking() {
        return toObject(getRows("bookings").get(0).get("booking"), Booking.class);
    }
}
