package apiUtil;

import java.util.stream.Collectors;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
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
        req.append(request.getHeaders().asList().stream()
                .map(h -> h.getName() + ": " + mask(h.getName(), h.getValue()))
                .collect(Collectors.joining("\n")));
        if (!request.getCookies().asList().isEmpty()) {
            req.append("\nCookie: ").append(request.getCookies().asList().stream()
                    .map(c -> c.getName() + "=" + maskValue(c.getValue()))
                    .collect(Collectors.joining("; ")));
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
