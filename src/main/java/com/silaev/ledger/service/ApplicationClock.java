package com.silaev.ledger.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ApplicationClock {
    public Instant now() {
        return Instant.now();
    }
}
