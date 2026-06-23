package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Centralized explicit wait utility.
 * No Thread.sleep() / hardcoded waits anywhere in the framework —
 * every wait goes through WebDriverWait + ExpectedConditions here.
 */
public class WaitUtil {

    private static final Logger logger = LoggerUtil.getLogger(WaitUtil.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtil(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(FrameworkConstants.EXPLICIT_WAIT_TIMEOUT));
    }

    public WebElement waitForVisibility(By locator) {
        logger.debug("Waiting for visibility of element: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public List<WebElement> waitForAllVisible(By locator) {
        logger.debug("Waiting for visibility of all elements: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public List<WebElement> waitForPresenceOfAll(By locator) {
        logger.debug("Waiting for presence of all elements: {}", locator);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public boolean waitForUrlContains(String fraction) {
        logger.debug("Waiting for URL to contain: {}", fraction);
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public boolean waitForTitleContains(String titleFraction) {
        logger.debug("Waiting for title to contain: {}", titleFraction);
        return wait.until(ExpectedConditions.titleContains(titleFraction));
    }

    public WebElement waitForPresence(By locator) {
        logger.debug("Waiting for presence of element: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean waitForInvisibility(By locator) {
        logger.debug("Waiting for invisibility of element: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
}
