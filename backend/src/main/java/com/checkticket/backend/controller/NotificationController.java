package com.checkticket.backend.controller;

import com.checkticket.backend.dto.NotificationLogDto;
import com.checkticket.backend.service.NotificationService;
import com.checkticket.backend.utils.SecurityUtils;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationLogDto> list() {
        return notificationService.listForUser(SecurityUtils.currentUserId());
    }
}
