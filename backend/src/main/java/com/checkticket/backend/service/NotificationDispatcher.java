package com.checkticket.backend.service;

import com.checkticket.backend.entity.Cinema;
import com.checkticket.backend.entity.CinemaAlert;
import com.checkticket.backend.entity.NotificationChannel;
import com.checkticket.backend.entity.Showtime;
import com.checkticket.backend.entity.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final NotificationService notificationService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String telegramBotToken;

    public NotificationDispatcher(NotificationService notificationService,
                                  ObjectProvider<JavaMailSender> mailSenderProvider,
                                  @Value("${app.telegram.bot-token:}") String telegramBotToken) {
        this.notificationService = notificationService;
        this.mailSenderProvider = mailSenderProvider;
        this.telegramBotToken = telegramBotToken;
    }

    public void dispatch(CinemaAlert alert, Showtime showtime) {
        String summary = buildSummary(alert, showtime);
        User user = alert.getUser();

        if (alert.isNotifyEmail()) {
            sendEmail(user.getEmail(), summary);
            notificationService.createLog(alert, NotificationChannel.EMAIL, summary);
        }
        if (alert.isNotifyTelegram() && user.getTelegramChatId() != null) {
            sendTelegram(user.getTelegramChatId(), summary);
            notificationService.createLog(alert, NotificationChannel.TELEGRAM, summary);
        }
    }

    private String buildSummary(CinemaAlert alert, Showtime showtime) {
        Cinema cinema = showtime.getCinema();
        return String.format("%s — %s, %s a las %s (%.2f €)",
                showtime.getMovieTitle(),
                cinema.getName(),
                showtime.getDayOfWeek(),
                showtime.getSessionTime(),
                showtime.getPrice() == null ? 0.0 : showtime.getPrice());
    }

    private void sendEmail(String to, String summary) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.info("SMTP no configurado: email simulado a {}", to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("CheckTicket — Tu alerta de cine");
            message.setText(summary);
            sender.send(message);
        } catch (Exception e) {
            log.warn("Error enviando email a {}: {}", to, e.getMessage());
        }
    }

    private void sendTelegram(String chatId, String summary) {
        if (telegramBotToken == null || telegramBotToken.isBlank()) {
            log.info("Telegram no configurado: mensaje simulado a {}", chatId);
            return;
        }
        try {
            String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";
            Map<String, String> body = Map.of("chat_id", chatId, "text", summary);
            RestClient.create()
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Error enviando telegram a {}: {}", chatId, e.getMessage());
        }
    }
}
