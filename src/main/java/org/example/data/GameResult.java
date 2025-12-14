package org.example.data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GameResult {
    public int gold;
    public long time;

    public GameResult() {}

    public GameResult(int gold) {
        this.gold = gold;
        this.time = System.currentTimeMillis();
    }

    @JsonIgnore
    public String getFormattedTime() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(time),
                ZoneId.systemDefault()
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return dateTime.format(formatter);
    }

}
