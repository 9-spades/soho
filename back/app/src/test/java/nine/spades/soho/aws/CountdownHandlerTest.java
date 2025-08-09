package nine.spades.soho.aws;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.*;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import nine.spades.utils.CorsHeaders;

import static nine.spades.utils.APIGatewayProxyResponseAssert.assertThat;

class CountdownHandlerTest {
    public static final String INVALID_DATE = "invalid-date";
    public static final String INVALID_TIMEZONE = "Invalid/Timezone";
    private static final CountdownHandler HANDLER = new CountdownHandler();

    private APIGatewayProxyResponseEvent response;
    private APIGatewayProxyRequestEvent request;
    @Mock private Context context;
    @Mock private LambdaLogger logger;

    public static String testTargetDate(int offset) {
        return ZonedDateTime.now(ZoneOffset.UTC).plusDays(offset).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private APIGatewayProxyResponseEvent givenWhen(String targetDate, String timezone) {
        Map<String, String> queryParams = new HashMap<>();
        if(targetDate != null) queryParams.put(CountdownHandler.TARGET_DATE, targetDate);
        if(timezone != null) queryParams.put(CountdownHandler.TIMEZONE, timezone);
        request.setQueryStringParameters(queryParams);
        return HANDLER.handleRequest(request, context);
    }

    @BeforeEach void setUp() {
        request = new APIGatewayProxyRequestEvent();
        MockitoAnnotations.openMocks(this);
        Mockito.when(context.getLogger()).thenReturn(logger);
    }

    @Test void shouldHandleValidRequest() throws Exception {
        response = givenWhen(testTargetDate(+1), null);

        // Then
        assertThat(response).isOk().hasCorsHeaders().hasHeader(MediaType.APPLICATION_JSON).hasValidJsonBody()
            .bodyContains("\"timezone\": \"UTC\"")
            .bodyMatches("\"days\": \\d+")
            .bodyMatches("\"hours\": \\d+")
            .bodyMatches("\"minutes\": \\d+")
            .bodyMatches("\"seconds\": \\d+");
    }

    @Test void shouldHandleMissingTargetDate() {
        assertThat(HANDLER.handleRequest(request, context)).isBadRequest().hasBody(CountdownHandler.MISSING_TARGET_DATE);
    }

    @Test void shouldHandleInvalidDateFormat() {
        assertThat(givenWhen(INVALID_DATE, null)).isBadRequest().hasBody(CountdownHandler.INVALID_TARGET_DATE);
    }

    @Test void shouldHandleInvalidTimezone() {
        assertThat(givenWhen(testTargetDate(+1), INVALID_TIMEZONE)).isBadRequest().hasBody(CountdownHandler.INVALID_TIMEZONE);
    }

    @Test void shouldHandleOptionsRequest() {
        // Given
        request.setHttpMethod(HttpMethod.OPTIONS);

        // When Then
        assertThat(HANDLER.handleRequest(request, context)).isOk().hasCorsHeaders()
            .hasHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.format("%s, %s", HttpMethod.GET, HttpMethod.OPTIONS))
            .hasHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.format("%s, %s", HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION));
    }

    @Test void shouldHandleExpiredCountdown() throws Exception {
        assertThat(givenWhen(testTargetDate(-1), null)).isOk().hasHeader(MediaType.APPLICATION_JSON)
            .hasJsonField("/days", "0")
            .hasJsonField("/hours", "0")
            .hasJsonField("/minutes", "0")
            .hasJsonField("/seconds", "0");
    }

    @Test void shouldHandleDifferentTimezoneFormats() throws Exception {
        assertThat(givenWhen(testTargetDate(0), CountdownResponseTest.TIMEZONE)).isOk().hasHeader(MediaType.APPLICATION_JSON)
            .hasJsonField("/timezone", CountdownResponseTest.TIMEZONE);
    }
}