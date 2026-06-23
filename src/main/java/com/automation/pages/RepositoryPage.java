package com.automation.pages;

import com.automation.models.RepositoryData;
import com.automation.utils.LoggerUtil;
import com.automation.utils.WaitUtil;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for an individual GitHub repository page.
 * Responsible for extracting repository analytics:
 * name, stars, forks, issues, primary language, and description.
 */
public class RepositoryPage {

    private static final Logger logger = LoggerUtil.getLogger(RepositoryPage.class);

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators — GitHub repo header structure
    private final By repoNameStrong = By.cssSelector("strong[itemprop='name'] a");
    private final By repoNameFallback = By.xpath("//h1//a[contains(@href, '/')][last()]");

    private final By starsCounter = By.id("repo-stars-counter-star");
    private final By forksCounter = By.id("repo-network-counter");
    private final By issuesCounter = By.xpath("//a[contains(@href,'/issues')]//span[@id='issues-repo-tab-count' or contains(@class,'Counter')]");
    private final By issuesLinkFallback = By.cssSelector("a[href*='/issues'] span.Counter");

    private final By aboutDescription = By.xpath("//h2[normalize-space()='About']/following-sibling::p[1]");
    private final By descriptionFallback = By.cssSelector("p.f4.my-3");

    private final By primaryLanguageSpan = By.xpath("//h2[normalize-space()='Languages']/following::span[contains(@class,'color-fg-default')][1]");
    private final By languageListItem = By.cssSelector("ul.list-style-none li .color-fg-default");

    private final By repoHeader = By.cssSelector("strong[itemprop='name']");

    public RepositoryPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Validate repository page has loaded")
    public boolean isRepositoryPageLoaded() {
        try {
            waitUtil.waitForVisibility(repoHeader);
            return true;
        } catch (Exception e) {
            logger.error("Repository header not visible: {}", e.getMessage());
            return false;
        }
    }

    @Step("Extract repository name")
    public String extractRepositoryName() {
        try {
            WebElement el = waitUtil.waitForVisibility(repoNameStrong);
            String name = el.getText().trim();
            if (name.isEmpty()) {
                name = driver.findElement(repoNameFallback).getText().trim();
            }
            logger.info("Extracted repository name: {}", name);
            return name;
        } catch (Exception e) {
            logger.warn("Primary repo name locator failed, using URL fallback: {}", e.getMessage());
            String[] segments = driver.getCurrentUrl().split("/");
            return segments[segments.length - 1];
        }
    }

    @Step("Extract star count")
    public String extractStars() {
        try {
            WebElement el = waitUtil.waitForVisibility(starsCounter);
            String stars = el.getText().trim();
            logger.info("Extracted stars: {}", stars);
            return stars;
        } catch (Exception e) {
            logger.warn("Could not extract star count: {}", e.getMessage());
            return "0";
        }
    }

    @Step("Extract fork count")
    public String extractForks() {
        try {
            WebElement el = waitUtil.waitForVisibility(forksCounter);
            String forks = el.getText().trim();
            logger.info("Extracted forks: {}", forks);
            return forks;
        } catch (Exception e) {
            logger.warn("Could not extract fork count: {}", e.getMessage());
            return "0";
        }
    }

    @Step("Extract open issues count")
    public String extractIssues() {
        try {
            List<WebElement> issueCounters = driver.findElements(issuesLinkFallback);
            if (!issueCounters.isEmpty()) {
                String issues = issueCounters.get(0).getText().trim();
                logger.info("Extracted issues: {}", issues);
                return issues.isEmpty() ? "0" : issues;
            }
            return "0";
        } catch (Exception e) {
            logger.warn("Could not extract issues count: {}", e.getMessage());
            return "0";
        }
    }

    @Step("Extract primary language")
    public String extractPrimaryLanguage() {
        try {
            List<WebElement> languages = driver.findElements(languageListItem);
            if (!languages.isEmpty()) {
                String lang = languages.get(0).getText().trim();
                logger.info("Extracted primary language: {}", lang);
                return lang.isEmpty() ? "Unknown" : lang;
            }
            return "Unknown";
        } catch (Exception e) {
            logger.warn("Could not extract primary language: {}", e.getMessage());
            return "Unknown";
        }
    }

    @Step("Extract repository description")
    public String extractDescription() {
        try {
            WebElement el = waitUtil.waitForVisibility(aboutDescription);
            String desc = el.getText().trim();
            if (desc.isEmpty()) {
                desc = driver.findElement(descriptionFallback).getText().trim();
            }
            logger.info("Extracted description: {}", desc);
            return desc.isEmpty() ? "No description provided" : desc;
        } catch (Exception e) {
            logger.warn("Could not extract description: {}", e.getMessage());
            return "No description provided";
        }
    }

    @Step("Extract complete repository data into model")
    public RepositoryData extractRepositoryData() {
        RepositoryData data = new RepositoryData();
        data.setRepositoryName(extractRepositoryName());
        data.setStars(extractStars());
        data.setForks(extractForks());
        data.setIssues(extractIssues());
        data.setPrimaryLanguage(extractPrimaryLanguage());
        data.setDescription(extractDescription());
        logger.info("Complete repository data extracted: {}", data);
        return data;
    }

    @Step("Scroll down the repository page using JavascriptExecutor")
    public void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to bottom of repository page using JavascriptExecutor");
    }

    @Step("Scroll to top of the repository page using JavascriptExecutor")
    public void scrollToTop() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0);");
        logger.info("Scrolled to top of repository page using JavascriptExecutor");
    }

    /**
     * Waits explicitly (no Thread.sleep) for the scroll position to actually change,
     * confirming the JS scroll action took effect before the next screenshot.
     */
    public void waitForScrollCompletion(long expectedMinimumYOffset) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> {
            Object result = ((JavascriptExecutor) webDriver)
                    .executeScript("return window.pageYOffset || document.documentElement.scrollTop;");
            if (result instanceof Number) {
                return ((Number) result).longValue() >= expectedMinimumYOffset;
            }
            return false;
        });
    }
}
