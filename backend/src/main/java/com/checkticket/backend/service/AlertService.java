package com.checkticket.backend.service;

import com.checkticket.backend.dto.AlertRequest;
import com.checkticket.backend.dto.CinemaAlertDto;
import com.checkticket.backend.entity.CinemaAlert;
import com.checkticket.backend.entity.Showtime;
import com.checkticket.backend.entity.User;
import com.checkticket.backend.repository.CinemaAlertRepository;
import com.checkticket.backend.repository.CinemaRepository;
import com.checkticket.backend.repository.ShowtimeRepository;
import com.checkticket.backend.repository.UserRepository;
import com.checkticket.backend.utils.AlertHashUtil;
import com.checkticket.backend.utils.Mappers;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlertService {

    private final CinemaAlertRepository alertRepository;
    private final UserRepository userRepository;
    private final CinemaRepository cinemaRepository;
    private final ShowtimeRepository showtimeRepository;
    private final NotificationDispatcher notificationDispatcher;

    public AlertService(CinemaAlertRepository alertRepository,
                        UserRepository userRepository,
                        CinemaRepository cinemaRepository,
                        ShowtimeRepository showtimeRepository,
                        NotificationDispatcher notificationDispatcher) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.cinemaRepository = cinemaRepository;
        this.showtimeRepository = showtimeRepository;
        this.notificationDispatcher = notificationDispatcher;
    }

    @Transactional(readOnly = true)
    public List<CinemaAlertDto> listForUser(Long userId) {
        return alertRepository.findByUserId(userId).stream()
                .map(Mappers::toDto)
                .toList();
    }

    @Transactional
    public CinemaAlertDto create(Long userId, AlertRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CinemaAlert alert = new CinemaAlert();
        applyRequest(alert, request);
        alert.setUser(user);
        alert.setActive(true);
        return Mappers.toDto(alertRepository.save(alert));
    }

    @Transactional
    public CinemaAlertDto update(Long userId, Long id, AlertRequest request) {
        CinemaAlert alert = alertRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        applyRequest(alert, request);
        return Mappers.toDto(alert);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        CinemaAlert alert = alertRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alertRepository.delete(alert);
    }

    /** Motor de alertas programado: busca coincidencias y notifica (con dedup por hash). */
    @Scheduled(fixedDelayString = "${app.alerts.delay-ms:300000}",
               initialDelayString = "${app.alerts.initial-delay-ms:30000}")
    @Transactional
    public void runAlertEngine() {
        List<Showtime> showtimes = new ArrayList<>();
        showtimeRepository.findAll().forEach(showtimes::add);

        for (CinemaAlert alert : alertRepository.findByActiveTrue()) {
            for (Showtime showtime : showtimes) {
                if (!matches(alert, showtime)) {
                    continue;
                }
                String hash = AlertHashUtil.hash(alert.getId(), showtime.getId());
                if (hash.equals(alert.getLastNotifiedHash())) {
                    continue;
                }
                notificationDispatcher.dispatch(alert, showtime);
                alert.setLastNotifiedHash(hash);
                alertRepository.save(alert);
            }
        }
    }

    private void applyRequest(CinemaAlert alert, AlertRequest request) {
        alert.setMovieTitle(request.movieTitle());
        alert.setTargetDay(request.targetDay());
        alert.setMaxPrice(request.maxPrice());
        alert.setNotifyEmail(request.notifyEmail() == null || request.notifyEmail());
        alert.setNotifyTelegram(Boolean.TRUE.equals(request.notifyTelegram()));
        if (request.cinemaId() != null) {
            alert.setCinema(cinemaRepository.findById(request.cinemaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema not found")));
        } else {
            alert.setCinema(null);
        }
    }

    private boolean matches(CinemaAlert alert, Showtime showtime) {
        if (!alert.getMovieTitle().equalsIgnoreCase(showtime.getMovieTitle())) {
            return false;
        }
        if (alert.getTargetDay() != null && !alert.getTargetDay().equalsIgnoreCase(showtime.getDayOfWeek())) {
            return false;
        }
        if (alert.getCinema() != null && !alert.getCinema().getId().equals(showtime.getCinema().getId())) {
            return false;
        }
        if (alert.getMaxPrice() != null && showtime.getPrice() != null
                && showtime.getPrice().compareTo(alert.getMaxPrice()) > 0) {
            return false;
        }
        return true;
    }
}
