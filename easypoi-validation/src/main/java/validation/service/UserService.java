package validation.service;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

import validation.model.User;

public interface UserService {
    User findByAccount(String name);

    Workbook template();

    Workbook download();

    String upload(InputStream inputStream);
}
