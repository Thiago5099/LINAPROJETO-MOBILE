package com.example.projeto.Feature.Login;

import android.content.Context;

/**
 * Header {@code Authorization} usado nas chamadas Retrofit autenticadas.
 */
public final class ApiAuthHeaders {

    private ApiAuthHeaders() {}

    /** {@code null} se não houver token. */
    public static String bearerOrNull(Context ctx) {
        if (ctx == null) return null;
        String raw = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", "");
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        raw = raw.trim();
        if (raw.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return raw;
        }
        return "Bearer " + raw;
    }
}
