package com.automation.base;

import com.automation.constants.FrameworkConstants;
import com.automation.factory.DriverFactory;
import com.automation.utils.ConfigReader;
import com.automation.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for all test classes.
 * Handles ThreadLocal driver initialization and teardown per test method,
 * enabling safe parallel execution across multiple threads.
 */
public class BaseTest {

    protected static final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        String resolvedBrowser = (browser != null && !browser.isEmpty())
                ? browser
                : ConfigReader.getInstance().getBrowser();

        logger.info("Setting up driver for browser: {} on thread [{}]", resolvedBrowser, Thread.currentThread().getId());
        DriverFactory.initDriver(resolvedBrowser);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("Tearing down driver for test [{}] on thread [{}]",
                result.getMethod().getMethodName(), Thread.currentThread().getId());
        DriverFactory.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    protected String getBaseUrl() {
        return FrameworkConstants.GITHUB_TOPICS_URL;
    }
}
