package com.checkticket.backend.service;

import com.checkticket.backend.dto.NotificationLogDto;
import com.checkticket.backend.entity.CinemaAlert;
import com.checkticket.backend.entity.NotificationChannel;
import com.checkticket.backend.entity.NotificationLog;
import com.checkticket.backend.repository.NotificationLogRepository;
import com.checkticket.backend.utils.Mappers;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationLogDto> listForUser(Long userId) {
        return notificationLogRepository.findByAlert_User_Id(userId).stream()
                .map(Mappers::toDto)
                .toList();
    }

    @Transactional
    public void createLog(CinemaAlert alert, NotificationChannel channel, String summary) {
        NotificationLog log = new NotificationLog();
        log.setAlert(alert);
        log.setChannel(channel);
        log.setMessageSummary(summary);
        log.setSentAt(LocalDateTime.now());
        notificationLogRepository.save(log);
    }
}
