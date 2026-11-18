package com.checkticket.backend.controller;

import com.checkticket.backend.dto.CinemaDto;
import com.checkticket.backend.dto.ShowtimeDto;
import com.checkticket.backend.repository.CinemaAlertRepository;
import com.checkticket.backend.repository.CinemaRepository;
import com.checkticket.backend.repository.NotificationLogRepository;
import com.checkticket.backend.repository.ShowtimeRepository;
import com.checkticket.backend.repository.UserRepository;
import com.checkticket.backend.service.CinemaService;
import com.checkticket.backend.service.ShowtimeService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final CinemaRepository cinemaRepository;
    private final ShowtimeRepository showtimeRepository;
    private final CinemaAlertRepository alertRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final CinemaService cinemaService;
    private final ShowtimeService showtimeService;

    public AdminController(UserRepository userRepository,
                           CinemaRepository cinemaRepository,
                           ShowtimeRepository showtimeRepository,
                           CinemaAlertRepository alertRepository,
                           NotificationLogRepository notificationLogRepository,
                           CinemaService cinemaService,
                           ShowtimeService showtimeService) {
        this.userRepository = userRepository;
        this.cinemaRepository = cinemaRepository;
        this.showtimeRepository = showtimeRepository;
        this.alertRepository = alertRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.cinemaService = cinemaService;
        this.showtimeService = showtimeService;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
                "users", userRepository.count(),
                "cinemas", cinemaRepository.count(),
                "showtimes", showtimeRepository.count(),
                "alerts", alertRepository.count(),
                "notifications", notificationLogRepository.count());
    }

    @PostMapping("/cinemas")
    public CinemaDto registerCinema(@RequestBody CinemaDto dto) {
        return cinemaService.create(dto);
    }

    @PostMapping("/cinemas/sync")
    public Map<String, Integer> sync(@RequestParam String city) {
        return Map.of("synced", cinemaService.syncFromOverpass(city));
    }

    @PostMapping("/showtimes/simulate")
    public List<ShowtimeDto> simulate(@RequestParam Long cinemaId) {
        return showtimeService.simulate(cinemaId);
    }
}
