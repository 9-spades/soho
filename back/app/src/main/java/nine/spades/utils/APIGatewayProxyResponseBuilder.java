package nine.spades.utils;

import java.util.*;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.ws.rs.core.*;

public final class APIGatewayProxyResponseBuilder {
    private final APIGatewayProxyResponseEvent response;
    private final Map<String, String> headers;

    private APIGatewayProxyResponseBuilder() {
        this.response = new APIGatewayProxyResponseEvent();
        this.headers = new HashMap<>();
    }

    public static APIGatewayProxyResponseBuilder builder() {
        return new APIGatewayProxyResponseBuilder();
    }

    public APIGatewayProxyResponseBuilder status(Response.Status status) {
        response.setStatusCode(status.getStatusCode());
        return this;
    }

    public APIGatewayProxyResponseBuilder body(String body) {
        response.setBody(body);
        return this;
    }

    public APIGatewayProxyResponseBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public APIGatewayProxyResponseBuilder contentTypeJson() {
        return header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    public APIGatewayProxyResponseBuilder corsHeaders() {
        return header(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    }

    public APIGatewayProxyResponseEvent build() {
        response.setHeaders(headers);
        return response;
    }

    public static APIGatewayProxyResponseEvent createSuccessResponse(String body) {
        return builder()
            .body(body)
            .contentTypeJson()
            .status(Response.Status.OK)
            .build();
    }
    
    public static APIGatewayProxyResponseEvent createErrorResponse(Response.Status status, String message) {
        return builder()
            .body(message)
            .status(status)
            .build();
    }
}