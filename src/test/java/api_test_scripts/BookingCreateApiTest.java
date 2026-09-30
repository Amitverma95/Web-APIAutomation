package api_test_scripts;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import apiUtil.SpecBuilder;
import dataProvider.ApiDataProvider;
import io.restassured.response.Response;
import pojo.booking.Booking;
import pojo.booking.CreateBookingResponse;
import util.JsonUtil;
import util.ApiTestDataReader;
import util.TestDescriptionContant;

public class BookingCreateApiTest extends BaseApiTest {

    @Test(groups = { "api", "smoke", "regression", "booking" }, dataProvider = "bookingData",
            dataProviderClass = ApiDataProvider.class, description = TestDescriptionContant.API_CREATE_BOOKING)
    public void verifyCreateBooking(String testCase, Booking booking) {
        Response response = bookingApi.createBooking(booking);

        response.then().spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response-schema.json"));
        CreateBookingResponse created = JsonUtil.fromJson(response, CreateBookingResponse.class);
        registerForCleanup(created.getBookingid());

        Assert.assertTrue(created.getBookingid() > 0, testCase + ": booking id should be positive");
        Assert.assertEquals(created.getBooking(), booking, testCase + ": booking in create response");
        // persisted - read it back
        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(created.getBookingid()), Booking.class), booking,
                testCase + ": booking returned by GET after create");
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, dataProvider = "missingMandatoryFields",
            dataProviderClass = ApiDataProvider.class, description = TestDescriptionContant.API_CREATE_MISSING_FIELDS)
    public void verifyCreateBookingWithMissingMandatoryFields(String testCase, JsonNode payload) {
        Response response = bookingApi.createBookingExpectingServerError(payload);

        // Known API behaviour: no request validation, missing fields crash the server (500) instead of 400
        response.then().spec(SpecBuilder.getStatusResponseSpec(500));
        Assert.assertFalse(response.asString().contains("bookingid"), testCase + ": booking must not be created");
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, description = TestDescriptionContant.API_CREATE_MALFORMED)
    public void verifyCreateBookingWithMalformedJson() {
        Response response = bookingApi.createBookingRaw("{\"firstname\": \"Jim\", ", "application/json", "application/json");
        response.then().spec(SpecBuilder.getStatusResponseSpec(400));
        Assert.assertEquals(response.asString(), "Bad Request", "Error body");
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, description = TestDescriptionContant.API_CREATE_UNSUPPORTED_ACCEPT)
    public void verifyCreateBookingWithUnsupportedAcceptHeader() {
        String body = "{\"firstname\":\"Jim\",\"lastname\":\"Brown\",\"totalprice\":1,\"depositpaid\":true,"
                + "\"bookingdates\":{\"checkin\":\"2026-11-01\",\"checkout\":\"2026-11-02\"}}";
        Response response = bookingApi.createBookingRaw(body, "application/json", "text/plain");
        response.then().spec(SpecBuilder.getStatusResponseSpec(418));
        Assert.assertEquals(response.asString(), "I'm a Teapot", "Error body");
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, description = TestDescriptionContant.API_CREATE_INVALID_TYPE)
    public void verifyCreateBookingWithInvalidTotalPrice() {
        Booking booking = ApiTestDataReader.getDefaultBooking();
        Map<String, Object> payload = Map.of(
                "firstname", booking.getFirstname(), "lastname", booking.getLastname(), "totalprice", "abc",
                "depositpaid", booking.getDepositpaid(), "bookingdates", booking.getBookingdates());

        Response response = bookingApi.createBooking(payload);

        // Known API behaviour: accepts the request (200) but drops the invalid value instead of rejecting it
        response.then().spec(SpecBuilder.getJsonResponseSpec(200));
        registerForCleanup(response.jsonPath().getInt("bookingid"));
        Assert.assertNull(response.jsonPath().get("booking.totalprice"), "Invalid totalprice 'abc' must not be stored");
    }
}
