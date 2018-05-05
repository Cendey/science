package com.netex.apps.impl.xsl;

import com.netex.apps.intf.Reader;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.impl.xsl.ExcelReader</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class ExcelReader implements Reader {

    private static final Logger logger = LogManager.getLogger(ExcelReader.class);

    @Override
    public List<Pair<List<String>, List<List<Object>>>> read(String filePath, Boolean isFileWithHeader)
            throws IOException {
        final List<Pair<List<String>, List<List<Object>>>> result = new ArrayList<>();

        if (Files.isReadable(Paths.get(filePath))) {
            // Creating a Workbook from an Excel file (.xls or .xlsx)
            try (final Workbook workbook = WorkbookFactory.create(new File(filePath))) {
                // Retrieving the number of sheets in the Workbook
                System.out.println(String.format("Workbook has %d Sheets :%n", workbook.getNumberOfSheets()));

                workbook.forEach(sheet -> {
                    List<String> sheetHeader = new ArrayList<>();
                    List<List<Object>> sheetData = new ArrayList<>();
                    sheet.forEach(row -> {
                        int rowNum = row.getRowNum();
                        if (isFileWithHeader) {
                            // Skip header row
                            if (rowNum != 0) {
                                List<Object> rowData = new ArrayList<>();
                                row.forEach(cell -> rowData.add(parse(workbook, cell)));
                                sheetData.add(rowData);
                            } else {
                                Row headerRow = sheet.getRow(rowNum);
                                headerRow.forEach(cell -> sheetHeader.add(cell.getStringCellValue()));
                            }
                        } else {
                            List<Object> rowData = new ArrayList<>();
                            row.forEach(cell -> rowData.add(parse(workbook, cell)));
                            sheetData.add(rowData);
                        }
                    });
                    Pair<List<String>, List<List<Object>>> sheetRecord = new Pair<>(sheetHeader, sheetData);
                    result.add(sheetRecord);
                });
            } catch (InvalidFormatException e) {
                logger.error(e.getCause().getMessage());
            }
        } else {
            logger.warn(String.format("%s is not readable or have no authorize to access!", filePath));
        }
        return result;
    }

    private static Object parse(Workbook workbook, Cell cell) {
        Object value;
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                value = DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
                break;
            case FORMULA:
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                value = formula(evaluator.evaluate(cell));
                break;
            default:
                value = null;
        }
        return value;
    }

    private static Object formula(CellValue cellValue) {
        Object value;
        switch (cellValue.getCellTypeEnum()) {
            case BOOLEAN:
                value = cellValue.getBooleanValue();
                break;
            case NUMERIC:
                value = cellValue.getNumberValue();
                break;
            case STRING:
                value = cellValue.getStringValue();
                break;
            default:
                value = null;
        }
        return value;
    }
}
