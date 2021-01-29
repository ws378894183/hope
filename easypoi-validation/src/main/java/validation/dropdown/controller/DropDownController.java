package validation.dropdown.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import validation.dropdown.service.impl.DropDownServiceImpl;

@Slf4j
@RestController
@RequestMapping(value = "/dropDown")
public class DropDownController {

    @Autowired
    private DropDownServiceImpl dropDownService;

    @GetMapping("/export")
    public void export(final HttpServletResponse response) {
        final Workbook workbook = this.dropDownService.export();
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=%s",
                            URLEncoder.encode("下拉校验" + LocalDateTime.now() + ".xlsx", "UTF-8")));
            workbook.write(out);
            out.flush();
        } catch (final Exception e) {
            DropDownController.log.error("下拉校验导出失败", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                DropDownController.log.error("下拉校验导出关闭流失败", e);
            }
        }
    }

    @GetMapping("/export1")
    public void export1(final HttpServletResponse response) {
        final Workbook workbook = this.dropDownService.export1();
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=%s",
                            URLEncoder.encode("下拉校验" + LocalDateTime.now() + ".xlsx", "UTF-8")));
            workbook.write(out);
            out.flush();
        } catch (final Exception e) {
            DropDownController.log.error("下拉校验导出失败", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                DropDownController.log.error("下拉校验导出关闭流失败", e);
            }
        }
    }
}
