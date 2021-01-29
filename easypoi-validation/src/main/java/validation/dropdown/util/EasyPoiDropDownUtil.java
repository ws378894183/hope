package validation.dropdown.util;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class EasyPoiDropDownUtil {

    public static void sheetConstraintXSSF(final Sheet sheet, final int firstRow, final int lastRow,
            final int firstCol, final int lastCol, final List<String> strings) {
        //  生成下拉列表 只对(x，x)单元格有效
        final CellRangeAddressList cellRangeAddressList =
                new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        final XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        final XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createExplicitListConstraint(strings.toArray(new String[1]));
        final XSSFDataValidation validation =
                (XSSFDataValidation) dvHelper.createValidation(dvConstraint, cellRangeAddressList);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    public static void setConstraint(final List<Map<String, Object>> constraint, final Workbook workbook, int sheetNum,
            final int startRow,
            final int lastRow) {
        EasyPoiDropDownUtil.setConstraint(constraint, workbook, workbook.getSheetAt(sheetNum), startRow, lastRow);
    }

    public static void setConstraint(final List<Map<String, Object>> constraint, final Workbook workbook,
            String sheetName,
            final int startRow,
            final int lastRow) {
        EasyPoiDropDownUtil.setConstraint(constraint, workbook, workbook.getSheet(sheetName), startRow, lastRow);
    }

    @SuppressWarnings("unchecked")
    public static void setConstraint(final List<Map<String, Object>> constraint, final Workbook workbook, Sheet sheet,
            final int startRow,
            final int lastRow) {
        //创建一个专门用来下拉列表信息的隐藏sheet页，因此也不能在现实页之前创建，否则无法隐藏。
        String hideSheetName = "hidden";
        while (workbook.getSheet(hideSheetName) != null) {
            hideSheetName = hideSheetName + new Random().nextInt(100);
        }
        //将下拉框数据放到新的sheet里，然后excel通过新的sheet数据加载下拉框数据
        final Sheet hidden = workbook.createSheet(hideSheetName);

        //创建单元格对象
        Cell cell = null;
        for (final Map<String, Object> map : constraint) {
            final Integer col = (Integer) map.get("col");
            final List<String> everyConstraint = (List<String>) map.get("list");
            if (everyConstraint.size() > 0) {
                //遍历，将数据取出来放到新sheet的单元格中
                for (int i = 0, length = everyConstraint.size(); i < length; i++) {
                    //取出数组中的每个元素
                    final String name = everyConstraint.get(i);
                    //根据i创建相应的行对象
                    final Row row = hidden.getRow(i) == null ? hidden.createRow(i) : hidden.getRow(i);
                    //创建每一行中的第一个单元格
                    cell = row.createCell(col);
                    //然后将元素赋值给这个单元格
                    cell.setCellValue(name);
                }
                // 设置名称引用的公式
                final String a = EasyPoiDropDownUtil.getColumn(col);
                // 创建名称，可被其他单元格引用
                final Name namedCell = workbook.createName();
                namedCell.setNameName(hideSheetName + col);
                namedCell.setRefersToFormula(
                        hideSheetName + "!$" + a + "$1:$" + String.valueOf(a) + "$"
                                + everyConstraint.size());
                final CellRangeAddressList regions = new CellRangeAddressList(startRow,
                        lastRow, col, col);
                //给sheet页加相同的校验
                if (sheet.getSheetName().equals(hideSheetName)) {
                    break;
                }
                final XSSFDataValidationHelper dvHelper =
                        new XSSFDataValidationHelper((XSSFSheet) sheet);
                //加载数据,将名称为hidden的sheet中的数据转换为List形式
                final XSSFDataValidationConstraint dvConstraint =
                        (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(hideSheetName + col);
                final XSSFDataValidation validation =
                        (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
                validation.setShowErrorBox(true);
                //将数据赋给下拉列表
                sheet.addValidationData(validation);
            }
        }
        //将hidden sheet设置为隐藏 调试时可以注释掉
        workbook.setSheetHidden(workbook.getSheetIndex("hidden"), true);
    }

    public static String getColumn(int col) {
        if (col < 0) {
            return null;
        }
        String columnStr = "";
        do {
            if (columnStr.length() > 0) {
                col--;
            }
            columnStr = ((char) ((col % 26) + 'A')) + columnStr;
            col = col / 26;
        } while (col > 0);
        return columnStr;
    }
}
