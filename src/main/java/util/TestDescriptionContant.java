package util;

public class TestDescriptionContant {

    public final static String VERIFY_LOGIN = "As a user i opened the application, entered valid username and password "
            + "and clicked on login button. User should land on the Products (inventory) page";

    public final static String VERIFY_INVALID_LOGIN = "Login with invalid credentials should show the correct error message "
            + "and user should stay on the login page";

    public final static String VERIFY_LOGOUT = "Logged in user should be able to logout from the side menu";

    public final static String VERIFY_INVENTORY_PRODUCTS = "Inventory page should list products with name and price";

    public final static String VERIFY_PRODUCT_DETAILS = "Selecting a product should open its details page with the same name and price";

    public final static String VERIFY_ADD_REMOVE_CART_BADGE = "Adding and removing products should update the cart badge count";

    public final static String VERIFY_SORT_BY_PRICE = "Sorting by price (low to high) should order the products by ascending price";

    public final static String VERIFY_SORT_BY_NAME = "Sorting by name (Z to A) should order the products by descending name";

    public final static String VERIFY_CART_DETAILS = "Cart should show every added product with correct name, price and quantity";

    public final static String VERIFY_REMOVE_FROM_CART = "Removing a product from the cart should remove it from the list and update the badge";

    public final static String VERIFY_CHECKOUT_MANDATORY_FIELDS = "Checkout information form should not continue when a mandatory field is empty";

    public final static String VERIFY_CHECKOUT_OVERVIEW = "Checkout overview should show the items, payment, shipping and correct item total, tax and total";

    public final static String VERIFY_CANCEL_CHECKOUT = "Cancelling checkout from the information page should return the user to the cart";

    public final static String VERIFY_END_TO_END_PURCHASE = "As a user i login, select products, add them to the cart, verify the cart, "
            + "enter shipping details, complete checkout and verify the order confirmation";

    // ------------------------------------------------------------------
    // API - Restful-Booker
    // ------------------------------------------------------------------

    public final static String API_HEALTH_CHECK = "GET /ping should return 201 Created when the API is up";

    public final static String API_CREATE_TOKEN = "POST /auth with valid credentials should return 200 and a token matching the schema";

    public final static String API_CREATE_TOKEN_INVALID = "POST /auth with invalid credentials should not return a token - API answers 200 with reason 'Bad credentials'";

    public final static String API_CREATE_TOKEN_MALFORMED = "POST /auth with a malformed JSON body should return 400 Bad Request";

    public final static String API_GET_BOOKING_IDS = "GET /booking should return 200 and a list of booking ids matching the schema";

    public final static String API_GET_BOOKING_BY_ID = "GET /booking/{id} should return 200 and the booking that was created";

    public final static String API_FILTER_BOOKINGS = "GET /booking?firstname&lastname should return the id of the matching booking";

    public final static String API_GET_INVALID_ID = "GET /booking/{id} with an unknown or invalid id should return 404 Not Found";

    public final static String API_CREATE_BOOKING = "POST /booking should return 200, a booking id and the same booking data (data driven)";

    public final static String API_CREATE_MISSING_FIELDS = "POST /booking without a mandatory field should be rejected - API answers 500 (no validation)";

    public final static String API_CREATE_MALFORMED = "POST /booking with a malformed JSON body should return 400 Bad Request";

    public final static String API_CREATE_UNSUPPORTED_ACCEPT = "POST /booking with an unsupported Accept header should return 418";

    public final static String API_CREATE_INVALID_TYPE = "POST /booking with a non numeric totalprice must not store the invalid value";

    public final static String API_UPDATE_BOOKING = "PUT /booking/{id} with token / basic auth should replace the booking";

    public final static String API_PARTIAL_UPDATE = "PATCH /booking/{id} should change only the sent fields";

    public final static String API_UPDATE_INVALID_AUTH = "PUT / PATCH without valid auth should return 403 and leave the booking unchanged";

    public final static String API_UPDATE_MISSING_FIELDS = "PUT /booking/{id} without mandatory fields should return 400 Bad Request";

    public final static String API_UPDATE_INVALID_ID = "PUT / PATCH on an unknown id should be rejected with 405";

    public final static String API_DELETE_BOOKING = "DELETE /booking/{id} should return 201 and the booking should no longer exist";

    public final static String API_DELETE_INVALID_AUTH = "DELETE without valid auth should return 403 and keep the booking";

    public final static String API_DELETE_INVALID_ID = "DELETE on an unknown id should be rejected with 405";

    public final static String API_BOOKING_LIFECYCLE = "Create, read, update, partially update and delete a booking end to end";
}
