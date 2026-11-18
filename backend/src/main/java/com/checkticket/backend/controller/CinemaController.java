package com.checkticket.backend.controller;

import com.checkticket.backend.dto.CinemaDto;
import com.checkticket.backend.dto.ShowtimeDto;
import com.checkticket.backend.service.CinemaService;
import com.checkticket.backend.service.ShowtimeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;
    private final ShowtimeService showtimeService;

    public CinemaController(CinemaService cinemaService, ShowtimeService showtimeService) {
        this.cinemaService = cinemaService;
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public List<CinemaDto> list() {
        return cinemaService.listAll();
    }

    @GetMapping("/{id}")
    public CinemaDto get(@PathVariable Long id) {
        return cinemaService.getById(id);
    }

    @GetMapping("/{id}/showtimes")
    public List<ShowtimeDto> showtimes(@PathVariable Long id) {
        return showtimeService.listByCinema(id);
    }
}
