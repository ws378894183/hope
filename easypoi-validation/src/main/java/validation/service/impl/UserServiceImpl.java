package validation.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import lombok.extern.slf4j.Slf4j;
import validation.cascade.util.EasyPoiCascadeUtil;
import validation.consts.ValidationConsts;
import validation.converter.UserConverter;
import validation.dropdown.util.EasyPoiDropDownUtil;
import validation.model.Dynasty;
import validation.model.Grade;
import validation.model.User;
import validation.repository.DynastyRepository;
import validation.repository.GradeRepository;
import validation.repository.UserRepository;
import validation.service.UserService;
import validation.vo.UserVO;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private DynastyRepository dynastyRepository;

    @Autowired
    private UserConverter userConverter;

    @Override
    public User findByAccount(String account) {
        return this.userRepository.findByAccount(account);
    }

    @Override
    public Workbook template() {
        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "下拉校验导入模板", ExcelType.XSSF),
                UserVO.class, Lists.newArrayList());
        this.cascadeData(workbook);
        this.dropDownData(workbook);
        return workbook;
    }

    private void cascadeData(Workbook workbook) {
        final List<String> allParent = new ArrayList<>();
        final Map<String, List<String>> children = new HashMap<>();
        final ArrayList<Integer> columns = Lists.newArrayList(3, 4);
        final List<String> mainParent = new ArrayList<>();
        final List<Grade> grades = this.gradeRepository.findAll();
        grades.forEach(t -> {
            final List<String> subList = new ArrayList<>();
            final String name = t.getName() + ValidationConsts.VALIDATION_SPILT + t.getId()
                    + ValidationConsts.VALIDATION_SPILT;
            mainParent.add(name);
            allParent.add(name);
            t.getClazz().forEach(v -> {
                final String cName = v.getName() + ValidationConsts.VALIDATION_SPILT + v.getId()
                        + ValidationConsts.VALIDATION_SPILT;
                subList.add(cName);
            });
            children.put(name, subList);
        });

        EasyPoiCascadeUtil.cascade(workbook, mainParent, allParent, children, columns, 0, 1, 65535);

    }

    private void dropDownData(Workbook workbook) {
        final List<Dynasty> dynasties = this.dynastyRepository.findAll();
        final List<String> dyStr = dynasties.stream().map(t -> {
            return t.getName() + ValidationConsts.VALIDATION_SPILT + t.getId() + ValidationConsts.VALIDATION_SPILT;
        }).collect(Collectors.toList());
        final Map<String, Object> map = new HashMap<>();
        map.put("col", 6);
        map.put("list", dyStr);
        final List<Map<String, Object>> constraint = new ArrayList<>();
        constraint.add(map);
        EasyPoiDropDownUtil.setConstraint(constraint, workbook, 0, 1, 65535);
    }

    @Override
    public String upload(InputStream fileStream) {
        final ImportParams params = new ImportParams();
        final StringBuilder errorMsg = new StringBuilder();
        params.setHeadRows(1);
        params.setNeedVerify(true);

        try {
            final ExcelImportResult<UserVO> result = ExcelImportUtil.importExcelMore(fileStream, UserVO.class, params);
            if (result.getList().isEmpty() && result.getFailList().isEmpty()) {

            }
            if (result.isVerifyFail()) {
                final List<UserVO> failList = result.getFailList();
                failList.forEach(l -> {
                    errorMsg.append("第" + (l.getRowNum() + 1) + "行," + l.getErrorMsg() + "! \n");
                });
            }
            if (errorMsg.length() > 0) {
                UserServiceImpl.log.error(errorMsg.toString());
                return errorMsg.toString();
            }
            final List<User> userList = this.userConverter.toListModel(result.getList());
            if (CollectionUtils.isNotEmpty(userList)) {
                this.userRepository.saveAll(userList);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        if (errorMsg.length() == 0) {
            return "导入成功";
        }
        return errorMsg.toString();

    }

    @Override
    public Workbook download() {
        final List<User> models = this.userRepository.findAll();
        final List<UserVO> list = new ArrayList<>(models.size() == 0 ? 1 : models.size());
        models.forEach(model -> {
            final UserVO vo = this.userConverter.toVO(model);
            list.add(vo);
        });
        final ExportParams param = new ExportParams("用户导出", "", ExcelType.XSSF);

        return ExcelExportUtil.exportExcel(param, UserVO.class, list);
    }
}
