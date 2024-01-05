package com.demo.backend.auth.oauth2.google_calendar;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Data
public class EventToCreateModel {
    private String summary;
    private Date startDate;
    private Date endDate;
    private String token;
}
