package apiUtil;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import util.Log;

/**
 * Logs every request and response to the log file and the Extent report of the running test.
 * Tokens, basic auth and passwords are masked.
 */
public class ApiLogFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification responseSpec,
            FilterContext ctx) {
        StringBuilder req = new StringBuilder();
        req.append(request.getMethod()).append(' ').append(request.getURI()).append('\n');
        for (Header h : request.getHeaders().asList()) {
            req.append(h.getName()).append(": ").append(mask(h.getName(), h.getValue())).append('\n');
        }
        if (!request.getCookies().asList().isEmpty()) {
            StringBuilder cookieStr = new StringBuilder();
            for (Cookie c : request.getCookies().asList()) {
                if (cookieStr.length() > 0) cookieStr.append("; ");
                cookieStr.append(c.getName()).append("=").append(maskValue(c.getValue()));
            }
            req.append("Cookie: ").append(cookieStr).append('\n');
        }
        Object body = request.getBody();
        if (body != null) {
            req.append("\n\n").append(body.toString().replaceAll("(\"password\"\\s*:\\s*\")[^\"]*\"", "$1****\""));
        }
        Log.code("API Request", req.toString());

        Response response = ctx.next(request, responseSpec);

        String res = "HTTP " + response.getStatusCode() + " (" + response.getTime() + " ms)\n"
                + "Content-Type: " + response.getContentType() + "\n\n" + response.getBody().asPrettyString();
        Log.code("API Response", res);
        return response;
    }

    private String mask(String header, String value) {
        return header.equalsIgnoreCase("Authorization") || header.equalsIgnoreCase("Cookie") ? maskValue(value) : value;
    }

    private String maskValue(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 4) + "****";
    }
}
