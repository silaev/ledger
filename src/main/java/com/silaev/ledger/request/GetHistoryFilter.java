package com.silaev.ledger.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silaev.ledger.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;


@ValidDateRange
public record GetHistoryFilter(@NotNull UUID accountId,
                               @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN) LocalDateTime from,
                               @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN) LocalDateTime to,
                               @NotNull ZoneId zoneId) {

    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private Instant toUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneOffset.UTC).withZoneSameLocal(zoneId()).toInstant();
    }

    public Instant toInUTC() {
        return toUTC(to());
    }

    public Instant fromInUTC() {
        return toUTC(from());
    }
}
