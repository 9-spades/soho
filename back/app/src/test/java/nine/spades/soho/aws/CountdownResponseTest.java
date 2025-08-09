package nine.spades.soho.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountdownResponseTest {
    public static final int DAYS = 365;
    public static final int HOURS = 12;
    public static final int MINUTES = 30;
    public static final int SECONDS = 45;
    public static final String TARGET_DATE = "2024-12-31T23:59:59";
    public static final String TIMEZONE = "America/New_York";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CountdownResponse response;

    public static CountdownResponse countdownResponse() {
        return new CountdownResponse(DAYS, HOURS, MINUTES, SECONDS, TARGET_DATE, TIMEZONE);
    }

    @Test void shouldSerializeToJsonCorrectly() throws Exception {
        // Given
        response = countdownResponse();

        // When
        String json = OBJECT_MAPPER.writeValueAsString(response);

        // Then
        assertNotNull(json);
        assertAll(
            () -> assertTrue(json.contains("\"days\":"+DAYS)),
            () -> assertTrue(json.contains("\"hours\":"+HOURS)),
            () -> assertTrue(json.contains("\"minutes\":"+MINUTES)),
            () -> assertTrue(json.contains("\"seconds\":"+SECONDS)),
            () -> assertTrue(json.contains(String.format("\"targetDate\":\"%s\"", TARGET_DATE))),
            () -> assertTrue(json.contains(String.format("\"timezone\":\"%s\"", TIMEZONE)))
        );
    }

    @Test void shouldDeserializeFromJsonCorrectly() throws Exception {
        // Given
        String json = String.format("""
            {
                "days": %d,
                "hours": %d,
                "minutes": %d,
                "seconds": %d,
                "targetDate": "%s",
                "timezone": "%s"
            }
            """, DAYS, HOURS, MINUTES, SECONDS, TARGET_DATE, TIMEZONE);

        // When
        response = OBJECT_MAPPER.readValue(json, CountdownResponse.class);

        // Then
        assertEquals(countdownResponse(), response);
    }

    @Test void shouldValidateBuilderPattern() {
        // Given & When
        response = CountdownResponse.builder()
            .days(DAYS).hours(HOURS).minutes(MINUTES).seconds(SECONDS).targetDate(TARGET_DATE).timezone(TIMEZONE).build();

        // Then
        assertEquals(countdownResponse(), response);
    }
}