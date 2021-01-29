package validation.cascade.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class EasyPoiCascadeUtil {

    public static void cascade(final Workbook workbook, final List<String> mainParent, final List<String> allParent,
            final Map<String, List<String>> children,
            final ArrayList<Integer> columns, int sheetNum, final int startRow, final int endRow) {
        EasyPoiCascadeUtil.cascade(workbook, mainParent, allParent, children, columns, workbook.getSheetAt(sheetNum),
                startRow, endRow);
    }

    public static void cascade(final Workbook workbook, final List<String> mainParent, final List<String> allParent,
            final Map<String, List<String>> children,
            final ArrayList<Integer> columns, String sheetName, final int startRow, final int endRow) {
        EasyPoiCascadeUtil.cascade(workbook, mainParent, allParent, children, columns, workbook.getSheet(sheetName),
                startRow, endRow);
    }

    public static void cascade(final Workbook workbook, final List<String> mainParent, final List<String> allParent,
            final Map<String, List<String>> children,
            final ArrayList<Integer> columns, Sheet sheet, final int startRow, final int endRow) {
        //创建一个专门用来存放地区信息的隐藏sheet页
        //因此也不能在现实页之前创建，否则无法隐藏。
        String hideSheetName = "area";
        while (workbook.getSheet(hideSheetName) != null) {
            hideSheetName = hideSheetName + new Random().nextInt(100);
        }
        final Sheet hideSheet = workbook.createSheet(hideSheetName);
        //这一行作用是将此sheet隐藏，功能未完成时注释此行,可以查看隐藏sheet中信息是否正确
        workbook.setSheetHidden(workbook.getSheetIndex(hideSheet), true);

        int rowId = 0;
        // 设置第一行，存省的信息
        final String parentNameKey = "父列表";
        final Row provinceRow = hideSheet.createRow(rowId++);
        provinceRow.createCell(0).setCellValue(parentNameKey);
        for (int i = 0; i < mainParent.size(); i++) {
            final Cell provinceCell = provinceRow.createCell(i + 1);
            provinceCell.setCellValue(mainParent.get(i));
        }
        // 添加父级别的名称管理器
        final String parentRange = EasyPoiCascadeUtil.getRange(1, rowId, mainParent.size());
        final Name parentName = workbook.createName();
        //key不可重复
        parentName.setNameName(parentNameKey);
        final String parentFormula = hideSheetName + "!" + parentRange;
        parentName.setRefersToFormula(parentFormula);

        // 将具体的数据写入到每一行中，行开头为父级区域，后面是子区域。
        for (final String key : allParent) {
            final String[] son = children.get(key).toArray(new String[1]);
            final Row row = hideSheet.createRow(rowId++);
            row.createCell(0).setCellValue(key);
            for (int j = 0; j < son.length; j++) {
                final Cell cell = row.createCell(j + 1);
                cell.setCellValue(son[j]);
            }

            // 添加名称管理器
            final String range = EasyPoiCascadeUtil.getRange(1, rowId, son.length);
            final Name name = workbook.createName();
            //key不可重复
            name.setNameName(key);
            final String formula = hideSheetName + "!" + range;
            System.out.println(formula);
            name.setRefersToFormula(formula);
        }

        if (sheet.getSheetName().equals(hideSheetName)) {
            return;
        }

        final XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);

        //最外层的规则 加载数据,将名称为hidden的sheet中的数据转换为List形式
        final XSSFDataValidationConstraint dvConstraint =
                (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(parentNameKey);
        // 四个参数分别是：起始行、终止行、起始列、终止列
        final CellRangeAddressList provRangeAddressList =
                new CellRangeAddressList(startRow, endRow, columns.get(0), columns.get(0));
        final XSSFDataValidation provinceDataValidation =
                (XSSFDataValidation) dvHelper.createValidation(dvConstraint, provRangeAddressList);

        //验证
        provinceDataValidation.createErrorBox("error", "请选择正确的数据");
        provinceDataValidation.setShowErrorBox(true);
        provinceDataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(provinceDataValidation);

        for (int j = 0; j < (columns.size() - 1); j++) {
            final String column = EasyPoiCascadeUtil.getColumn(columns.get(j));
            EasyPoiCascadeUtil.setDataValidation(column, (XSSFSheet) sheet, startRow, endRow,
                    columns.get(j + 1));
        }
    }

    /**
     * 加载下拉列表内容
     *
     * @param formulaString
     * @param naturalRowIndex
     * @param naturalColumnIndex
     * @param dvHelper
     * @return
     */
    private static DataValidation getDataValidationByFormula(
            final String formulaString, final int firstRow, final int lastRow, final int naturalColumnIndex,
            final XSSFDataValidationHelper dvHelper) {
        // 加载下拉列表内容
        // 举例：若formulaString = "INDIRECT($A$2)" 表示规则数据会从名称管理器中获取key与单元格 A2 值相同的数据，
        //如果A2是江苏省，那么此处就是江苏省下的市信息。
        final XSSFDataValidationConstraint dvConstraint =
                (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(formulaString);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        final CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                lastRow, naturalColumnIndex, naturalColumnIndex);
        // 数据有效性对象
        // 绑定
        final XSSFDataValidation data_validation_list =
                (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
        data_validation_list.setEmptyCellAllowed(false);
        if (data_validation_list instanceof XSSFDataValidation) {
            data_validation_list.setSuppressDropDownArrow(true);
            data_validation_list.setShowErrorBox(true);
        } else {
            data_validation_list.setSuppressDropDownArrow(false);
        }
        // 设置输入信息提示信息
        data_validation_list.createPromptBox("下拉选择提示", "请使用下拉方式选择合适的值！");
        return data_validation_list;
    }

    /**
     * 设置有效性
     *
     * @param offset 主影响单元格所在列，即此单元格由哪个单元格影响联动
     * @param sheet
     * @param rowNum 行数
     * @param colNum 列数
     */
    public static void setDataValidation(final String offset, final XSSFSheet sheet, final int startRow,
            final int endRow,
            final int colNum) {
        final XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        DataValidation data_validation_list;
        data_validation_list = EasyPoiCascadeUtil.getDataValidationByFormula(
                "INDIRECT($" + offset + (2) + ")", startRow, endRow, colNum, dvHelper);
        sheet.addValidationData(data_validation_list);
    }

    /**
     * 计算formula
     *
     * @param offset 偏移量，如果给0，表示从A列开始，1，就是从B列
     * @param rowId 第几行
     * @param colCount 一共多少列
     * @return 如果给入参 1,1,10. 表示从B1-K1。最终返回 $B$1:$K$1
     */
    public static String getRange(final int offset, final int rowId, final int colCount) {
        return "$" + EasyPoiCascadeUtil.getColumn(offset) + "$" + rowId + ":$"
                + EasyPoiCascadeUtil.getColumn(((offset + colCount) - 1)) + "$" + rowId;
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

    public static int excelColStrToNum(String column) {
        int num = 0;
        int result = 0;
        final int length = column.length();
        for (int i = 0; i < length; i++) {
            final char ch = column.charAt(length - i - 1);
            num = (ch - 'A') + 1;
            num *= Math.pow(26, i);
            result += num;
        }
        return result - 1;
    }

    public static void main(String[] args) {
        final int col = 701 + (26 * 26 * 26) + (26 * 26 * 26 * 26);
        System.out.println("origin  " + EasyPoiCascadeUtil.getColumn(col));
        System.out
                .println(EasyPoiCascadeUtil.excelColStrToNum(EasyPoiCascadeUtil.getColumn(col)) == col);

    }
}
