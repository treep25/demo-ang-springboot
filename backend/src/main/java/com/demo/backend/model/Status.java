package com.demo.backend.model;

import java.util.Arrays;

public enum Status {
    PENDING, PUBLISHED;

    public static Status fromBoolean(boolean published) {
        return published ? PUBLISHED : PENDING;
    }

    public static Status fromTextStatus(String published) {
        return Status.valueOf(published.toUpperCase());
    }

    public static boolean fromTextStatusValid(String published) {
        return Arrays
                .stream(Status.values())
                .map(Object::toString)
                .toList()
                .contains(published.toUpperCase());
    }
}
