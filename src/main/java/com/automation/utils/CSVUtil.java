package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import com.automation.models.RepositoryData;
import com.opencsv.CSVWriter;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Exports RepositoryData records to CSV format using OpenCSV.
 * Files are written to test-output/data/.
 */
public class CSVUtil {

    private static final Logger logger = LoggerUtil.getLogger(CSVUtil.class);

    private CSVUtil() {
    }

    public static String exportToCSV(List<RepositoryData> repositoryDataList, String fileName) {
        File dir = new File(FrameworkConstants.CSV_EXPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File csvFile = new File(dir, fileName);

        try (FileWriter fileWriter = new FileWriter(csvFile);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            String[] header = {"Repository Name", "Stars", "Forks", "Issues", "Primary Language", "Description"};
            csvWriter.writeNext(header);

            for (RepositoryData data : repositoryDataList) {
                String[] row = {
                        safe(data.getRepositoryName()),
                        safe(data.getStars()),
                        safe(data.getForks()),
                        safe(data.getIssues()),
                        safe(data.getPrimaryLanguage()),
                        safe(data.getDescription())
                };
                csvWriter.writeNext(row);
            }

            logger.info("CSV export successful: {} ({} records)", csvFile.getAbsolutePath(), repositoryDataList.size());
            return csvFile.getAbsolutePath();

        } catch (IOException e) {
            logger.error("Failed to export CSV file: {}", e.getMessage());
            throw new RuntimeException("CSV export failed: " + e.getMessage(), e);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
