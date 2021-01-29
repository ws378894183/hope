package validation.fixture;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import validation.model.Clazz;
import validation.model.Dynasty;
import validation.model.Grade;
import validation.model.User;
import validation.repository.DynastyRepository;
import validation.repository.GradeRepository;
import validation.repository.UserRepository;

@Component
@Slf4j
public class ValidationInit implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private DynastyRepository dynastyRepository;

    @Override
    public void run(String... args) throws Exception {
        this.initGradeAndClazz();
        this.initUser();
        this.initDynasty();
    }

    private void initGradeAndClazz() {
        if (this.gradeRepository.findAll().size() > 0) {
            ValidationInit.log.info("数据库年级表中有数据，暂不初始化");
            return;
        }
        final List<Grade> lists = new ArrayList<>();
        for (int g = 0; g < 3; g++) {
            final Grade grade = new Grade();
            final List<Clazz> clazzList = new ArrayList<>();

            grade.setName(this.getStrFromInt(g) + "年级");
            grade.setClazz(clazzList);
            for (int i = 0; i < 5; i++) {
                final Clazz clazz = new Clazz();
                clazz.setName(this.getStrFromInt(i) + "班");
                clazz.setGrade(grade);
                clazzList.add(clazz);
            }
            lists.add(grade);
        }
        this.gradeRepository.saveAll(lists);
    }

    private String getStrFromInt(int num) {
        String result = "";
        switch (num) {
        case 0:
            result = "一";
            break;
        case 1:
            result = "二";
            break;
        case 2:
            result = "三";
            break;
        case 3:
            result = "四";
            break;
        case 4:
            result = "五";
            break;
        default:
            result = "";
            break;
        }
        return result;
    }

    private void initUser() {
        if (this.userRepository.findAll().size() > 0) {
            ValidationInit.log.info("数据库用户表中有数据，暂不初始化");
            return;
        }
        final List<User> users = Lists.newArrayList(new User("李白", "libai"), new User("杜甫", "dufu"),
                new User("王勃", "wangbo"), new User("骆宾王", "luobinwang"));
        this.userRepository.saveAll(users);
    }

    private void initDynasty() {
        if (this.dynastyRepository.findAll().size() > 0) {
            ValidationInit.log.info("数据库朝代表中有数据，暂不初始化");
            return;
        }
        final List<Dynasty> dynasties =
                Lists.newArrayList(new Dynasty("秦"), new Dynasty("汉"), new Dynasty("唐"), new Dynasty("宋"));
        this.dynastyRepository.saveAll(dynasties);
    }
}
