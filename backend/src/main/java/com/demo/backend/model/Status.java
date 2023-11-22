package com.demo.backend.model;

public enum Status {
    PENDING, PUBLISHED;

    public static Status fromBoolean(boolean published) {
        return published ? PUBLISHED : PENDING;
    }
}
