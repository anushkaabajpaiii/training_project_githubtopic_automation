package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots (pass/fail/ad-hoc) with timestamp-based naming.
 *
 * Storage layout under the project root:
 *   test-output/screenshots/              -> ad-hoc, in-test screenshots (e.g. before/after scroll)
 *   test-output/screenshots/pass/         -> auto-captured on every test PASS (via TestListener)
 *   test-output/screenshots/fail/         -> auto-captured on every test FAILURE (via TestListener)
 *
 * All paths are logged and returned so they can also be attached to the Allure report.
 */
public class ScreenshotUtil {

    private static final Logger logger = LoggerUtil.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
    }

    /**
     * Captures a screenshot into the generic screenshots directory (test-output/screenshots/).
     * Use this for ad-hoc, in-test captures that aren't specifically a pass/fail outcome
     * (e.g. "before scroll", "after scroll", "data extracted").
     *
     * @param driver   active WebDriver instance
     * @param testName logical name (e.g., test method name or scenario tag)
     * @return absolute path of the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        return captureScreenshotToDir(driver, testName, FrameworkConstants.SCREENSHOT_DIR);
    }

    /**
     * Captures a screenshot specifically for a test PASS outcome, stored under
     * test-output/screenshots/pass/. Intended to be called from TestListener.onTestSuccess().
     *
     * @param driver   active WebDriver instance
     * @param testName logical name (typically the test method name)
     * @return absolute path of the saved screenshot file, or null if capture failed
     */
    public static String capturePassScreenshot(WebDriver driver, String testName) {
        return captureScreenshotToDir(driver, testName + "_PASS", FrameworkConstants.SCREENSHOT_PASS_DIR);
    }

    /**
     * Captures a screenshot specifically for a test FAILURE outcome, stored under
     * test-output/screenshots/fail/. Intended to be called from TestListener.onTestFailure().
     * This is the screenshot that should be attached to the Allure report on failure
     * and is the first place to look when triaging a broken run.
     *
     * @param driver   active WebDriver instance
     * @param testName logical name (typically the test method name)
     * @return absolute path of the saved screenshot file, or null if capture failed
     */
    public static String captureFailureScreenshot(WebDriver driver, String testName) {
        return captureScreenshotToDir(driver, testName + "_FAIL", FrameworkConstants.SCREENSHOT_FAIL_DIR);
    }

    private static String captureScreenshotToDir(WebDriver driver, String fileBaseName, String targetDir) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = fileBaseName + "_" + timestamp + ".png";

        try {
            File screenshotDir = new File(targetDir);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(screenshotDir, fileName);
            FileUtils.copyFile(source, destination);

            logger.info("Screenshot captured: {}", destination.getAbsolutePath());
            return destination.getAbsolutePath();

        } catch (IOException e) {
            logger.error("Failed to capture screenshot for [{}]: {}", fileBaseName, e.getMessage());
            return null;
        } catch (ClassCastException e) {
            logger.error("Driver does not support TakesScreenshot interface: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Captures screenshot bytes — used for direct Allure attachment.
     */
    public static byte[] captureScreenshotBytes(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot bytes: {}", e.getMessage());
            return new byte[0];
        }
    }
}
