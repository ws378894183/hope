package validation.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import validation.consts.ValidationConsts;
import validation.model.User;
import validation.model.enums.Sex;
import validation.repository.ClazzRepository;
import validation.repository.DynastyRepository;
import validation.vo.UserVO;

@Component
public class UserConverter extends AbstractConverter<User, UserVO> {

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private DynastyRepository dynastyRepository;

    @Override
    public User toModel(UserVO vo) {
        final User user = new User();
        user.setAccount(vo.getAccount());
        if (StringUtils.isNotEmpty(vo.getClazz())) {
            final String id = vo.getClazz().substring(vo.getClazz().indexOf(ValidationConsts.VALIDATION_SPILT) + 1,
                    vo.getClazz().lastIndexOf(ValidationConsts.VALIDATION_SPILT));
            user.setClazz(this.clazzRepository.findById(Long.valueOf(id)).get());
        }
        user.setName(vo.getName());
        user.setPhone(vo.getPhone());
        if (StringUtils.isNotBlank(vo.getSex())) {
            user.setSex(Sex.valueOf(vo.getSex()));
        }
        if (StringUtils.isNotBlank(vo.getDynasty())) {
            final String id = vo.getDynasty().substring(vo.getDynasty().indexOf(ValidationConsts.VALIDATION_SPILT) + 1,
                    vo.getDynasty().lastIndexOf(ValidationConsts.VALIDATION_SPILT));
            user.setDynasty(this.dynastyRepository.findById(Long.valueOf(id)).get());
        }

        return user;
    }

    @Override
    public UserVO toVO(User model) {
        final UserVO vo = new UserVO();
        vo.setAccount(model.getAccount());
        if (model.getClazz() != null) {
            vo.setClazz(model.getClazz().getName());
            vo.setGrade(model.getClazz().getGrade().getName());
        }
        vo.setName(model.getName());
        vo.setPhone(model.getPhone());
        vo.setSex(model.getSex() == null ? null : model.getSex().name());
        vo.setDynasty(model.getDynasty() == null ? null : model.getDynasty().getName());
        return vo;
    }

}
