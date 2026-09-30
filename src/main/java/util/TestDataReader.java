package util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pojo.CustomerDetails;

/**
 * Reads test data from src/main/resources/testdata/testData.json (loaded once from the classpath).
 */
public class TestDataReader {

    private static final String TEST_DATA_FILE = "testdata/testData.json";
    private static JSONObject testData;

    private static synchronized JSONObject getTestData() {
        if (testData == null) {
            try (InputStream in = TestDataReader.class.getClassLoader().getResourceAsStream(TEST_DATA_FILE)) {
                if (in == null) {
                    throw new RuntimeException(TEST_DATA_FILE + " not found on classpath");
                }
                testData = (JSONObject) new JSONParser().parse(new InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Unable to read test data file " + TEST_DATA_FILE + ": " + e.getMessage(), e);
            }
        }
        return testData;
    }

    private static JSONObject getSection(String section) {
        Object value = getTestData().get(section);
        if (value == null) {
            throw new RuntimeException("Section '" + section + "' not found in " + TEST_DATA_FILE);
        }
        return (JSONObject) value;
    }

    public static String getValue(String section, String key) {
        Object value = getSection(section).get(key);
        if (value == null) {
            throw new RuntimeException("Key '" + section + "." + key + "' not found in " + TEST_DATA_FILE);
        }
        return value.toString();
    }

    public static CustomerDetails getCustomer(String key) {
        JSONObject customer = (JSONObject) getSection("customer").get(key);
        if (customer == null) {
            throw new RuntimeException("Customer '" + key + "' not found in " + TEST_DATA_FILE);
        }
        return toCustomer(customer);
    }

    public static CustomerDetails toCustomer(JSONObject json) {
        return new CustomerDetails((String) json.get("firstName"), (String) json.get("lastName"),
                (String) json.get("postalCode"));
    }

    public static List<String> getProductNames(String key) {
        JSONArray products = (JSONArray) getSection("products").get(key);
        if (products == null) {
            throw new RuntimeException("Product list '" + key + "' not found in " + TEST_DATA_FILE);
        }
        return toStringList(products);
    }

    public static List<String> toStringList(JSONArray array) {
        List<String> list = new ArrayList<>();
        for (Object item : array) {
            list.add(item.toString());
        }
        return list;
    }

    public static BigDecimal getTaxRate() {
        return new BigDecimal(getValue("checkout", "taxRate"));
    }

    /** Rows of a top level array (e.g. "invalidLogin") - used by TestNG data providers. */
    public static List<JSONObject> getRows(String key) {
        JSONArray rows = (JSONArray) getTestData().get(key);
        if (rows == null) {
            throw new RuntimeException("Data set '" + key + "' not found in " + TEST_DATA_FILE);
        }
        List<JSONObject> list = new ArrayList<>();
        for (Object row : rows) {
            list.add((JSONObject) row);
        }
        return list;
    }
}
