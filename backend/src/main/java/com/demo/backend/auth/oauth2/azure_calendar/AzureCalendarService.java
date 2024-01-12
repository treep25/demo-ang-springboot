package com.demo.backend.auth.oauth2.azure_calendar;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AzureCalendarService {


    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.client-id}")
    private String clientId;
    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.client-secret}")
    private String clientSecret;
    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.tenant-id}")
    private String tenantId;

    private String redirectUri = "http://localhost:8081/azure/calendar";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String MAIN_USER_CALENDAR_ENDPOINT_API = "https://graph.microsoft.com/v1.0/me/calendarView?startDateTime=%s&endDateTime=%s";
    private static final String GET_MAIN_CALENDAR_BODY_OBJECT = "value";

    DateTimeFormatter formatterAzureDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private static final String CREATE_EVENT_ENDPOINT = "https://graph.microsoft.com/v1.0/me/calendars/";
    private static final String GET_MAIN_CALENDAR_ID_ENDPOINT = "https://graph.microsoft.com/v1.0/me/calendar";


    public List<CalendarEvent> getUserCalendars(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, BEARER + accessToken);

        String startDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String endDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String url = String.format(MAIN_USER_CALENDAR_ENDPOINT_API, startDateTime, endDateTime);

        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(url));
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(requestEntity, String.class);

        Map<String, Object> bodyMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {
        });
        List<Map<String, Object>> calendarEventMaps = (List<Map<String, Object>>) bodyMap.get(GET_MAIN_CALENDAR_BODY_OBJECT);

        List<CalendarEvent> calendarEvents = calendarEventMaps.stream()
                .map(eventMap -> objectMapper.convertValue(eventMap, CalendarEvent.class))
                .toList();

        return calendarEvents;
    }


    public String createEvent(EventToCreateAzureModel eventToCreateAzureModel) throws Exception {
        eventToCreateAzureModel.setCalendarId(getMainCalendarId(eventToCreateAzureModel.getAccessToken()));

        HttpHeaders headers = setUpHeaderBearerToCreateEvent(eventToCreateAzureModel);
        Map<String, Object> requestBody = setUpBodyParamsToCreateEvent(eventToCreateAzureModel);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = CREATE_EVENT_ENDPOINT + eventToCreateAzureModel.getCalendarId() + "/events";

        ResponseEntity<String> responseEntity = new RestTemplate().exchange(new URI(url), HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Event successfully created");
        } else {
            System.out.println("Error during the creating the event: " + responseEntity.getBody());
        }

        return responseEntity.getBody();
    }

    private final Map<Predicate<String>, Supplier<DayOfWeek>> dayOfWeekConstruct = Map.of(
            dayOfWeekString -> dayOfWeekString.equals("Mon"), () -> DayOfWeek.MONDAY,
            dayOfWeekString -> dayOfWeekString.equals("Tue"), () -> DayOfWeek.TUESDAY,
            dayOfWeekString -> dayOfWeekString.equals("Wed"), () -> DayOfWeek.WEDNESDAY,
            dayOfWeekString -> dayOfWeekString.equals("Thu"), () -> DayOfWeek.THURSDAY,
            dayOfWeekString -> dayOfWeekString.equals("Fri"), () -> DayOfWeek.FRIDAY,
            dayOfWeekString -> dayOfWeekString.equals("Sat"), () -> DayOfWeek.SATURDAY,
            dayOfWeekString -> dayOfWeekString.equals("Sun"), () -> DayOfWeek.SUNDAY
    );

    public List<CalendarEvent> getEventsByDayOfWeek(String accessToken, String dayOfWeek) throws URISyntaxException, JsonProcessingException {
        AtomicReference<DayOfWeek> dayOfWeekEnumRepresentation = new AtomicReference<>();

        dayOfWeekConstruct.forEach(
                (dayOfWeekStringPredicate, dayOfWeekEnumSupplier) -> {
                    if (dayOfWeekStringPredicate.test(dayOfWeek)) {
                        dayOfWeekEnumRepresentation.set(dayOfWeekEnumSupplier.get());
                    }
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        String mainCalendarId = getMainCalendarId(accessToken);

        LocalDateTime startOfDay = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(dayOfWeekEnumRepresentation.get())).with(LocalTime.MIN).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endOfDay = startOfDay.with(LocalTime.MAX);

        URI uri = UriComponentsBuilder.fromUriString("https://graph.microsoft.com/v1.0/me/calendars/{mainCalendarId}/events")
                .queryParam("$filter", "start/dateTime ge '{startOfDay}' and start/dateTime lt '{endOfDay}'")
                .buildAndExpand(mainCalendarId, startOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), endOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .toUri();

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = new RestTemplate().exchange(uri, HttpMethod.GET, requestEntity, String.class);


        Map<String, Object> bodyMap = objectMapper.readValue(exchange.getBody(), new TypeReference<>() {
        });

        List<Map<String, Object>> calendarEventMaps = (List<Map<String, Object>>) bodyMap.get(GET_MAIN_CALENDAR_BODY_OBJECT);

        List<CalendarEvent> calendarEvents = calendarEventMaps.stream()
                .map(eventMap -> objectMapper.convertValue(eventMap, CalendarEvent.class))
                .toList();

        return calendarEvents;
    }

    private String getStartOfDay(DayOfWeek dayOfWeek) {
        LocalDateTime startOfDay = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(dayOfWeek)).truncatedTo(ChronoUnit.SECONDS);
        return startOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String getEndOfDay(DayOfWeek dayOfWeek) {
        LocalDateTime endOfDay = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(dayOfWeek)).with(LocalTime.MAX);
        return endOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private HttpHeaders setUpHeaderBearerToCreateEvent(EventToCreateAzureModel eventToCreateAzureModel) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, BEARER + eventToCreateAzureModel.getAccessToken());
        headers.set("Content-Type", "application/json");

        return headers;
    }

    private Map<String, Object> setUpBodyParamsToCreateEvent(EventToCreateAzureModel eventToCreateAzureModel) {
        return Map.of(
                "subject", eventToCreateAzureModel.getSummary(),
                "start", Map.of("dateTime", eventToCreateAzureModel.getStartDate().toInstant().atZone(ZoneId.systemDefault()).format(formatterAzureDateTime), "timeZone", "UTC"),
                "end", Map.of("dateTime", eventToCreateAzureModel.getEndDate().toInstant().atZone(ZoneId.systemDefault()).format(formatterAzureDateTime), "timeZone", "UTC"),
                "location", Map.of("displayName", eventToCreateAzureModel.getLocation())
        );
    }

    private String getMainCalendarId(String token) throws URISyntaxException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(GET_MAIN_CALENDAR_ID_ENDPOINT));

        ResponseEntity<String> responseEntity = new RestTemplate().exchange(requestEntity, String.class);

        CalendarResponse calendarResponse = objectMapper.readValue(responseEntity.getBody(), CalendarResponse.class);

        return calendarResponse.getId();
    }
}
