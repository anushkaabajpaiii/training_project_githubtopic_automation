package com.automation.utils;

import com.automation.constants.FrameworkConstants;
import com.automation.models.RepositoryData;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Exports RepositoryData records to an Excel (.xlsx) workbook using Apache POI.
 * Files are written to test-output/data/.
 */
public class ExcelUtil {

    private static final Logger logger = LoggerUtil.getLogger(ExcelUtil.class);

    private ExcelUtil() {
    }

    public static String exportToExcel(List<RepositoryData> repositoryDataList, String fileName) {
        File dir = new File(FrameworkConstants.EXCEL_EXPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File excelFile = new File(dir, fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("RepositoryAnalytics");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {"Repository Name", "Stars", "Forks", "Issues", "Primary Language", "Description"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (RepositoryData data : repositoryDataList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(safe(data.getRepositoryName()));
                row.createCell(1).setCellValue(safe(data.getStars()));
                row.createCell(2).setCellValue(safe(data.getForks()));
                row.createCell(3).setCellValue(safe(data.getIssues()));
                row.createCell(4).setCellValue(safe(data.getPrimaryLanguage()));
                row.createCell(5).setCellValue(safe(data.getDescription()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                workbook.write(fos);
            }

            logger.info("Excel export successful: {} ({} records)", excelFile.getAbsolutePath(), repositoryDataList.size());
            return excelFile.getAbsolutePath();

        } catch (IOException e) {
            logger.error("Failed to export Excel file: {}", e.getMessage());
            throw new RuntimeException("Excel export failed: " + e.getMessage(), e);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
