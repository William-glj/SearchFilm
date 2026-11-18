package com.checkticket.backend.repository;

import com.checkticket.backend.entity.CinemaAlert;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CinemaAlertRepository extends CrudRepository<CinemaAlert, Long> {

    List<CinemaAlert> findByUserId(Long userId);

    Optional<CinemaAlert> findByIdAndUserId(Long id, Long userId);

    List<CinemaAlert> findByActiveTrue();
}
