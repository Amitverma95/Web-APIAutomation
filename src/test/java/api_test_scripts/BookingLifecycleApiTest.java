package api_test_scripts;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.Assert;
import org.testng.annotations.Test;

import apiUtil.SpecBuilder;
import io.restassured.response.Response;
import pojo.booking.Booking;
import pojo.booking.CreateBookingResponse;
import util.JsonUtil;
import util.ApiTestDataReader;
import util.Log;
import util.TestDescriptionContant;

public class BookingLifecycleApiTest extends BaseApiTest {

    @Test(groups = { "api", "smoke", "regression", "e2e", "booking" }, description = TestDescriptionContant.API_BOOKING_LIFECYCLE)
    public void verifyBookingLifecycle() {
        Booking booking = ApiTestDataReader.getDefaultBooking();

        Log.info("Step 1: create booking");
        Response createResponse = bookingApi.createBooking(booking);
        createResponse.then().spec(SpecBuilder.getJsonResponseSpec(200))
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response-schema.json"));
        int bookingId = JsonUtil.fromJson(createResponse, CreateBookingResponse.class).getBookingid();
        registerForCleanup(bookingId);

        Log.info("Step 2: read booking " + bookingId);
        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(bookingId), Booking.class), booking, "Booking after create");

        Log.info("Step 3: full update");
        Booking update = ApiTestDataReader.get("updateBooking", Booking.class);
        bookingApi.updateBooking(bookingId, update, tokenAuth()).then().spec(SpecBuilder.getJsonResponseSpec(200));
        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(bookingId), Booking.class), update, "Booking after PUT");

        Log.info("Step 4: partial update");
        Booking patch = ApiTestDataReader.get("partialUpdate", Booking.class);
        bookingApi.partialUpdateBooking(bookingId, patch, tokenAuth()).then().spec(SpecBuilder.getJsonResponseSpec(200));
        Assert.assertEquals(JsonUtil.fromJson(bookingApi.getBooking(bookingId), Booking.class), update.merge(patch), "Booking after PATCH");

        Log.info("Step 5: delete");
        bookingApi.deleteBooking(bookingId, tokenAuth()).then().spec(SpecBuilder.getStatusResponseSpec(201));
        bookingApi.getBooking(bookingId).then().spec(SpecBuilder.getStatusResponseSpec(404));
    }
}
