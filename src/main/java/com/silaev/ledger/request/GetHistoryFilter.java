package com.silaev.ledger.request;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;


public record GetHistoryFilter(UUID accountId, LocalDateTime from, LocalDateTime to, ZoneId zoneId) {

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
