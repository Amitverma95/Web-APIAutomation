package dataProvider;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.annotations.DataProvider;

import util.TestDataReader;

/**
 * TestNG data providers backed by src/main/resources/testdata/testData.json.
 * The first value of every row is a readable case name - it is shown in the report.
 */
public class TestDataProvider {

    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        List<JSONObject> rows = TestDataReader.getRows("invalidLogin");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.get(i);
            data[i] = new Object[] { row.get("case"), row.get("username"), row.get("password"), row.get("error") };
        }
        return data;
    }

    @DataProvider(name = "invalidCheckoutData")
    public static Object[][] invalidCheckoutData() {
        List<JSONObject> rows = TestDataReader.getRows("invalidCheckout");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.get(i);
            data[i] = new Object[] { row.get("case"), TestDataReader.toCustomer(row), row.get("error") };
        }
        return data;
    }

    @DataProvider(name = "purchaseData")
    public static Object[][] purchaseData() {
        List<JSONObject> rows = TestDataReader.getRows("purchaseScenarios");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.get(i);
            data[i] = new Object[] { row.get("scenario"), TestDataReader.toStringList((JSONArray) row.get("products")) };
        }
        return data;
    }
}
