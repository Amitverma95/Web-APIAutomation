package api_test_scripts;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;

import apiUtil.Auth;
import apiUtil.AuthApiUtil;
import apiUtil.BookingApiUtil;
import io.restassured.response.Response;
import listener.TestListener;
import pojo.booking.Booking;
import pojo.booking.CreateBookingResponse;
import util.JsonUtil;
import util.Log;

/**
 * Base for API tests - no browser. Bookings created through createTestBooking() are deleted after each test.
 */
@Listeners(TestListener.class)
public class BaseApiTest {

    protected AuthApiUtil authApi = new AuthApiUtil();
    protected BookingApiUtil bookingApi = new BookingApiUtil();

    private static final ThreadLocal<List<Integer>> createdBookings = ThreadLocal.withInitial(ArrayList::new);

    /** Precondition - creates a booking and returns its id. */
    protected int createTestBooking(Booking booking) {
        Response response = bookingApi.createBooking(booking);
        Assert.assertEquals(response.getStatusCode(), 200, "Precondition failed - create booking: " + response.asString());
        int bookingId = JsonUtil.fromJson(response, CreateBookingResponse.class).getBookingid();
        createdBookings.get().add(bookingId);
        Log.info("Precondition: created booking " + bookingId);
        return bookingId;
    }

    /** Registers a booking created inside the test itself for clean up. */
    protected void registerForCleanup(int bookingId) {
        createdBookings.get().add(bookingId);
    }

    /** Token from POST /auth, refreshed automatically when it expires. */
    protected Auth tokenAuth() {
        return Auth.sharedToken();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteCreatedBookings() {
        for (int bookingId : createdBookings.get()) {
            try {
                bookingApi.deleteBooking(bookingId, tokenAuth());
            } catch (Exception e) {
                System.err.println("Could not delete booking " + bookingId + ": " + e.getMessage());
            }
        }
        createdBookings.get().clear();
    }
}
