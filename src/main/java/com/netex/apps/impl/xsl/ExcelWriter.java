package com.netex.apps.impl.xsl;

import com.netex.apps.intf.Writer;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
        final Path path = Paths.get(filePath);
        final Path parent = path.getParent();
        if (Files.isWritable(parent)) {
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            // Create a Workbook
            try (Workbook workbook = create(filePath)) {
                if (workbook == null) return;

                // Create a Sheet
                Sheet sheet = workbook.createSheet();

                fillData(dataInfo, workbook, sheet);

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

    private void fillData(Pair<List<String>, List<List<Object>>> dataInfo, Workbook workbook, Sheet sheet) {
        Integer rowNum = 0;
        List<String> lstHeader = dataInfo.getKey();
        if (lstHeader != null && lstHeader.size() > 0) {
            CellStyle headerCellStyle = createHeadStyle(workbook);

            // Create a Row
            Row headerRow = sheet.createRow(rowNum++);

            // Creating cells
            int column = 0;
            for (String title : lstHeader) {
                Cell cell = headerRow.createCell(column++);
                cell.setCellValue(title);
                cell.setCellStyle(headerCellStyle);
            }
        }

        CellStyle dateCellStyle = stylize(workbook);

        List<List<Object>> lstData = dataInfo.getValue();
        // Create Other rows and cells with data

        for (List<Object> rowData : lstData) {
            Row row = sheet.createRow(rowNum++);
            int columnNum = 0;
            for (Object colData : rowData) {
                Cell cell = row.createCell(columnNum++);
                if (colData != null) {
                    Class<?> dataClass = colData.getClass();
                    if (ClassUtils.isAssignable(dataClass, String.class)) {
                        cell.setCellValue(String.valueOf(colData));
                    } else if (ClassUtils.isAssignable(dataClass, Double.class, true)) {
                        cell.setCellValue(Double.class.cast(colData));
                    } else if (ClassUtils.isAssignable(dataClass, Date.class)) {
                        cell.setCellValue(Date.class.cast(colData));
                        cell.setCellStyle(dateCellStyle);
                    } else if (ClassUtils.isAssignable(dataClass, Boolean.class, true)) {
                        cell.setCellValue(Boolean.class.cast(colData));
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
}
