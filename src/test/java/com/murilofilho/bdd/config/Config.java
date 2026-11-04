package com.murilofilho.bdd.config;

import java.time.Duration;
import java.util.Optional;

/**
 * Run configuration. Each value comes from a JVM system property (-Dbrowser=firefox),
 * then an environment variable (BROWSER=firefox), then a default for a local run.
 */
public final class Config {

    private Config() {}

    /** chrome | firefox */
    public static String browser() {
        return read("browser", "BROWSER", "chrome").toLowerCase();
    }

    /** Selenium Grid URL. Empty means a local browser managed by Selenium Manager. */
    public static Optional<String> gridUrl() {
        return Optional.of(read("grid.url", "GRID_URL", "")).filter(s -> !s.isBlank());
    }

    /** Base URL as seen by the browser (inside the grid this is the app's container name). */
    public static String appUrl() {
        return stripSlash(read("app.url", "APP_URL", "http://localhost:3000"));
    }

    /** Base URL as seen by this JVM, for API calls. */
    public static String apiUrl() {
        return stripSlash(read("api.url", "API_URL", "http://localhost:3000"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(read("headless", "HEADLESS", "true"));
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(read("timeout.seconds", "TIMEOUT_SECONDS", "10")));
    }

    private static String read(String property, String env, String fallback) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) return fromProperty;
        String fromEnv = System.getenv(env);
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        return fallback;
    }

    private static String stripSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
