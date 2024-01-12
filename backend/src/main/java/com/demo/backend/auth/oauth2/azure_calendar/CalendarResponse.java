package com.demo.backend.auth.oauth2.azure_calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("color")
    private String color;
    @JsonProperty("hexColor")
    private String hexColor;
    @JsonProperty("isDefaultCalendar")
    private boolean isDefaultCalendar;
    @JsonProperty("changeKey")
    private String changeKey;
    @JsonProperty("canShare")
    private boolean canShare;
    @JsonProperty("canViewPrivateItems")
    private boolean canViewPrivateItems;
    @JsonProperty("canEdit")
    private boolean canEdit;
    @JsonProperty("allowedOnlineMeetingProviders")
    private String[] allowedOnlineMeetingProviders;
    @JsonProperty("defaultOnlineMeetingProvider")
    private String defaultOnlineMeetingProvider;
    @JsonProperty("isTallyingResponses")
    private boolean isTallyingResponses;
    @JsonProperty("isRemovable")
    private boolean isRemovable;
    @JsonProperty("owner")
    private Owner owner;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequiredArgsConstructor
    @Data
    public static class Owner {
        @JsonProperty("name")
        private String name;

        @JsonProperty("address")
        private String address;
    }
}
