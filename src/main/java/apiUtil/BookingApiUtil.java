package apiUtil;

import java.util.Map;
import java.util.function.Supplier;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import util.APIEndPoint;
import util.JsonUtil;
import util.Log;

/**
 * Reusable Booking API calls. Methods return the raw Response - tests decide what to validate.
 */
public class BookingApiUtil {

    public Response ping() {
        return ApiClient.get(SpecBuilder.getBaseRequestSpec(), APIEndPoint.PING);
    }

    public Response getBookingIds() {
        Log.info("GET " + APIEndPoint.BOOKING);
        return ApiClient.get(SpecBuilder.getRequestSpec(), APIEndPoint.BOOKING);
    }

    public Response getBookingIds(Map<String, ?> filters) {
        Log.info("GET " + APIEndPoint.BOOKING + " with filters " + filters);
        return ApiClient.get(SpecBuilder.getRequestSpec().queryParams(filters), APIEndPoint.BOOKING);
    }

    public Response getBooking(Object bookingId) {
        Log.info("GET " + APIEndPoint.BOOKING + "/" + bookingId);
        return ApiClient.get(withId(SpecBuilder.getRequestSpec(), bookingId), APIEndPoint.BOOKING_BY_ID);
    }

    /** payload can be a Booking POJO or any object/map that serialises to JSON. */
    public Response createBooking(Object payload) {
        Log.info("POST " + APIEndPoint.BOOKING);
        return ApiClient.post(SpecBuilder.getRequestSpec().body(JsonUtil.toJson(payload)), APIEndPoint.BOOKING);
    }

    /** Same as createBooking but without retry - for negative requests that are expected to return 5xx. */
    public Response createBookingExpectingServerError(Object payload) {
        Log.info("POST " + APIEndPoint.BOOKING + " (no retry - server error expected)");
        return ApiClient.send(Method.POST, SpecBuilder.getRequestSpec().body(JsonUtil.toJson(payload)), APIEndPoint.BOOKING, 0);
    }

    /** Raw body and explicit headers - used for malformed JSON / unsupported media type checks. */
    public Response createBookingRaw(String rawBody, String contentType, String accept) {
        Log.info("POST " + APIEndPoint.BOOKING + " raw body, Content-Type=" + contentType + ", Accept=" + accept);
        RequestSpecification request = SpecBuilder.getBaseRequestSpec().body(rawBody);
        if (contentType != null) {
            request.contentType(contentType);
        }
        if (accept != null) {
            request.accept(accept);
        }
        return ApiClient.post(request, APIEndPoint.BOOKING);
    }

    public Response updateBooking(Object bookingId, Object payload, Auth auth) {
        Log.info("PUT " + APIEndPoint.BOOKING + "/" + bookingId + " using " + auth);
        return sendWithAuth(Method.PUT, () -> withId(SpecBuilder.getRequestSpec(), bookingId).body(JsonUtil.toJson(payload)), auth);
    }

    public Response partialUpdateBooking(Object bookingId, Object payload, Auth auth) {
        Log.info("PATCH " + APIEndPoint.BOOKING + "/" + bookingId + " using " + auth);
        return sendWithAuth(Method.PATCH, () -> withId(SpecBuilder.getRequestSpec(), bookingId).body(JsonUtil.toJson(payload)), auth);
    }

    public Response deleteBooking(Object bookingId, Auth auth) {
        Log.info("DELETE " + APIEndPoint.BOOKING + "/" + bookingId + " using " + auth);
        return sendWithAuth(Method.DELETE, () -> withId(SpecBuilder.getBaseRequestSpec(), bookingId), auth);
    }

    /**
     * Sends a request that needs auth. With the shared token, a 403 means the token expired:
     * a new token is created and the request is sent once more.
     */
    private Response sendWithAuth(Method method, Supplier<RequestSpecification> requestBuilder, Auth auth) {
        String tokenUsed = auth.isSharedToken() ? AuthApiUtil.getToken() : null;
        Response response = ApiClient.send(method, auth.apply(requestBuilder.get()), APIEndPoint.BOOKING_BY_ID);
        if (response.getStatusCode() == 403 && auth.isSharedToken()) {
            AuthApiUtil.refreshToken(tokenUsed);
            response = ApiClient.send(method, auth.apply(requestBuilder.get()), APIEndPoint.BOOKING_BY_ID);
        }
        return response;
    }

    private RequestSpecification withId(RequestSpecification request, Object bookingId) {
        return request.pathParam("id", bookingId);
    }
}
