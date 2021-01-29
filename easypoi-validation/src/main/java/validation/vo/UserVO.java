package validation.vo;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO implements IExcelDataModel, IExcelModel {
    @Excel(name = "姓名")
    private String name;

    @Excel(name = "电话", width = 30)
    private String phone;

    @NotBlank(message = "账号不能为空")
    @Length(max = 255, message = "账号不能超过{max}位")
    @Excel(name = "账号", isImportField = "true", width = 30)
    private String account;

    @Excel(name = "年级")
    private String grade;

    @Excel(name = "班级")
    private String clazz;

    @Excel(name = "性别", replace = {"男_male", "女_female" }, addressList = true)
    private String sex;

    @Excel(name = "朝代")
    private String dynasty;

    private String erroMsg;

    private Integer rowNum;

    @Override
    public String getErrorMsg() {
        return this.erroMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.erroMsg = errorMsg;
    }

    @Override
    public Integer getRowNum() {
        return this.rowNum;
    }

    @Override
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}
