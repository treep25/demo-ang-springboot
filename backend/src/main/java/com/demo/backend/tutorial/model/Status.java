package com.demo.backend.tutorial.model;

import java.util.Arrays;

public enum Status {
    PENDING, PUBLISHED;

    public static Status getStatusFromText(String status) {
        return Status.valueOf(status.toUpperCase());
    }

    public static boolean iStatusValidFromText(String status) {
        return Arrays
                .stream(Status.values())
                .map(Object::toString)
                .toList()
                .contains(status.toUpperCase());
    }
}
