package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Central logging utility wrapper around Log4j2.
 * Provides a single point of access for logging so log statements
 * are consistent across pages, tests, and utilities.
 */
public class LoggerUtil {

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    private LoggerUtil() {
    }
}
