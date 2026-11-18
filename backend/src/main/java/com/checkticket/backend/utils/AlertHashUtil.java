package com.checkticket.backend.utils;

import java.nio.charset.StandardCharsets;
import org.springframework.util.DigestUtils;

public final class AlertHashUtil {

    private AlertHashUtil() {
    }

    /** Hash estable para evitar envíos duplicados de una misma alerta/sesión. */
    public static String hash(Long alertId, Long showtimeId) {
        return DigestUtils.md5DigestAsHex((alertId + ":" + showtimeId).getBytes(StandardCharsets.UTF_8));
    }
}
