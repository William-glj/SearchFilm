package com.checkticket.backend.controller;

import com.checkticket.backend.dto.AlertRequest;
import com.checkticket.backend.dto.CinemaAlertDto;
import com.checkticket.backend.service.AlertService;
import com.checkticket.backend.utils.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<CinemaAlertDto> list() {
        return alertService.listForUser(SecurityUtils.currentUserId());
    }

    @PostMapping
    public CinemaAlertDto create(@Valid @RequestBody AlertRequest request) {
        return alertService.create(SecurityUtils.currentUserId(), request);
    }

    @PutMapping("/{id}")
    public CinemaAlertDto update(@PathVariable Long id, @Valid @RequestBody AlertRequest request) {
        return alertService.update(SecurityUtils.currentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertService.delete(SecurityUtils.currentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
