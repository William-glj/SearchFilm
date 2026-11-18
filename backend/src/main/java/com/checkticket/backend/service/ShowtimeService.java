package com.checkticket.backend.service;

import com.checkticket.backend.dto.ShowtimeDto;
import com.checkticket.backend.entity.Cinema;
import com.checkticket.backend.entity.Showtime;
import com.checkticket.backend.repository.CinemaRepository;
import com.checkticket.backend.repository.ShowtimeRepository;
import com.checkticket.backend.utils.Mappers;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShowtimeService {

    private static final String[] MOVIE_TITLES = {
            "Dune: Parte Dos", "Oppenheimer", "Barbie", "La Sociedad de la Nieve"
    };

    private final ShowtimeRepository showtimeRepository;
    private final CinemaRepository cinemaRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, CinemaRepository cinemaRepository) {
        this.showtimeRepository = showtimeRepository;
        this.cinemaRepository = cinemaRepository;
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDto> listByCinema(Long cinemaId) {
        return showtimeRepository.findByCinemaId(cinemaId).stream()
                .map(Mappers::toDto)
                .toList();
    }

    /** Simulador de sesiones del administrador (fallback sin scraping externo). */
    @Transactional
    public List<ShowtimeDto> simulate(Long cinemaId) {
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema not found"));

        LocalTime[] times = {LocalTime.of(17, 0), LocalTime.of(19, 30), LocalTime.of(22, 0)};
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Showtime> created = new ArrayList<>();

        for (String title : MOVIE_TITLES) {
            for (DayOfWeek day : DayOfWeek.values()) {
                for (LocalTime time : times) {
                    Showtime showtime = new Showtime();
                    showtime.setCinema(cinema);
                    showtime.setMovieTitle(title);
                    showtime.setDayOfWeek(day.name());
                    showtime.setSessionTime(time);
                    showtime.setPrice(BigDecimal.valueOf(random.nextDouble(6.0, 12.0))
                            .setScale(2, RoundingMode.HALF_UP));
                    showtime.setSeatsAvailable(random.nextInt(60, 120));
                    created.add(showtimeRepository.save(showtime));
                }
            }
        }
        return created.stream().map(Mappers::toDto).toList();
    }
}
