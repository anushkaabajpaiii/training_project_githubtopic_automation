package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.constants.FrameworkConstants;
import com.automation.models.RepositoryData;
import com.automation.pages.HomePage;
import com.automation.pages.RepositoryPage;
import com.automation.pages.TopicsPage;
import com.automation.utils.CSVUtil;
import com.automation.utils.ExcelUtil;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

/**
 * SCENARIO 7: Run repository extraction for Java, Python, Selenium, Automation
 * in parallel using TestNG (thread-count=4). Each thread uses its own
 * ThreadLocal WebDriver instance via DriverFactory, guaranteeing thread safety.
 *
 * Parallelism is driven by testng.xml: <test> level parallel="methods" thread-count="4"
 * combined with a DataProvider(parallel = true) so each topic runs on its own thread.
 */
@Epic("GitHub Repository Analytics")
@Feature("Parallel Repository Extraction")
public class ParallelAnalyticsTest extends BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(ParallelAnalyticsTest.class);

    @DataProvider(name = "topicsProvider", parallel = true)
    public Object[][] topicsProvider() {
        return new Object[][]{
                {"java"},
                {"python"},
                {"selenium"},
                {"automation"}
        };
    }

    @Test(dataProvider = "topicsProvider", description = "Extract repository analytics in parallel for multiple topics")
    @Severity(SeverityLevel.NORMAL)
    @Story("Parallel extraction across Java, Python, Selenium, Automation topics")
    @Description("Each topic is processed on an independent thread with its own ThreadLocal WebDriver instance.")
    public void testParallelRepositoryExtraction(String topic) {
        long threadId = Thread.currentThread().getId();
        logger.info("Starting parallel extraction for topic '{}' on thread [{}]", topic, threadId);

        HomePage homePage = new HomePage(getDriver());
        homePage.openTopicsPage(getBaseUrl());
        homePage.navigateToTopicDirectly(topic);

        TopicsPage topicsPage = new TopicsPage(getDriver());
        boolean reposDisplayed = topicsPage.areRepositoriesDisplayed();
        Assert.assertTrue(reposDisplayed, "No repositories displayed for topic: " + topic);

        topicsPage.openFirstRepository();

        RepositoryPage repositoryPage = new RepositoryPage(getDriver());
        Assert.assertTrue(repositoryPage.isRepositoryPageLoaded(), "Repository page failed to load for topic: " + topic);

        RepositoryData data = repositoryPage.extractRepositoryData();
        logger.info("[Thread {}] Topic '{}' extracted data: {}", threadId, topic, data);

        ScreenshotUtil.captureScreenshot(getDriver(), "Parallel_" + topic + "_Extracted");

        Assert.assertFalse(data.getRepositoryName().trim().isEmpty(),
                "Repository name should not be empty for topic: " + topic);

        List<RepositoryData> singleResult = Collections.singletonList(data);
        String csvFileName = topic + "_" + FrameworkConstants.CSV_FILE_NAME;
        String excelFileName = topic + "_" + FrameworkConstants.EXCEL_FILE_NAME;

        CSVUtil.exportToCSV(singleResult, csvFileName);
        ExcelUtil.exportToExcel(singleResult, excelFileName);

        logger.info("Completed parallel extraction for topic '{}' on thread [{}]", topic, threadId);
    }
}
