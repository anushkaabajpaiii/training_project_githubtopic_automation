package com.automation.constants;

/**
 * Centralized constants used across the framework.
 * Avoids magic strings/numbers scattered across classes.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {
        // Prevent instantiation
    }

    // ---------------- Paths ----------------
    public static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    public static final String SCREENSHOT_DIR = "test-output/screenshots/";
    public static final String SCREENSHOT_PASS_DIR = "test-output/screenshots/pass/";
    public static final String SCREENSHOT_FAIL_DIR = "test-output/screenshots/fail/";
    public static final String CSV_EXPORT_DIR = "test-output/data/";
    public static final String EXCEL_EXPORT_DIR = "test-output/data/";
    public static final String LOG_DIR = "test-output/logs/";

    // ---------------- File Names ----------------
    public static final String CSV_FILE_NAME = "RepositoryData.csv";
    public static final String EXCEL_FILE_NAME = "RepositoryData.xlsx";

    // ---------------- Timeouts (seconds) ----------------
    public static final int EXPLICIT_WAIT_TIMEOUT = 30;
    public static final int PAGE_LOAD_TIMEOUT = 60;
    public static final int POLLING_INTERVAL_MILLIS = 500;

    // ---------------- Retry ----------------
    public static final int MAX_RETRY_COUNT = 2;

    // ---------------- URLs ----------------
    public static final String GITHUB_TOPICS_URL = "https://github.com/topics";

    // ---------------- Browser ----------------
    public static final String DEFAULT_BROWSER = "chrome";
}
