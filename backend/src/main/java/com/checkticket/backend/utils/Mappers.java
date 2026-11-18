package com.checkticket.backend.utils;

import com.checkticket.backend.dto.CinemaAlertDto;
import com.checkticket.backend.dto.CinemaDto;
import com.checkticket.backend.dto.NotificationLogDto;
import com.checkticket.backend.dto.ShowtimeDto;
import com.checkticket.backend.entity.Cinema;
import com.checkticket.backend.entity.CinemaAlert;
import com.checkticket.backend.entity.NotificationLog;
import com.checkticket.backend.entity.Showtime;

public final class Mappers {

    private Mappers() {
    }

    public static CinemaDto toDto(Cinema cinema) {
        return new CinemaDto(cinema.getId(), cinema.getExternalOsmId(), cinema.getName(),
                cinema.getCity(), cinema.getZone(), cinema.getLatitude(), cinema.getLongitude());
    }

    public static ShowtimeDto toDto(Showtime showtime) {
        return new ShowtimeDto(showtime.getId(), showtime.getCinema().getId(), showtime.getTmdbMovieId(),
                showtime.getMovieTitle(), showtime.getDayOfWeek(), showtime.getSessionTime(),
                showtime.getPrice(), showtime.getSeatsAvailable(), showtime.getUpdatedAt());
    }

    public static CinemaAlertDto toDto(CinemaAlert alert) {
        Long cinemaId = alert.getCinema() != null ? alert.getCinema().getId() : null;
        return new CinemaAlertDto(alert.getId(), alert.getUser().getId(), cinemaId, alert.getMovieTitle(),
                alert.getTargetDay(), alert.getMaxPrice(), alert.isNotifyEmail(), alert.isNotifyTelegram(),
                alert.isActive(), alert.getLastNotifiedHash());
    }

    public static NotificationLogDto toDto(NotificationLog log) {
        return new NotificationLogDto(log.getId(), log.getAlert().getId(), log.getChannel().name(),
                log.getMessageSummary(), log.getSentAt());
    }
}
