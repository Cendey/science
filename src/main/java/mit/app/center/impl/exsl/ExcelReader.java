package mit.app.center.impl.exsl;

import javafx.util.Pair;
import mit.app.center.intf.Reader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.impl.exsl.ExcelReader</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class ExcelReader implements Reader {

    @Override
    public List<Pair<List<String>, List<List<String>>>> read(String filePath, Boolean isFileWithHeader) throws IOException {
        List<Pair<List<String>, List<List<String>>>> result = null;

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        try (Workbook workbook = WorkbookFactory.create(new File(filePath))) {
            // Retrieving the number of sheets in the Workbook
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            workbook.forEach(sheet -> {sheet.forEach(row -> {

            });});
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object parse(Cell cell) {
        Object value = null;
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                value= cell.getBooleanCellValue();
                break;
            case STRING:
                value =cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value=cell.getNumericCellValue();
                }
                break;
            case FORMULA:
                System.out.print(cell.getCellFormula());
                break;
            case BLANK:
                System.out.print("");
                break;
            default:
                System.out.print("");
        }

       return value;
    }
}
