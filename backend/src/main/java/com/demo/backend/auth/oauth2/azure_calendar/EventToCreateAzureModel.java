package com.demo.backend.auth.oauth2.azure_calendar;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Data
public class EventToCreateAzureModel {
    private String calendarId;
    private String summary;
    private Date startDate;
    private Date endDate;
    private String location;
    private String accessToken;
}
