package com.automation.factory;

import com.automation.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Factory Design Pattern implementation for WebDriver creation.
 * Uses ThreadLocal<WebDriver> so each TestNG thread (parallel execution)
 * gets its own isolated driver instance — critical for thread safety.
 */
public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
    }

    /**
     * Initializes a new WebDriver instance for the calling thread
     * based on the browser name supplied.
     */
    public static void initDriver(String browserName) {
        WebDriver driver = createDriver(browserName);
        driverThreadLocal.set(driver);

        int pageLoadTimeout = ConfigReader.getInstance().getPageLoadTimeout();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();

        logger.info("Driver initialized for browser [{}] on thread [{}]", browserName, Thread.currentThread().getId());
    }

    private static WebDriver createDriver(String browserName) {
        if (browserName == null || browserName.trim().isEmpty()) {
            browserName = "chrome";
        }

        boolean headless = ConfigReader.getInstance().isHeadless();

        switch (browserName.trim().toLowerCase()) {

            case "chrome": {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                }
                return new ChromeDriver(options);
            }

            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                return new FirefoxDriver(options);
            }

            case "edge": {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                }
                return new EdgeDriver(options);
            }

            default:
                logger.error("Unsupported browser requested: {}", browserName);
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialized for thread: " + Thread.currentThread().getId()
                    + ". Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            logger.info("Driver quit and removed for thread [{}]", Thread.currentThread().getId());
        }
    }
}
