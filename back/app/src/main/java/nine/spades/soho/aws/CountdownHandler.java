package nine.spades.soho.aws;

import java.time.*;
import java.time.zone.ZoneRulesException;
import java.util.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.*;

import nine.spades.time.Countdown;
import nine.spades.utils.*;

public class CountdownHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String TARGET_DATE = "targetDate"; // query params
    public static final String TIMEZONE = "timezone";
    public static final String MISSING_TARGET_DATE = "Query parameter 'targetDate' is required"; // msgs
    public static final String INVALID_TARGET_DATE = "Invalid 'targetDate' format";
    public static final String INVALID_TIMEZONE = "Invalid 'timezone'";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        if(HttpMethod.OPTIONS.equals(request.getHttpMethod())) {
            return APIGatewayProxyResponseBuilder.builder()
                .corsHeaders()
                .header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.format("%s, %s", HttpMethod.GET, HttpMethod.OPTIONS))
                .header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE)
                .status(Response.Status.OK)
                .build();
        }
        Map<String, String> queryParams = request.getQueryStringParameters();
        if(queryParams == null || queryParams.get(TARGET_DATE) == null || queryParams.get(TARGET_DATE).isEmpty())
            return APIGatewayProxyResponseBuilder.createErrorResponse(Response.Status.BAD_REQUEST, MISSING_TARGET_DATE);
        String targetDate = queryParams.get(TARGET_DATE), timezone = queryParams.getOrDefault(TIMEZONE, DEFAULT_TIMEZONE);
        try {
            List<Integer> countdown = Countdown.compute(ZonedDateTime.now(), LocalDateTime.parse(targetDate).atZone(ZoneId.of(timezone)));
            CountdownResponse body = CountdownResponse.builder()
                .days(countdown.get(0))
                .hours(countdown.get(1))
                .minutes(countdown.get(2))
                .seconds(countdown.get(3))
                .targetDate(targetDate)
                .timezone(timezone)
                .build();
            return APIGatewayProxyResponseBuilder.builder()
                .corsHeaders()
                .body(OBJECT_MAPPER.writeValueAsString(body))
                .contentTypeJson()
                .status(Response.Status.OK)
                .build();
        } catch(ZoneRulesException ignore) {
            return APIGatewayProxyResponseBuilder.createErrorResponse(Response.Status.BAD_REQUEST, INVALID_TIMEZONE);
        } catch(DateTimeException ignore) {
            return APIGatewayProxyResponseBuilder.createErrorResponse(Response.Status.BAD_REQUEST , INVALID_TARGET_DATE);
        } catch(JsonProcessingException exception) {
            context.getLogger().log(exception.getMessage());
            return APIGatewayProxyResponseBuilder.createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, null);
        }
    }
}