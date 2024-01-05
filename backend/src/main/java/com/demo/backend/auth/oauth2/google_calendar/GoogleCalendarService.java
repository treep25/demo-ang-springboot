package com.demo.backend.auth.oauth2.google_calendar;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class GoogleCalendarService {

    private Calendar calndarApi;

    private Calendar newCalendarServiceApi(String accessToken) throws GeneralSecurityException, IOException {
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                jsonFactory,
                null
        )
                .setHttpRequestInitializer(request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                .setApplicationName("Spring boot Angular OAuth2")
                .build();
    }


    private List<CalendarListEntry> getCalendarList() throws IOException {
        CalendarList calendarList = calndarApi.calendarList().list().execute();
        return calendarList.getItems();
    }


    private CalendarListEntry findPrimaryCalendar(List<CalendarListEntry> calendarList) {
        for (CalendarListEntry calendar : calendarList) {
            if (Boolean.TRUE.equals(calendar.getPrimary())) {
                return calendar;
            }
        }
        return null;
    }

    private List<Event> getEventsForTheWeekMethod(String calendarId) throws IOException {
        Date now = new Date();
        ZonedDateTime zonedDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0).atZone(ZoneId.systemDefault());


        Events events = calndarApi.events().list(calendarId)
                .setTimeMin(new DateTime(Date.from(zonedDateTime.toInstant())))
                .execute();

        return events.getItems();
    }

    public List<Event> getEventsForTheWeek(String token) throws GeneralSecurityException, IOException {
        calndarApi = newCalendarServiceApi(token);

        List<CalendarListEntry> calendarList = getCalendarList();

        CalendarListEntry primaryCalendar = findPrimaryCalendar(calendarList);

        if (primaryCalendar != null) {
            return getEventsForTheWeekMethod(primaryCalendar.getId());
        }
        return null;
    }

    public Event createEvent(EventToCreateModel eventToCreateModel) throws GeneralSecurityException, IOException {
        calndarApi = newCalendarServiceApi(eventToCreateModel.getToken());

        Event event = new Event()
                .setSummary(eventToCreateModel.getSummary())
                .setDescription(eventToCreateModel.getSummary());

        DateTime startDateTime = new DateTime(eventToCreateModel.getStartDate());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Kiev");
        event.setStart(start);

        DateTime endDateTime = new DateTime(eventToCreateModel.getEndDate());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Kiev");
        event.setEnd(end);

        String calendarId = "primary";
        event = calndarApi.events().insert(calendarId, event).execute();

        return event;
    }

    public List<Event> getCalendarByDay(String day, String googleAccessTokenNotRequered) throws GeneralSecurityException, IOException {
        calndarApi = newCalendarServiceApi(googleAccessTokenNotRequered);

        Date currentDate = new Date();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(currentDate);

        switch (day.toLowerCase()) {
            case "mon" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
            case "tue" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.TUESDAY);
            case "wed" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.WEDNESDAY);
            case "thu" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.THURSDAY);
            case "fri" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.FRIDAY);
            case "sat" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SATURDAY);
            case "sun" -> calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
            default -> {
                return Collections.emptyList();
            }
        }
        Date startOfDay = calendar.getTime();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = calendar.getTime();

        Events events = calndarApi.events().list("primary")
                .setTimeMin(new DateTime(startOfDay))
                .setTimeMax(new DateTime(endOfDay))
                .execute();

        return events.getItems();

    }
}
