package nine.spades.time;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountdownTest {
    public static final String TEST_DATA_FILE = "/countdown-test-data.json";
    public static final String[] TEST_CASE_FIELDS = {
        "testName",
        "nowZonedDateTime",
        "targetZonedDateTime",
        "expected"
    };
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static Stream<Object[]> timeWindows() throws Exception {        
        return Stream.of(OBJECT_MAPPER.readTree(new String(CountdownTest.class.getResourceAsStream(TEST_DATA_FILE).readAllBytes(), StandardCharsets.UTF_8)).iterator())
            .flatMap(nodeIterator -> {
                Stream.Builder<Object[]> builder = Stream.builder();
                nodeIterator.forEachRemaining(testCase -> {
                    String testName = testCase.get(TEST_CASE_FIELDS[0]).asText();
                    ZonedDateTime now = ZonedDateTime.parse(testCase.get(TEST_CASE_FIELDS[1]).asText());
                    ZonedDateTime target = ZonedDateTime.parse(testCase.get(TEST_CASE_FIELDS[2]).asText());
                    List<Integer> expected = OBJECT_MAPPER.convertValue(testCase.get(TEST_CASE_FIELDS[3]), new TypeReference<List<Integer>>() {});
                    builder.add(new Object[]{testName, now, target, expected});
                });
                return builder.build();
            });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("timeWindows")
    void shouldComputeTimeUntilTarget(String testName, ZonedDateTime now, ZonedDateTime target, List<Integer> expected) {
        assertEquals(expected, Countdown.compute(now, target));
    }
}