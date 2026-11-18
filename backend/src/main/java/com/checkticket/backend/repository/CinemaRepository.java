package com.checkticket.backend.repository;

import com.checkticket.backend.entity.Cinema;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CinemaRepository extends CrudRepository<Cinema, Long> {

    Optional<Cinema> findByExternalOsmId(String externalOsmId);
}
