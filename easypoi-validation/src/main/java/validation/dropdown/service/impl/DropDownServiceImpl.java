package validation.dropdown.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import validation.dropdown.dto.Grade;
import validation.dropdown.service.DropDownService;
import validation.dropdown.util.EasyPoiDropDownUtil;
import validation.dropdown.vo.StudentImportVO;

@Service
public class DropDownServiceImpl implements DropDownService {

    @Override
    public Workbook export() {
        final List<StudentImportVO> list = new ArrayList<>(1);
        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "下拉校验导入模板", ExcelType.XSSF),
                StudentImportVO.class, list);
        //取第一个sheet加校验
        final Sheet sheet = workbook.getSheetAt(0);
        EasyPoiDropDownUtil.sheetConstraintXSSF(sheet, 0, 65535, 2, 2, this.buildData());
        return workbook;
    }

    @Override
    public Workbook export1() {
        final List<StudentImportVO> list = new ArrayList<>(1);
        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "下拉校验导入模板", ExcelType.XSSF),
                StudentImportVO.class, list);
        final List<Map<String, Object>> constraint = new ArrayList<>();
        final Map<String, Object> map = new HashMap<>();
        map.put("col", 2);
        map.put("list", this.buildData());
        constraint.add(map);
        EasyPoiDropDownUtil.setConstraint(constraint, workbook, workbook.getSheet("下拉校验导入模板"), 0, 65535);
        return workbook;
    }

    public List<String> buildData() {
        final Grade one = new Grade(1, "一年级");
        final Grade two = new Grade(2, "二年级");
        final Grade three = new Grade(3, "三年级");
        final List<Grade> grades = Lists.newArrayList(one, two, three);
        final List<String> strs = new ArrayList<>();
        grades.stream().map(t -> {
            return t.getName() + "_" + t.getId();
        }).forEach(strs::add);
        return strs;
    }
}
