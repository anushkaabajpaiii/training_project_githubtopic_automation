package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Writes allure-results/environment.properties so the Allure report's
 * "Environment" tab shows browser, OS, Java version, and framework details.
 */
public class AllureEnvironmentWriter {

    private static final Logger logger = LoggerUtil.getLogger(AllureEnvironmentWriter.class);
    private static final String ALLURE_RESULTS_DIR = "allure-results";

    private AllureEnvironmentWriter() {
    }

    public static void writeEnvironmentFile() {
        Properties props = new Properties();

        try {
            props.setProperty("Browser", ConfigReader.getInstance().getBrowser());
            props.setProperty("Headless.Mode", String.valueOf(ConfigReader.getInstance().isHeadless()));
            props.setProperty("Base.URL", ConfigReader.getInstance().getBaseUrl());
        } catch (Exception e) {
            logger.warn("ConfigReader unavailable while writing Allure environment file: {}", e.getMessage());
            props.setProperty("Browser", FrameworkConstants.DEFAULT_BROWSER);
        }

        props.setProperty("Java.Version", System.getProperty("java.version"));
        props.setProperty("OS.Name", System.getProperty("os.name"));
        props.setProperty("OS.Arch", System.getProperty("os.arch"));
        props.setProperty("Framework", "GitHub Repository Analytics Automation Framework");
        props.setProperty("Selenium.Version", "4.21.0");
        props.setProperty("TestNG.Version", "7.10.2");

        File dir = new File(ALLURE_RESULTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File envFile = new File(dir, "environment.properties");
        try (FileOutputStream fos = new FileOutputStream(envFile)) {
            props.store(fos, "Allure Environment Information - Auto Generated");
            logger.info("Allure environment.properties written to: {}", envFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to write Allure environment.properties: {}", e.getMessage());
        }
    }
}
