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
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * SCENARIO 3: Open first repository, extract Name/Stars/Forks/Issues/Language/Description,
 *             store in Java Objects, log values, capture screenshot.
 * SCENARIO 4: Export extracted repository data into CSV and Excel under test-output/data.
 * SCENARIO 5: Scroll through repository page using JavascriptExecutor, screenshots before/after.
 * SCENARIO 6: Validate Stars > 0, Forks >= 0, Language not null, Repository Name not empty.
 */
@Epic("GitHub Repository Analytics")
@Feature("Repository Data Extraction and Export")
public class RepositoryAnalyticsTest extends BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(RepositoryAnalyticsTest.class);

    @Test(priority = 1, description = "Extract repository analytics data, export to CSV/Excel, validate scrolling and data integrity")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Extract, validate, scroll, and export repository data")
    @Description("Opens the first repository under the Java topic, extracts repository analytics, "
            + "validates the data, scrolls the page via JavaScript, and exports results to CSV and Excel.")
    public void testExtractAndExportRepositoryAnalytics() {
        logger.info("Starting test: testExtractAndExportRepositoryAnalytics");

        HomePage homePage = new HomePage(getDriver());
        homePage.openTopicsPage(getBaseUrl());
        homePage.navigateToTopicDirectly("java");

        TopicsPage topicsPage = new TopicsPage(getDriver());
        Assert.assertTrue(topicsPage.areRepositoriesDisplayed(), "Repositories were not displayed on Java topic page");

        topicsPage.openFirstRepository();

        RepositoryPage repositoryPage = new RepositoryPage(getDriver());
        Assert.assertTrue(repositoryPage.isRepositoryPageLoaded(), "Repository page did not load");

        // SCENARIO 3: Extract data into Java object
        RepositoryData repositoryData = repositoryPage.extractRepositoryData();
        logger.info("Repository data extracted: {}", repositoryData);
        ScreenshotUtil.captureScreenshot(getDriver(), "RepositoryPage_DataExtracted");

        // SCENARIO 6: Data validation
        validateRepositoryData(repositoryData);

        // SCENARIO 5: Scroll using JavascriptExecutor with before/after screenshots
        ScreenshotUtil.captureScreenshot(getDriver(), "BeforeScroll");
        repositoryPage.scrollToBottom();
        repositoryPage.waitForScrollCompletion(1);
        ScreenshotUtil.captureScreenshot(getDriver(), "AfterScroll");

        // SCENARIO 4: Export to CSV and Excel
        List<RepositoryData> dataList = new ArrayList<>();
        dataList.add(repositoryData);

        String csvPath = CSVUtil.exportToCSV(dataList, FrameworkConstants.CSV_FILE_NAME);
        String excelPath = ExcelUtil.exportToExcel(dataList, FrameworkConstants.EXCEL_FILE_NAME);

        logger.info("CSV exported to: {}", csvPath);
        logger.info("Excel exported to: {}", excelPath);

        Assert.assertNotNull(csvPath, "CSV export path should not be null");
        Assert.assertNotNull(excelPath, "Excel export path should not be null");

        logger.info("Test completed: testExtractAndExportRepositoryAnalytics | Result: PASSED");
    }

    @Step("Validate extracted repository data integrity")
    private void validateRepositoryData(RepositoryData data) {
        Assert.assertNotNull(data.getRepositoryName(), "Repository name should not be null");
        Assert.assertFalse(data.getRepositoryName().trim().isEmpty(), "Repository name should not be empty");

        long stars = data.getStarsAsLong();
        long forks = data.getForksAsLong();

        logger.info("Validating -> Stars: {}, Forks: {}, Language: {}, Name: '{}'",
                stars, forks, data.getPrimaryLanguage(), data.getRepositoryName());

        Assert.assertTrue(stars > 0, "Stars count should be greater than 0, found: " + stars);
        Assert.assertTrue(forks >= 0, "Forks count should be greater than or equal to 0, found: " + forks);
        Assert.assertNotNull(data.getPrimaryLanguage(), "Primary language should not be null");
        Assert.assertFalse(data.getPrimaryLanguage().trim().isEmpty(), "Primary language should not be empty");
    }
}
