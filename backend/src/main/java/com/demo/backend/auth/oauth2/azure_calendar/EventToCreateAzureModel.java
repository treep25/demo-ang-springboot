package com.demo.backend.auth.oauth2.azure_calendar;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Data
public class EventToCreateAzureModel {
    private String calendarId;
    private String subject;
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;
    private String location;
    private String accessToken;
}
