package com.silaev.ledger.response;

import java.util.Objects;
import java.util.UUID;

public record AccountResponse(
    UUID id
) {
    public AccountResponse {
        Objects.requireNonNull(id, "id must not be null");
    }
}
