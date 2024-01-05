package com.demo.backend.auth.oauth2.azure_calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarEvent {
    private String id;
    private String eTag;
    private String createdDateTime;
    private String lastModifiedDateTime;
    private List<Attendee> attendees;
    private String changeKey;
    private List<String> categories;
    private String originalStartTimeZone;
    private String originalEndTimeZone;
    private String iCalUId;
    private int reminderMinutesBeforeStart;
    private boolean isReminderOn;
    private boolean hasAttachments;
    private String subject;
    private String bodyPreview;
    private String importance;
    private String sensitivity;
    private boolean isAllDay;
    private boolean isCancelled;
    private boolean responseRequested;
    private String seriesMasterId;
    private String showAs;
    private String type;
    private String webLink;
    private String onlineMeetingUrl;
    private boolean isOnlineMeeting;
    private String onlineMeetingProvider;
    private boolean allowNewTimeProposals;
    private String occurrenceId;
    private boolean isDraft;
    private boolean hideAttendees;
    private ResponseStatus responseStatus;
    private Body body;
    private StartEnd start;
    private StartEnd end;
    private Location location;
    private List<Location> locations;
    private Organizer organizer;
    private Object onlineMeeting;

}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Attendee {
    private EmailAddress emailAddress;
}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseStatus {
    private String response;
    private String time;
}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Body {
    private String contentType;
    private String content;
}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class StartEnd {
    private String dateTime;
    private String timeZone;
}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Location {
    private String displayName;
    private String locationType;
    private String uniqueId;
    private String uniqueIdType;
}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Organizer {
    private EmailAddress emailAddress;
}

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class EmailAddress {
    private String name;
    private String address;
}