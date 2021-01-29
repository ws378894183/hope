package validation.cascade.controller;

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
import validation.cascade.service.CascadeService;

@Slf4j
@RestController
@RequestMapping("/cascade")
public class CascadeController {

    @Autowired
    private CascadeService cascadeService;

    @GetMapping("/export")
    public void export(final HttpServletResponse response) {
        final Workbook workbook = this.cascadeService.export();
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=%s",
                            URLEncoder.encode("级联校验" + LocalDateTime.now() + ".xlsx", "UTF-8")));
            workbook.write(out);
            out.flush();
        } catch (final Exception e) {
            CascadeController.log.error("级联校验导出失败", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                CascadeController.log.error("级联校验导出关闭流失败", e);
            }
        }
    }
}
