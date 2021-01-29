package validation.cascade.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import validation.cascade.service.CascadeService;
import validation.cascade.util.EasyPoiCascadeUtil;
import validation.cascade.vo.StudentImportVO;

@Service
public class CascadeServiceImpl implements CascadeService {

    @Override
    public Workbook export() {

        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "下拉校验导入模板", ExcelType.XSSF),
                StudentImportVO.class, Lists.newArrayList());
        this.getCascadeData(workbook);
        return workbook;
    }

    public void getCascadeData(Workbook workbook) {

        final List<String> mainParent = Lists.newArrayList("小学", "初中", "高中");
        final List<String> allParent =
                Lists.newArrayList("小学", "初中", "一年级", "二年级", "三年级", "四年级", "五年级", "六年级", "初一", "初二", "初三");

        final Map<String, List<String>> children = new HashMap<>();
        children.put("小学", Lists.newArrayList("一年级", "二年级", "三年级", "四年级", "五年级", "六年级"));
        children.put("一年级", Lists.newArrayList("一班", "二班", "三班"));
        children.put("二年级", Lists.newArrayList("一班", "二班", "三班"));
        children.put("三年级", Lists.newArrayList("一班", "二班", "三班"));
        children.put("四年级", Lists.newArrayList("一班", "二班", "三班"));
        children.put("五年级", Lists.newArrayList("一班", "二班", "三班"));
        children.put("六年级", Lists.newArrayList("一班", "二班", "三班"));
        children.put("初中", Lists.newArrayList("初一", "初二", "初三"));
        children.put("初一", Lists.newArrayList("一班", "二班", "三班"));
        children.put("初二", Lists.newArrayList("一班", "二班", "三班"));
        children.put("初三", Lists.newArrayList("一班", "二班", "三班"));
        final ArrayList<Integer> columns = Lists.newArrayList(2, 3, 4);
        EasyPoiCascadeUtil.cascade(workbook, mainParent, allParent, children, columns, 0, 1, 65535);
    }

}
