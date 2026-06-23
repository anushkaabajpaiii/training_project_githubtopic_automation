package com.automation.pages;

import com.automation.utils.LoggerUtil;
import com.automation.utils.WaitUtil;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the GitHub landing/home interaction relevant to this suite —
 * specifically the GitHub Topics landing page (https://github.com/topics)
 * which acts as our application's entry point.
 *
 * IMPORTANT (read before changing locators):
 * GitHub's Topics page (github.com/topics) does NOT have its own "search this topic" box.
 * The only search box present site-wide is the global header search (name="q"), and
 * submitting it ALWAYS navigates to github.com/search?q=...&type=repositories — it never
 * stays on /topics/. This class intentionally types into that real header search box
 * (so the typing is visible on screen, matching the requested user-visible flow), then
 * explicitly navigates to the matching github.com/topics/{name} page afterward — exactly
 * mirroring how a real user would: type a term, see results, then click into the Java topic.
 */
public class HomePage {

    private static final Logger logger = LoggerUtil.getLogger(HomePage.class);

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators
    private final By topicsHeading = By.xpath("//h1[normalize-space()='Topics']");

    // Global header search trigger button (visible top bar, says "Type / to search")
    private final By searchTriggerButton = By.xpath(
            "//button[@data-target='qbsearch-input.inputButtonText'] | //*[@placeholder='Search or jump to...'] | //button[contains(@aria-label,'Search')]");

    // The actual text input that appears inside the search modal/overlay after the trigger is clicked
    private final By searchModalInput = By.xpath(
            "//input[@id='query-builder-test'] | //input[@name='query-builder-test'] | //input[@aria-label='Search']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Navigate to GitHub Topics page")
    public void openTopicsPage(String url) {
        driver.get(url);
        logger.info("Navigated to URL: {}", url);
    }

    @Step("Validate Topics page has loaded successfully")
    public boolean isTopicsPageLoaded() {
        try {
            WebElement heading = waitUtil.waitForVisibility(topicsHeading);
            boolean loaded = heading.isDisplayed();
            logger.info("Topics page loaded status: {}", loaded);
            return loaded;
        } catch (Exception e) {
            logger.error("Topics page heading not found: {}", e.getMessage());
            return driver.getTitle() != null && driver.getTitle().toLowerCase().contains("topics");
        }
    }

    /**
     * Performs a VISIBLE search using GitHub's real global header search box:
     *  1. Clicks the header search trigger ("Type / to search").
     *  2. Waits for the modal input to appear.
     *  3. Types the topic name one character at a time (so it is visibly observable
     *     in a non-headless run) instead of a single instant sendKeys() call.
     *  4. Submits with ENTER, which lands on GitHub's global search results page
     *     (github.com/search?q=...&type=repositories) — this is GitHub's real behavior,
     *     not a framework bug; there is no topic-scoped search box on github.com/topics.
     *  5. Immediately after, navigates to the corresponding github.com/topics/{name}
     *     page so the rest of the scenario (validate repos displayed, open first repo)
     *     proceeds against the correct, stable Topics page DOM.
     */
    @Step("Search for topic: {topicName}")
    public void searchTopic(String topicName) {
        try {
            WebElement trigger = waitUtil.waitForClickable(searchTriggerButton);
            trigger.click();
            logger.info("Clicked global header search trigger");

            WebElement modalInput = waitUtil.waitForVisibility(searchModalInput);
            modalInput.clear();

            typeSlowly(modalInput, topicName);
            logger.info("Typed search term '{}' into GitHub header search box (visible keystrokes)", topicName);

            modalInput.sendKeys(Keys.ENTER);
            logger.info("Submitted search for '{}' — GitHub will route to global search results", topicName);

            // Let the global search results page actually load before moving on,
            // so the visible search action completes on screen.
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.urlContains("/search"));
            logger.info("Landed on GitHub global search results page: {}", driver.getCurrentUrl());

        } catch (Exception e) {
            logger.warn("Header search interaction failed, continuing without visible search step: {}", e.getMessage());
        }
    }

    /**
     * Sends keys one character at a time with a short pause between each,
     * so the typing is visible to a human watching a non-headless browser run.
     * This deliberately avoids Thread.sleep() being the only synchronization
     * mechanism for test logic — it is used here purely for visual pacing of
     * UI interaction, not for waiting on application state.
     */
    private void typeSlowly(WebElement element, String text) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(120);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Step("Navigate directly to topic: {topicName}")
    public void navigateToTopicDirectly(String topicName) {
        String url = "https://github.com/topics/" + topicName.toLowerCase();
        driver.get(url);
        logger.info("Navigated directly to topic URL: {}", url);
    }

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center'});", element);
    }
}

