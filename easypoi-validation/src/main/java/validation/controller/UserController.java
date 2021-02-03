package validation.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.afterturn.easypoi.word.WordExportUtil;
import lombok.extern.slf4j.Slf4j;
import validation.model.User;
import validation.service.UserService;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    public static final String PATTERN_NULL_CONN = "yyyyMMddHHmmss";

    @GetMapping("/{account}")
    public User getUser(@PathVariable String account) {
        return this.userService.findByAccount(account);
    }

    @GetMapping("/template")
    public void template(final HttpServletResponse response) {
        final Workbook workbook = this.userService.template();
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=%s",
                            URLEncoder.encode("用户导出模板" + LocalDateTime.now() + ".xlsx", "UTF-8")));
            workbook.write(out);
            out.flush();
        } catch (final Exception e) {
            UserController.log.error("用户导出模板失败", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                UserController.log.error("用户导出模板关闭流失败", e);
            }
        }
    }

    @PostMapping("/import")
    public String upload(@RequestParam("file") final MultipartFile file) {
        try {
            return this.userService.upload(file.getInputStream());

        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "导入成功";

    }

    @GetMapping(value = "/export")
    public void download(final HttpServletResponse response) {

        final Workbook workbook = this.userService.download();
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=%s",
                            URLEncoder.encode(
                                    "用户导出" + new SimpleDateFormat(UserController.PATTERN_NULL_CONN).format(new Date())
                                            + ".xlsx",
                                    "UTF-8")));
            workbook.write(out);
            out.flush();

        } catch (final Exception e) {
            UserController.log.error("用户导出失败", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                UserController.log.error("用户导出关闭流失败", e);
            }
        }
    }

    @GetMapping(value = "/export/doc")
    public void downloadDoc(final HttpServletResponse response) {
        OutputStream out = null;
        try {

            final HashMap<String, Object> map = new HashMap<>();
            map.put("name", "Response的乱码问题");
            final XWPFDocument doc = WordExportUtil.exportWord07(
                    "word/test.docx", map);
            out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=%s",
                            URLEncoder.encode("word文档" + LocalDateTime.now() + ".docx", "UTF-8")));
            doc.write(out);
            out.flush();

        } catch (final Exception e) {
            UserController.log.error("测试导出word", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                UserController.log.error("测试导出word", e);
            }
        }
    }
}
