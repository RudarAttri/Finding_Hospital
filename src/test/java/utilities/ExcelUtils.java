package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {

    private final String filePath;
    private final String sheetName;

    public ExcelUtils(String filePath, String sheetName) {
        this.filePath  = filePath;
        this.sheetName = sheetName;
    }

    // ─── READ entire sheet (for TestNG @DataProvider) ────────────────────────
    public Object[][] getSheetData() throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook   = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) throw new IOException("Sheet not found: " + sheetName);

            int totalRows = sheet.getLastRowNum();
            int totalCols = sheet.getRow(0).getLastCellNum();

            Object[][] data = new Object[totalRows][totalCols];
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = (row != null) ? row.getCell(j) : null;
                    data[i - 1][j] = getCellValue(cell);
                }
            }
            return data;
        }
    }

    // ─── WRITE a single cell — with retry logic ──────────────────────────────
    public synchronized boolean writeCell(int rowIndex, int colIndex, String value) {
        int maxAttempts = 5;
        int attempt = 0;
        Exception lastError = null;

        while (attempt < maxAttempts) {
            attempt++;
            FileOutputStream fos = null;
            Workbook workbook    = null;
            FileInputStream fis  = null;

            try {
                fis = new FileInputStream(filePath);
                workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheet(sheetName);

                if (sheet == null) {
                    System.out.println("  ✘ Excel write failed: sheet '"
                            + sheetName + "' not found");
                    return false;
                }

                Row row = sheet.getRow(rowIndex);
                if (row == null) row = sheet.createRow(rowIndex);

                Cell cell = row.getCell(colIndex);
                if (cell == null) cell = row.createCell(colIndex);

                cell.setCellValue(value);

                fis.close();
                fis = null;

                fos = new FileOutputStream(filePath);
                workbook.write(fos);
                fos.flush();

                System.out.println("  ✔ Excel updated [row=" + rowIndex
                        + ", col=" + colIndex + "] → " + value);
                return true;

            } catch (IOException e) {
                lastError = e;
                String msg = e.getMessage() != null ? e.getMessage() : "";

                if (msg.contains("being used by another process")
                        || msg.contains("access is denied")) {
                    System.out.println("  ⚠ Excel locked (attempt " + attempt
                            + "/" + maxAttempts + ") — close Excel app! Retrying in 3s...");
                    sleep(3000);
                } else {
                    System.out.println("  ✘ Excel write error: " + msg);
                    break;   // non-recoverable error
                }
            } finally {
                try { if (fis      != null) fis.close();      } catch (IOException ignored) {}
                try { if (workbook != null) workbook.close(); } catch (IOException ignored) {}
                try { if (fos      != null) fos.close();      } catch (IOException ignored) {}
            }
        }

        System.out.println("  ✘ Excel write FAILED after " + maxAttempts + " attempts");
        if (lastError != null) {
            System.out.println("    Last error: " + lastError.getMessage());
            System.out.println("    >>> CLOSE the Excel file in Microsoft Excel <<<");
        }
        return false;
    }

    // ─── WRITE status by TestCaseID ──────────────────────────────────────────
    public synchronized boolean writeStatusByTestCaseId(String testCaseId,
                                                        String status,
                                                        int testCaseIdCol,
                                                        int statusCol) {
        // Find the row index first
        int targetRow = -1;
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook   = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell idCell = row.getCell(testCaseIdCol);
                if (idCell != null
                        && testCaseId.equalsIgnoreCase(getCellValue(idCell))) {
                    targetRow = i;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("  ✘ Could not read Excel to find TestCaseID: "
                    + e.getMessage());
            return false;
        }

        if (targetRow == -1) {
            System.out.println("  ✘ TestCase ID not found in Excel: " + testCaseId);
            return false;
        }

        return writeCell(targetRow, statusCol, status);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getDateCellValue().toString();
                double d = cell.getNumericCellValue();
                return (d == (long) d) ? String.valueOf((long) d) : String.valueOf(d);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            case BLANK:
            default:      return "";
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}