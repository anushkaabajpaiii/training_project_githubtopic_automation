package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.HomePage;
import com.automation.pages.TopicsPage;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SCENARIO 1: Open GitHub Topics, validate page loaded, capture screenshot, log execution.
 * SCENARIO 2: Search for "Java", open Java topic, validate repositories displayed, capture screenshot.
 */
@Epic("GitHub Repository Analytics")
@Feature("GitHub Topics Search")
public class GitHubSearchTest extends BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(GitHubSearchTest.class);

    @Test(priority = 1, description = "Validate GitHub Topics page loads successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Open GitHub Topics page")
    @Description("Opens the GitHub Topics landing page and validates it has loaded successfully.")
    public void testTopicsPageLoadsSuccessfully() {
        logger.info("Starting test: testTopicsPageLoadsSuccessfully");

        HomePage homePage = new HomePage(getDriver());
        homePage.openTopicsPage(getBaseUrl());

        boolean loaded = homePage.isTopicsPageLoaded();
        ScreenshotUtil.captureScreenshot(getDriver(), "TopicsPage_Loaded");

        Assert.assertTrue(loaded, "GitHub Topics page did not load successfully");
        logger.info("Test completed: testTopicsPageLoadsSuccessfully | Result: PASSED");
    }

    @Test(priority = 2, description = "Search for Java topic and validate repositories are displayed")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Search topic and validate repository listing")
    @Description("Types 'java' into GitHub's header search box (visibly), submits it, captures the resulting "
            + "global search page, then navigates into the Java topic page and validates repository cards render.")
    public void testSearchJavaTopicShowsRepositories() {
        logger.info("Starting test: testSearchJavaTopicShowsRepositories");

        HomePage homePage = new HomePage(getDriver());
        homePage.openTopicsPage(getBaseUrl());

        // SCENARIO 2: visibly type "java" into the real GitHub search box and submit it.
        homePage.searchTopic("java");
        ScreenshotUtil.captureScreenshot(getDriver(), "GlobalSearch_Java_Results");
        logger.info("Captured search results page after submitting 'java' search: {}", getDriver().getCurrentUrl());

        // GitHub's search submission always lands on /search?q=java&type=repositories
        // (there is no topic-scoped search box on github.com/topics). Now open the
        // actual Java Topic page to continue the scenario against stable repo-card markup.
        homePage.navigateToTopicDirectly("java");

        TopicsPage topicsPage = new TopicsPage(getDriver());
        boolean reposDisplayed = topicsPage.areRepositoriesDisplayed();

        ScreenshotUtil.captureScreenshot(getDriver(), "JavaTopic_Repositories");

        Assert.assertTrue(reposDisplayed, "No repositories displayed on the Java topic page");
        logger.info("Repository count on Java topic page: {}", topicsPage.getRepositoryCount());
        logger.info("Test completed: testSearchJavaTopicShowsRepositories | Result: PASSED");
    }
}
