package api_test_scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

import apiUtil.Auth;
import apiUtil.SpecBuilder;
import dataProvider.ApiDataProvider;
import io.restassured.response.Response;
import util.ApiTestDataReader;
import util.TestDescriptionContant;

public class BookingDeleteApiTest extends BaseApiTest {

    @Test(groups = { "api", "smoke", "regression", "booking" }, description = TestDescriptionContant.API_DELETE_BOOKING)
    public void verifyDeleteBooking() {
        int bookingId = createTestBooking(ApiTestDataReader.getDefaultBooking());

        Response response = bookingApi.deleteBooking(bookingId, tokenAuth());

        // Restful-Booker answers a successful delete with 201 Created
        response.then().spec(SpecBuilder.getStatusResponseSpec(201));
        Assert.assertEquals(response.asString(), "Created", "Delete response body");
        bookingApi.getBooking(bookingId).then().spec(SpecBuilder.getStatusResponseSpec(404));
    }

    @Test(groups = { "api", "regression", "booking", "negative", "auth" }, dataProvider = "invalidAuth",
            dataProviderClass = ApiDataProvider.class, description = TestDescriptionContant.API_DELETE_INVALID_AUTH)
    public void verifyDeleteBookingWithInvalidAuth(String testCase, Auth auth) {
        int bookingId = createTestBooking(ApiTestDataReader.getDefaultBooking());

        Response response = bookingApi.deleteBooking(bookingId, auth);

        response.then().spec(SpecBuilder.getStatusResponseSpec(403));
        Assert.assertEquals(response.asString(), "Forbidden", testCase + ": error body");
        bookingApi.getBooking(bookingId).then().spec(SpecBuilder.getJsonResponseSpec(200));
    }

    @Test(groups = { "api", "regression", "booking", "negative" }, description = TestDescriptionContant.API_DELETE_INVALID_ID)
    public void verifyDeleteBookingWithInvalidId() {
        bookingApi.deleteBooking(99999999, tokenAuth()).then().spec(SpecBuilder.getStatusResponseSpec(405));
    }
}
