package com.automation.listeners;

import com.automation.factory.DriverFactory;
import com.automation.utils.AllureEnvironmentWriter;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * TestNG Listener Framework implementation.
 * Handles:
 *  - Logging test lifecycle events (start, pass, fail, skip)
 *  - Capturing pass/fail screenshots
 *  - Attaching screenshots to Allure report on failure
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LoggerUtil.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("===== Test Suite Started: {} =====", context.getName());
        AllureEnvironmentWriter.writeEnvironmentFile();
        copyAllureCategoriesFile();
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("===== Test Suite Finished: {} =====", context.getName());
        logger.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info(">>> Starting test: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("<<< Test PASSED: {}", result.getMethod().getMethodName());
        attachScreenshotIfDriverAvailable(result.getMethod().getMethodName(), "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        logger.error("<<< Test FAILED: {} | Reason: {}", methodName,
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");

        String screenshotPath = attachScreenshotIfDriverAvailable(methodName, "FAIL");
        if (screenshotPath != null) {
            logger.error("Failure screenshot saved at: {}", screenshotPath);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("<<< Test SKIPPED: {}", result.getMethod().getMethodName());
    }

    private String attachScreenshotIfDriverAvailable(String testName, String status) {
        try {
            WebDriver driver = DriverFactory.getDriver();

            String path = "FAIL".equals(status)
                    ? ScreenshotUtil.captureFailureScreenshot(driver, testName)
                    : ScreenshotUtil.capturePassScreenshot(driver, testName);

            logger.info("Screenshot captured on {} for [{}] at path: {}", status, testName, path);

            byte[] screenshotBytes = ScreenshotUtil.captureScreenshotBytes(driver);
            if (screenshotBytes.length > 0) {
                Allure.addAttachment(testName + "_" + status, new ByteArrayInputStream(screenshotBytes));
            }
            return path;
        } catch (IllegalStateException e) {
            logger.warn("Driver not available for screenshot capture on test [{}]: {}", testName, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while attaching screenshot for [{}]: {}", testName, e.getMessage());
            return null;
        }
    }

    private void copyAllureCategoriesFile() {
        try {
            File source = new File("src/test/resources/allure-categories/categories.json");
            File destinationDir = new File("allure-results");
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            if (source.exists()) {
                FileUtils.copyFile(source, new File(destinationDir, "categories.json"));
                logger.info("Allure categories.json copied to allure-results/");
            } else {
                logger.warn("categories.json source file not found at: {}", source.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to copy categories.json to allure-results: {}", e.getMessage());
        }
    }
}
