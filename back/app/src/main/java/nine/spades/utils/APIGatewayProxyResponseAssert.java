package nine.spades.utils;

import java.util.*;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import jakarta.ws.rs.core.Response.Status;
import org.assertj.core.api.AbstractAssert;

/**
 * Custom AssertJ assertion for APIGatewayProxyResponseEvent
 */
public class APIGatewayProxyResponseAssert 
    extends AbstractAssert<APIGatewayProxyResponseAssert, APIGatewayProxyResponseEvent> {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public APIGatewayProxyResponseAssert(APIGatewayProxyResponseEvent actual) {
        super(Objects.requireNonNull(actual), APIGatewayProxyResponseAssert.class);
    }
    
    public static APIGatewayProxyResponseAssert assertThat(APIGatewayProxyResponseEvent response) {
        return new APIGatewayProxyResponseAssert(response);
    }
    
    /**
     * Verifies the response status code
     */
    public APIGatewayProxyResponseAssert hasStatus(int expectedStatus) {
        if(actual.getStatusCode() != expectedStatus)
            failWithMessage("Expected status <%d> but was <%d>", expectedStatus, actual.getStatusCode());
        return this;
    }
    
    /**
     * Verifies the response has a non-null body
     */
    public APIGatewayProxyResponseAssert hasNonNullBody() {
        if(actual.getBody() == null)
            failWithMessage("Expected non-null body but was null");
        return this;
    }
    
    /**
     * Verifies the response body matches exactly
     */
    public APIGatewayProxyResponseAssert hasBody(String expectedBody) {
        hasNonNullBody();
        if(!actual.getBody().equals(expectedBody));
            failWithMessage("Expected body <%s> but was <%s>", expectedBody, actual.getBody());
        return this;
    }
    
    /**
     * Verifies the response body contains the specified text
     */
    public APIGatewayProxyResponseAssert bodyContains(String expectedText) {
        hasNonNullBody();
        if(!actual.getBody().contains(expectedText))
            failWithMessage("Expected body to contain <%s> but was <%s>", expectedText, actual.getBody());
        return this;
    }
    
    /**
     * Verifies the response body matches the given regex pattern
     */
    public APIGatewayProxyResponseAssert bodyMatches(String regex) {
        hasNonNullBody();
        if(!actual.getBody().matches(regex))
            failWithMessage("Expected body to match regex <%s> but was <%s>", regex, actual.getBody());
        return this;
    }
    
    /**
     * Verifies the response has valid JSON body
     */
    public APIGatewayProxyResponseAssert hasValidJsonBody() {
        hasNonNullBody();
        try {
            OBJECT_MAPPER.readTree(actual.getBody());
        } catch(Exception ignore) {
            failWithMessage("Expected valid JSON body but was invalid: <%s>", actual.getBody());
        }
        return this;
    }

    /**
     * Verifies a header exists
     */
    public APIGatewayProxyResponseAssert hasHeader(String headerName) {
        Map<String, String> headers = actual.getHeaders();
        if(headers == null || !headers.containsKey(headerName))
            failWithMessage("Expected header <%s> to exist but was not found", headerName);
        return this;
    }
    
    /**
     * Verifies a header exists with expected value
     */
    public APIGatewayProxyResponseAssert hasHeader(String headerName, String expectedValue) {
        hasHeader(headerName);
        String actualValue = actual.getHeaders().get(headerName);
        if(!actualValue.equals(expectedValue))
            failWithMessage("Expected header <%s> to be <%s> but was <%s>", headerName, expectedValue, actualValue);
        return this;
    }
    
    /**
     * Verifies CORS headers are present
     */
    public APIGatewayProxyResponseAssert hasCorsHeaders() {
        hasHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        return this;
    }
    
    /**
     * Convenience methods for common status codes
     */
    public APIGatewayProxyResponseAssert isOk() {
        return hasStatus(Status.OK.getStatusCode());
    }
    
    public APIGatewayProxyResponseAssert isCreated() {
        return hasStatus(Status.CREATED.getStatusCode());
    }
    
    public APIGatewayProxyResponseAssert isBadRequest() {
        return hasStatus(Status.BAD_REQUEST.getStatusCode());
    }
    
    public APIGatewayProxyResponseAssert isUnauthorized() {
        return hasStatus(Status.UNAUTHORIZED.getStatusCode());
    }
    
    public APIGatewayProxyResponseAssert isForbidden() {
        return hasStatus(Status.FORBIDDEN.getStatusCode());
    }
    
    public APIGatewayProxyResponseAssert isNotFound() {
        return hasStatus(Status.NOT_FOUND.getStatusCode());
    }
    
    public APIGatewayProxyResponseAssert isInternalServerError() {
        return hasStatus(Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    /**
     * Verifies JSON response contains a field
     * e.g. "/user/profile/firstName"
     */
    public APIGatewayProxyResponseAssert hasJsonField(String fieldPath) {
        hasValidJsonBody();
        try {
            if(OBJECT_MAPPER.readTree(actual.getBody()).at(fieldPath).isMissingNode())
                failWithMessage("Expected JSON field <%s> to exist but was not found", fieldPath);
        } catch(JsonProcessingException ignore) {}
        return this;
    }
    
    /**
     * Verifies JSON response contains a field with expected value
     */
    public APIGatewayProxyResponseAssert hasJsonField(String fieldPath, String expectedValue) {
        hasJsonField(fieldPath);
        try {            
            String actualValue = OBJECT_MAPPER.readTree(actual.getBody()).at(fieldPath).asText();
            if(!actualValue.equals(expectedValue))
                failWithMessage("Expected JSON field <%s> to be <%s> but was <%s>", fieldPath, expectedValue, actualValue);
        } catch(JsonProcessingException ignore) {}
        return this;
    }
}
