package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;

/**
 * Run this once to generate TestData/CorporateWellnessData.xlsx
 * with one valid row and one invalid row.
 *
 * Columns (0-indexed):
 *   0 = Name
 *   1 = OrganizationName
 *   2 = ContactNumber
 *   3 = Email
 *   4 = OrganizationSizeDropdownIndex   (1 = first real option)
 *   5 = InterestedInDropdownIndex       (1 = first real option)
 */
public class ExcelDataCreator {

    public static void main(String[] args) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CorporateWellness");

        // ── Row 0: Header ──────────────────────────────────────────────────
        Row header = sheet.createRow(0);
        String[] headers = {"Name", "OrganizationName", "ContactNumber",
                "Email", "OrgSizeIndex", "InterestedInIndex"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // ── Row 1: VALID data ──────────────────────────────────────────────
        Row validRow = sheet.createRow(1);
        validRow.createCell(0).setCellValue("Sanika Sharma");       // Full valid name
        validRow.createCell(1).setCellValue("Cognizant");           // Valid org name
        validRow.createCell(2).setCellValue("9876543210");          // 10-digit number
        validRow.createCell(3).setCellValue("sanika@cognizant.com");// Valid corporate email
        validRow.createCell(4).setCellValue("1");                   // Dropdown index 1
        validRow.createCell(5).setCellValue("1");                   // Dropdown index 1

        // ── Row 2: INVALID data ────────────────────────────────────────────
        Row invalidRow = sheet.createRow(2);
        invalidRow.createCell(0).setCellValue("S");                 // Too short / invalid name
        invalidRow.createCell(1).setCellValue("");                  // Empty org name
        invalidRow.createCell(2).setCellValue("123");               // Invalid phone (too short)
        invalidRow.createCell(3).setCellValue("notanemail");        // Invalid email format
        invalidRow.createCell(4).setCellValue("0");                 // Index 0 = placeholder "--Select--"
        invalidRow.createCell(5).setCellValue("0");                 // Index 0 = placeholder "--Select--"

        // ── Auto-size columns ──────────────────────────────────────────────
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ── Write to file ──────────────────────────────────────────────────
        Files.createDirectories(Paths.get("TestData"));
        try (FileOutputStream fos = new FileOutputStream("TestData/CorporateWellnessData.xlsx")) {
            workbook.write(fos);
        }
        workbook.close();

        System.out.println("Excel file created: TestData/CorporateWellnessData.xlsx");
        System.out.println("  Row 1 → VALID data");
        System.out.println("  Row 2 → INVALID data");
    }
}