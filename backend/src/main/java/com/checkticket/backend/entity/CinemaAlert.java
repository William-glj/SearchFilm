package com.checkticket.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cinema_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "target_day")
    private String targetDay;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "notify_email", nullable = false)
    private boolean notifyEmail = true;

    @Column(name = "notify_telegram", nullable = false)
    private boolean notifyTelegram = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_notified_hash")
    private String lastNotifiedHash;
}
