package com.automation.listeners;

import com.automation.constants.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to FrameworkConstants.MAX_RETRY_COUNT times
 * before TestNG marks it as a final failure.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < FrameworkConstants.MAX_RETRY_COUNT) {
            retryCount++;
            logger.warn("Retrying test [{}] - attempt {} of {}",
                    result.getName(), retryCount, FrameworkConstants.MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }
}
