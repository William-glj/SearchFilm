-- =====================================================================
-- CheckTicket — Modelo relacional (PostgreSQL 17)
-- Entidades: users, cinemas, showtimes, cinema_alerts, notification_logs
-- =====================================================================

CREATE TABLE IF NOT EXISTS users (
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255),                -- cifrada BCrypt; NULL para login Google OAuth2
    full_name        VARCHAR(255) NOT NULL,
    role             VARCHAR(32)  NOT NULL,       -- ROLE_USER | ROLE_ADMIN
    telegram_chat_id VARCHAR(255),                -- opcional, notificaciones móviles
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cinemas (
    id              BIGSERIAL PRIMARY KEY,
    external_osm_id VARCHAR(255) NOT NULL UNIQUE, -- evita duplicados de OpenStreetMap
    name            VARCHAR(255) NOT NULL,
    city            VARCHAR(255),
    zone            VARCHAR(255),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS showtimes (
    id              BIGSERIAL PRIMARY KEY,
    cinema_id       BIGINT        NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    tmdb_movie_id   BIGINT,                       -- enlaza metadatos de TMDB
    movie_title     VARCHAR(255)  NOT NULL,
    day_of_week     VARCHAR(16)   NOT NULL,       -- MONDAY..SUNDAY
    session_time    TIME          NOT NULL,
    price           NUMERIC(10, 2),
    seats_available INTEGER       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cinema_alerts (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    cinema_id          BIGINT        REFERENCES cinemas(id)          ON DELETE SET NULL,
    movie_title        VARCHAR(255)  NOT NULL,
    target_day         VARCHAR(16),             -- día objetivo de la semana
    max_price          NUMERIC(10, 2),          -- opcional
    notify_email       BOOLEAN       NOT NULL DEFAULT TRUE,
    notify_telegram    BOOLEAN       NOT NULL DEFAULT FALSE,
    active             BOOLEAN       NOT NULL DEFAULT TRUE,
    last_notified_hash VARCHAR(255)             -- evita envíos duplicados en @Scheduled
);

CREATE TABLE IF NOT EXISTS notification_logs (
    id              BIGSERIAL PRIMARY KEY,
    alert_id        BIGINT        NOT NULL REFERENCES cinema_alerts(id) ON DELETE CASCADE,
    channel         VARCHAR(16)   NOT NULL,     -- EMAIL | TELEGRAM
    message_summary VARCHAR(500),
    sent_at         TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_showtimes_cinema       ON showtimes(cinema_id);
CREATE INDEX IF NOT EXISTS idx_alerts_user            ON cinema_alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_alerts_cinema          ON cinema_alerts(cinema_id);
CREATE INDEX IF NOT EXISTS idx_notification_logs_alert ON notification_logs(alert_id);
