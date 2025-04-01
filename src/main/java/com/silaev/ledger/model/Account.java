package com.silaev.ledger.model;

import java.util.Objects;
import java.util.UUID;

public record Account(
    UUID id
    // TODO: Some other fields like accountType etc are omitted for the sake of simplicity
) {
    public Account {
        Objects.requireNonNull(id, "id must not be null");
    }
}
