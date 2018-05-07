package com.netex.apps.impl.xsl;

import com.netex.apps.intf.Writer;
import com.netex.apps.util.Utilities;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.impl.xsl.ExcelWriter</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class ExcelWriter implements Writer {

    private final static Logger logger = LogManager.getLogger(ExcelWriter.class);

    @Override
    public void write(Pair<List<String>, List<List<Object>>> dataInfo, String filePath) throws IOException {
        final Path parent = Paths.get(Utilities.liveParent(filePath));
        if (Files.isWritable(parent)) {
            final Path path = Paths.get(filePath).getParent();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            // Create a Workbook
            try (Workbook workbook = create(filePath)) {
                if (workbook == null) return;

                // Create a Sheet
                Sheet sheet = workbook.createSheet();

                fillData(reconstruct(dataInfo, workbook), workbook, sheet);

                resizeColumn(sheet);

                // Write the output to a file
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                }
            }
        } else {
            logger.warn(String.format("%s is not writable or have no authorize to write a file!", parent));
        }
    }

    private void fillData(Pair<List<List<String>>, List<List<Object>>> dataInfo, Workbook workbook, Sheet sheet) {
        Integer rowNum = 0;
        List<List<String>> lstHeader = dataInfo.getKey();
        if (lstHeader != null && lstHeader.size() > 0) {
            CellStyle headerCellStyle = createHeadStyle(workbook);

            // Create a Row
            Row headerRow = sheet.createRow(rowNum++);

            // Creating cells
            int column = 0;
            for (List<String> headers : lstHeader) {
                for (String header : headers) {
                    Cell cell = headerRow.createCell(column++);
                    cell.setCellValue(header);
                    cell.setCellStyle(headerCellStyle);
                }
            }
        }

        CellStyle dateCellStyle = stylize(workbook);

        List<List<Object>> records = dataInfo.getValue();
        // Create Other rows and cells with data

        for (List<Object> line : records) {
            Row row = sheet.createRow(rowNum++);
            int columnNum = 0;
            Cell cell;
            for (Object item : line) {
                if (item != null) {
                    Class<?> dataClass = item.getClass();
                    if (ClassUtils.isAssignable(dataClass, String.class)) {
                        cell = row.createCell(columnNum++, CellType.STRING);
                        cell.setCellValue(String.valueOf(item));
                    } else if (ClassUtils.isAssignable(dataClass, Double.class, true)) {
                        cell = row.createCell(columnNum++, CellType.NUMERIC);
                        cell.setCellValue(Double.class.cast(item));
                    } else if (ClassUtils.isAssignable(dataClass, Date.class)) {
                        cell = row.createCell(columnNum++, CellType.NUMERIC);
                        cell.setCellValue(Date.class.cast(item));
                        cell.setCellStyle(dateCellStyle);
                    } else if (ClassUtils.isAssignable(dataClass, Boolean.class, true)) {
                        cell = row.createCell(columnNum++, CellType.BOOLEAN);
                        cell.setCellValue(Boolean.class.cast(item));
                    } else {
                        cell = row.createCell(columnNum++, CellType.STRING);
                        cell.setCellValue(String.valueOf(item));
                    }
                }
            }
        }
    }

    private CellStyle stylize(Workbook workbook) {
        // Cell Style for formatting Date
        /* CreationHelper helps us create instances for various things like DataFormat,
           Hyperlink, RichTextString etc in a format (HSSF, XSSF) independent way */
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
        return dateCellStyle;
    }

    private void resizeColumn(Sheet sheet) {
        // Resize all columns to fit the content size
        sheet.getRow(0).forEach(cell -> {
            int column = cell.getColumnIndex();
            sheet.autoSizeColumn(column);
        });
    }

    private Workbook create(String filePath) {
        String extension = FilenameUtils.getExtension(filePath);
        Workbook workbook;
        if (StringUtils.equals(extension, "xlsx")) {
            workbook = new XSSFWorkbook();     // new HSSFWorkbook() for generating `.xls` file
        } else if (StringUtils.equals(extension, "xls")) {
            workbook = new HSSFWorkbook();
        } else {
            logger.warn(String.format("Invalid file name: %s!", FilenameUtils.getName(filePath)));
            return null;
        }
        return workbook;
    }

    private CellStyle createHeadStyle(Workbook workbook) {
        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }

    private int limit(Workbook workbook) {
        int columns = -1;
        SpreadsheetVersion version = workbook.getSpreadsheetVersion();
        switch (version) {
            case EXCEL97:
                columns = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
                break;
            case EXCEL2007:
                columns = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
                break;
            default:
                logger.error("Can't identify current excel version, file will be ignored!");
        }
        return columns;
    }

    private Pair<List<List<String>>, List<List<Object>>> reconstruct(
        Pair<List<String>, List<List<Object>>> records, Workbook workbook) {
        Pair<List<List<String>>, List<List<Object>>> result = null;
        int boundary = limit(workbook) + 1;
        if (records != null) {
            List<List<String>> overview = null;
            List<String> headers = records.getKey();
            if (headers != null) {
                overview = eachHeader(headers, boundary);
            }

            List<List<Object>> items = null;
            List<List<Object>> data = records.getValue();
            if (data != null && data.size() > 0) {
                items = new ArrayList<>();
                for (List<Object> row : data) {
                    items.addAll(eachLine(row, boundary));
                }
            }
            result = new Pair<>(overview, items);
        }
        return result;
    }


    private List<List<String>> eachHeader(List<String> headers, int boundary) {
        List<List<String>> overview;
        int size = headers.size();
        overview = new ArrayList<>();
        if (size > boundary) {
            int segment = size / boundary + (size % boundary == 0 ? 0 : 1);
            int start, end;
            for (int counter = 0; counter < segment; counter++) {
                start = counter * boundary;
                if (counter != segment - 1) {
                    end = start + boundary;
                } else {
                    end = size;
                }
                List<String> head = new ArrayList<>();
                for (int index = start; index < end; index++) {
                    head.add(headers.get(index));
                }
                overview.add(head);
            }
        } else {
            overview.add(headers);
        }
        return overview;
    }

    private List<List<Object>> eachLine(List<Object> row, int boundary) {
        List<List<Object>> overall = new ArrayList<>();
        int size = row.size();
        if (size > boundary) {
            int segment = size / boundary + (size % boundary == 0 ? 0 : 1);
            int start, end;
            for (int counter = 0; counter < segment; counter++) {
                start = counter * boundary;
                if (counter != segment - 1) {
                    end = start + boundary;
                } else {
                    end = size;
                }
                List<Object> item = new ArrayList<>();
                for (int index = start; index < end; index++) {
                    item.add(row.get(index));
                }
                overall.add(item);
            }
        } else {
            overall.add(row);
        }
        return overall;
    }
}
