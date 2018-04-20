package mit.app.center.impl.exsl;

import javafx.util.Pair;
import mit.app.center.intf.Writer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.impl.exsl.ExcelWriter</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class ExcelWriter implements Writer {

    @Override
    public void write(Pair<List<String>, List<List<String>>> dataInfo, String filePath) throws IOException {
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook();     // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances for various things like DataFormat,
           Hyperlink, RichTextString etc in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Employee");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Creating cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 1;
        for(Employee employee: employees) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                .setCellValue(employee.getName());

            row.createCell(1)
                .setCellValue(employee.getEmail());

            Cell dateOfBirthCell = row.createCell(2);
            dateOfBirthCell.setCellValue(employee.getDateOfBirth());
            dateOfBirthCell.setCellStyle(dateCellStyle);

            row.createCell(3)
                .setCellValue(employee.getSalary());
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }


    // Example to modify an existing excel file
    private static void modifyExistingWorkbook() throws InvalidFormatException, IOException {
        // Obtain a workbook from the excel file
        Workbook workbook = WorkbookFactory.create(new File("existing-spreadsheet.xlsx"));

        // Get Sheet at index 0
        Sheet sheet = workbook.getSheetAt(0);

        // Get Row at index 1
        Row row = sheet.getRow(1);

        // Get the Cell at index 2 from the above row
        Cell cell = row.getCell(2);

        // Create the cell if it doesn't exist
        if (cell == null)
            cell = row.createCell(2);

        // Update the cell's value
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Updated Value");

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("existing-spreadsheet.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }
    }
}
