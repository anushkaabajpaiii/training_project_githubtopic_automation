package com.automation.pages;

import com.automation.utils.LoggerUtil;
import com.automation.utils.WaitUtil;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for an individual GitHub Topic page (e.g. github.com/topics/java).
 * Responsible for validating that repositories are displayed and
 * for navigating into the first repository result.
 */
public class TopicsPage {

    private static final Logger logger = LoggerUtil.getLogger(TopicsPage.class);

    private final WebDriver driver;
    private final WaitUtil waitUtil;

    // Locators — verified against live github.com/topics/{name} markup.
    // Each result is an <article> containing an <h3> with TWO links: owner, then repo name.
    // XPath used here (not :has()) for guaranteed cross-browser support across Chrome/Firefox/Edge.
    private final By repositoryCards = By.xpath("//article[.//h3//a[@href]]");
    private final By topicPageHeading = By.cssSelector("h1");

    public TopicsPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    @Step("Validate repositories are displayed on topic page")
    public boolean areRepositoriesDisplayed() {
        try {
            List<WebElement> cards = waitUtil.waitForAllVisible(repositoryCards);
            boolean displayed = !cards.isEmpty();
            logger.info("Repository cards found on topic page: {}", cards.size());
            return displayed;
        } catch (Exception e) {
            logger.error("No repository cards found on topic page: {}", e.getMessage());
            return false;
        }
    }

    @Step("Get count of repository cards displayed")
    public int getRepositoryCount() {
        List<WebElement> cards = driver.findElements(repositoryCards);
        return cards.size();
    }

    @Step("Open the first repository from topic results")
    public void openFirstRepository() {
        List<WebElement> cards = waitUtil.waitForAllVisible(repositoryCards);
        if (cards.isEmpty()) {
            throw new IllegalStateException("No repository cards found on topic page to open.");
        }

        WebElement firstCard = cards.get(0);
        List<WebElement> linksInCard = firstCard.findElements(By.xpath(".//h3//a[@href]"));

        if (linksInCard.isEmpty()) {
            throw new IllegalStateException("No links found inside the first repository card's <h3>.");
        }

        // h3 contains [owner link, repo-name link] in that order on github.com/topics/{name}.
        // Take the LAST link in the h3 — that is always the repository name link.
        WebElement repoNameLink = linksInCard.get(linksInCard.size() - 1);
        String repoHref = repoNameLink.getAttribute("href");
        logger.info("Opening first repository: {}", repoHref);

        // Scroll into view before clicking to avoid intercepted-click issues on long topic pages.
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior:'instant', block:'center'});", repoNameLink);

        repoNameLink.click();
    }

    public String getPageHeadingText() {
        return waitUtil.waitForVisibility(topicPageHeading).getText();
    }
}
