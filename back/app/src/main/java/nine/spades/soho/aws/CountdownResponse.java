package nine.spades.soho.aws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CountdownResponse {
    private final int days;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final String targetDate;
    private final String timezone;

    @JsonCreator
    public CountdownResponse(
            @JsonProperty("days") int days,
            @JsonProperty("hours") int hours,
            @JsonProperty("minutes") int minutes,
            @JsonProperty("seconds") int seconds,
            @JsonProperty("targetDate") String targetDate,
            @JsonProperty("timezone") String timezone) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.targetDate = targetDate;
        this.timezone = timezone;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO implementation
        return false;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public String getTimezone() {
        return timezone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int days;
        private int hours;
        private int minutes;
        private int seconds;
        private String targetDate;
        private String timezone;

        public Builder days(int days) {
            this.days = days;
            return this;
        }

        public Builder hours(int hours) {
            this.hours = hours;
            return this;
        }

        public Builder minutes(int minutes) {
            this.minutes = minutes;
            return this;
        }

        public Builder seconds(int seconds) {
            this.seconds = seconds;
            return this;
        }

        public Builder targetDate(String targetDate) {
            this.targetDate = targetDate;
            return this;
        }

        public Builder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public CountdownResponse build() {
            return new CountdownResponse(days, hours, minutes, seconds, targetDate, timezone);
        }
    }
}