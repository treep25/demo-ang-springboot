package com.demo.backend.request;

import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RequestProcessingService {

    private final UserRepository userRepository;
    private final RequestProcessingRepository requestRepository;

    public void createRequest(RequestDto requestDto) {
        Request request = Request
                .builder()
                .status(RequestStatus.OPEN)
                .issue(requestDto.getIssue())
                .build();

        User user = Stream.of(userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(RuntimeException::new)).filter(user1 -> !user1.isEnabled()).toList().get(0);
        request.setUser(user);

        requestRepository.save(request);
    }

    @Scheduled(cron = "0 0 0 * * 1")
    private void cleanUpFromClosedRequestOneTimeInWeek() {
        requestRepository.deleteAllByStatusIsClosed();
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }


    public void closeRequest(long id) {
        Request byId = requestRepository.findById(id).orElseThrow(RuntimeException::new);
        byId.setStatus(RequestStatus.CLOSED);
        requestRepository.save(byId);
    }

    public void canceleRequest(long id) {
        Request byId = requestRepository.findById(id).orElseThrow(RuntimeException::new);
        byId.setStatus(RequestStatus.CANCELED);
        requestRepository.save(byId);
    }
}
