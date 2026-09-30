package api_test_scripts;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;

import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import apiUtil.SpecBuilder;
import dataProvider.ApiDataProvider;
import io.restassured.response.Response;
import pojo.booking.Booking;
import util.JsonUtil;
import util.ApiTestDataReader;
import util.TestDescriptionContant;

public class BookingGetApiTest extends BaseApiTest {

    @Test(groups = { "api", "smoke", "regression", "booking" }, description = TestDescriptionContant.API_HEALTH_CHECK)
    public void verifyHealthCheck() {
        Response response = bookingApi.ping();
        response.then().spec(SpecBuilder.getStatusResponseSpec(201));
        Assert.assertEquals(response.asString(), "Created", "Ping body");
    }

    @Test(groups = { "api", "regression", "booking" }, description = TestDescriptionContant.API_GET_BOOKING_IDS)
    public void verifyGetAllBookingIds() {
        bookingApi.getBookingIds().then()
                .spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids-schema.json"))
                .body("$", not(empty()))
                .body("bookingid", everyItem(greaterThan(0)));
    }

    @Test(groups = { "api", "smoke", "regression", "booking" }, description = TestDescriptionContant.API_GET_BOOKING_BY_ID)
    public void verifyGetBookingById() {
        Booking expected = ApiTestDataReader.getDefaultBooking();
        int bookingId = createTestBooking(expected);

        Response response = bookingApi.getBooking(bookingId);

        response.then().spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
        Assert.assertEquals(JsonUtil.fromJson(response, Booking.class), expected, "Booking returned by GET /booking/" + bookingId);
    }

    @Test(groups = { "api", "regression", "booking" }, description = TestDescriptionContant.API_FILTER_BOOKINGS)
    public void verifyFilterBookingsByName() {
        // unique name so the filter result is predictable on the shared public server
        Booking booking = ApiTestDataReader.getDefaultBooking();
        booking.setFirstname("Filter" + UUID.randomUUID().toString().substring(0, 8));
        int bookingId = createTestBooking(booking);

        bookingApi.getBookingIds(Map.of("firstname", booking.getFirstname(), "lastname", booking.getLastname())).then()
                .spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids-schema.json"))
                .body("bookingid", hasItem(bookingId));
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, dataProvider = "invalidBookingIds",
            dataProviderClass = ApiDataProvider.class, description = TestDescriptionContant.API_GET_INVALID_ID)
    public void verifyGetBookingWithInvalidId(String bookingId) {
        Response response = bookingApi.getBooking(bookingId);
        response.then().spec(SpecBuilder.getStatusResponseSpec(404));
        Assert.assertEquals(response.asString(), "Not Found", "Error body for id " + bookingId);
    }
}
