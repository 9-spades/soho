package nine.spades.time;

import java.time.*;
import java.util.List;
import java.util.stream.*;

public class Countdown {
    public static final int[] T = {1, 60, 3600, 86400, Integer.MAX_VALUE};

    // List<Integer> ~ {seconds, minutes, hours, days}
    public static List<Integer> compute(LocalDateTime now, LocalDateTime target) {
        long t = target.isAfter(now) ? Duration.between(now, target).getSeconds() : 0L;
        return IntStream.range(1, 5).map(i -> (int) ((t%T[i])/T[i-1])).boxed().collect(Collectors.toList());
    }

    public static List<Integer> compute(ZonedDateTime now, ZonedDateTime target) {
        return compute(now.toLocalDateTime(), target.withZoneSameInstant(now.getZone()).toLocalDateTime());
    }
}