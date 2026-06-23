package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton Config Reader.
 * Reads config.properties once and exposes typed getters.
 * Thread-safe via double-checked locking, since tests may run in parallel.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    private static volatile ConfigReader instance;
    private final Properties properties;

    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    private ConfigReader() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            logger.info("Config properties loaded successfully from {}", CONFIG_PATH);
        } catch (IOException e) {
            logger.error("Failed to load config.properties from {}", CONFIG_PATH, e);
            throw new RuntimeException("Unable to load config.properties: " + e.getMessage(), e);
        }
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property '{}' not found in config.properties", key);
        }
        return value;
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getBrowser() {
        return get("browser", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait", "30"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(get("page.load.timeout", "60"));
    }

    public String getBaseUrl() {
        return get("base.url", "https://github.com/topics");
    }

    public int getRetryCount() {
        return Integer.parseInt(get("retry.count", "2"));
    }
}
