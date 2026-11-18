package com.checkticket.backend.repository;

import com.checkticket.backend.entity.Showtime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ShowtimeRepository extends CrudRepository<Showtime, Long> {

    List<Showtime> findByCinemaId(Long cinemaId);
}
