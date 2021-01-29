package validation.cascade.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentImportVO {
    @Excel(name = "ID")
    private long id;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "学校")
    private String school;
    @Excel(name = "年级")
    private String grade;
    @Excel(name = "班级")
    private String clazz;
}
