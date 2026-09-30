package dataProvider;

import java.util.List;

import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.databind.JsonNode;

import apiUtil.Auth;
import pojo.auth.AuthRequest;
import pojo.booking.Booking;
import util.ApiTestDataReader;

/**
 * API data providers backed by src/main/resources/testdata/apiTestData.json.
 * The first value of every row is a readable case name - it is shown in the report.
 */
public class ApiDataProvider {

    @DataProvider(name = "bookingData", parallel = true)
    public static Object[][] bookingData() {
        List<JsonNode> rows = ApiTestDataReader.getRows("bookings");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            data[i] = new Object[] { rows.get(i).get("case").asText(),
                    ApiTestDataReader.toObject(rows.get(i).get("booking"), Booking.class) };
        }
        return data;
    }

    @DataProvider(name = "invalidCredentials")
    public static Object[][] invalidCredentials() {
        List<JsonNode> rows = ApiTestDataReader.getRows("invalidCredentials");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            JsonNode row = rows.get(i);
            String password = row.get("password").isNull() ? null : row.get("password").asText();
            data[i] = new Object[] { row.get("case").asText(), new AuthRequest(row.get("username").asText(), password) };
        }
        return data;
    }

    @DataProvider(name = "invalidBookingIds")
    public static Object[][] invalidBookingIds() {
        List<JsonNode> rows = ApiTestDataReader.getRows("invalidBookingIds");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            data[i] = new Object[] { rows.get(i).asText() };
        }
        return data;
    }

    @DataProvider(name = "missingMandatoryFields")
    public static Object[][] missingMandatoryFields() {
        List<JsonNode> rows = ApiTestDataReader.getRows("missingMandatoryFields");
        Object[][] data = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            data[i] = new Object[] { rows.get(i).get("case").asText(), rows.get(i).get("payload") };
        }
        return data;
    }

    @DataProvider(name = "invalidAuth")
    public static Object[][] invalidAuth() {
        return new Object[][] {
                { "No auth", Auth.none() },
                { "Invalid token", Auth.token("invalid_token") },
                { "Invalid basic auth", Auth.basic("wrong_user", "wrong_password") } };
    }
}
