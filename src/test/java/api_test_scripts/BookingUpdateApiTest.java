package api_test_scripts;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import apiUtil.Auth;
import apiUtil.SpecBuilder;
import dataProvider.ApiDataProvider;
import io.restassured.response.Response;
import pojo.booking.Booking;
import util.JsonUtil;
import util.ApiTestDataReader;
import util.ConfigFileReader;
import util.TestDescriptionContant;

public class BookingUpdateApiTest extends BaseApiTest {

    private final ConfigFileReader config = new ConfigFileReader();

    @Test(groups = { "api", "smoke", "regression", "booking" }, description = TestDescriptionContant.API_UPDATE_BOOKING)
    public void verifyUpdateBookingWithToken() {
        verifyUpdateBooking(tokenAuth());
    }

    @Test(groups = { "api", "regression", "booking", "auth" }, description = TestDescriptionContant.API_UPDATE_BOOKING)
    public void verifyUpdateBookingWithBasicAuth() {
        verifyUpdateBooking(Auth.basic(config.getApiUsername(), config.getApiPassword()));
    }

    private void verifyUpdateBooking(Auth auth) {
        int bookingId = createTestBooking(ApiTestDataReader.getDefaultBooking());
        Booking update = ApiTestDataReader.get("updateBooking", Booking.class);

        Response response = bookingApi.updateBooking(bookingId, update, auth);

        response.then().spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
        Assert.assertEquals(JsonUtil.fromJson(response, Booking.class), update, "Booking in PUT response");
        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(bookingId), Booking.class), update, "Booking returned by GET after PUT");
    }

    @Test(groups = { "api", "regression", "booking" }, description = TestDescriptionContant.API_PARTIAL_UPDATE)
    public void verifyPartialUpdateBooking() {
        Booking original = ApiTestDataReader.getDefaultBooking();
        int bookingId = createTestBooking(original);
        Booking patch = ApiTestDataReader.get("partialUpdate", Booking.class);
        Booking expected = original.merge(patch);

        Response response = bookingApi.partialUpdateBooking(bookingId, patch, tokenAuth());

        response.then().spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
        Booking actual = JsonUtil.fromJson(response, Booking.class);
        Assert.assertEquals(actual.getFirstname(), patch.getFirstname(), "Patched firstname");
        Assert.assertEquals(actual.getTotalprice(), patch.getTotalprice(), "Patched totalprice");
        Assert.assertEquals(actual, expected, "Fields not in the PATCH body must stay unchanged");
    }

    @Test(groups = { "api", "regression", "booking", "negative", "auth" }, dataProvider = "invalidAuth",
            dataProviderClass = ApiDataProvider.class, description = TestDescriptionContant.API_UPDATE_INVALID_AUTH)
    public void verifyUpdateBookingWithInvalidAuth(String testCase, Auth auth) {
        Booking original = ApiTestDataReader.getDefaultBooking();
        int bookingId = createTestBooking(original);

        Response putResponse = bookingApi.updateBooking(bookingId, ApiTestDataReader.get("updateBooking", Booking.class), auth);
        putResponse.then().spec(SpecBuilder.getStatusResponseSpec(403));
        Assert.assertEquals(putResponse.asString(), "Forbidden", testCase + ": PUT error body");

        Response patchResponse = bookingApi.partialUpdateBooking(bookingId, ApiTestDataReader.get("partialUpdate", Booking.class), auth);
        patchResponse.then().spec(SpecBuilder.getStatusResponseSpec(403));

        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(bookingId), Booking.class), original, testCase + ": booking must be unchanged");
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, description = TestDescriptionContant.API_UPDATE_MISSING_FIELDS)
    public void verifyUpdateBookingWithMissingFields() {
        Booking original = ApiTestDataReader.getDefaultBooking();
        int bookingId = createTestBooking(original);

        Response response = bookingApi.updateBooking(bookingId, Map.of("firstname", "OnlyFirstName"), tokenAuth());

        response.then().spec(SpecBuilder.getStatusResponseSpec(400));
        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(bookingId), Booking.class), original, "Booking must be unchanged");
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, description = TestDescriptionContant.API_UPDATE_INVALID_ID)
    public void verifyUpdateBookingWithInvalidId() {
        Booking update = ApiTestDataReader.get("updateBooking", Booking.class);
        bookingApi.updateBooking(99999999, update, tokenAuth()).then().spec(SpecBuilder.getStatusResponseSpec(405));
        bookingApi.partialUpdateBooking(99999999, ApiTestDataReader.get("partialUpdate", Booking.class), tokenAuth())
                .then().spec(SpecBuilder.getStatusResponseSpec(405));
    }
}
