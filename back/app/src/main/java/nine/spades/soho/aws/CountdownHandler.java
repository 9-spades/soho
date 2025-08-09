package nine.spades.soho.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import nine.spades.time.Countdown;

import java.util.HashMap;
import java.util.Map;

public class CountdownHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    // query params
    public static final String TARGET_DATE = "targetDate";
    public static final String TIMEZONE = "timezone";
    // msgs
    public static final String MISSING_TARGET_DATE = "targetDate parameter is required";
    public static final String INVALID_TARGET_DATE = "Invalid date format";
    public static final String INVALID_TIMEZONE = "Invalid timezone";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Countdown countdown = new Countdown();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        
        // Set CORS headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeaders(headers);

        try {
            // Handle OPTIONS request for CORS preflight
            if ("OPTIONS".equals(request.getHttpMethod())) {
                response.setStatusCode(200);
                response.setBody("");
                return response;
            }

            // Get query parameters
            Map<String, String> queryParams = request.getQueryStringParameters();
            if (queryParams == null) {
                return createErrorResponse(400, "targetDate parameter is required");
            }

            String targetDateStr = queryParams.get("targetDate");
            if (targetDateStr == null || targetDateStr.isEmpty()) {
                return createErrorResponse(400, "targetDate parameter is required");
            }

            String timezone = queryParams.getOrDefault("timezone", "UTC");

            // TODO: Implement actual countdown calculation
            countdown.wait(1000);
            // For now, return a stub response
            CountdownResponse countdownResponse = CountdownResponse.builder()
                .days(0)
                .hours(0)
                .minutes(0)
                .seconds(0)
                .targetDate(targetDateStr)
                .timezone(timezone)
                .build();

            response.setStatusCode(200);
            response.setBody(objectMapper.writeValueAsString(countdownResponse));

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            if (e.getMessage().contains("Invalid date format")) {
                return createErrorResponse(400, "Invalid date format");
            } else if (e.getMessage().contains("Invalid timezone")) {
                return createErrorResponse(400, "Invalid timezone");
            } else {
                return createErrorResponse(500, "Internal server error");
            }
        }

        return response;
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);
        
        try {
            response.setBody(objectMapper.writeValueAsString(errorBody));
        } catch (Exception e) {
            response.setBody("{\"error\":\"" + message + "\"}");
        }
        
        return response;
    }
}