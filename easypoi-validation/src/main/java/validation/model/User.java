package validation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import validation.model.enums.Sex;

@Getter
@Setter
@Entity
//@Table(name = "user", indexes = {
//        @Index(name = "event_id", columnList = "id"),
//        @Index(name = "event_code", columnList = "code"),
//})
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(appliesTo = "user", comment = "用户表")
public class User extends AbstractModel {

    private static final long serialVersionUID = 7943456999074997573L;

    private String name;

    private boolean disabled = false;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(length = 11)
    private String phone;

    @Column(nullable = false)
    private String account;

    @ManyToOne
    private Clazz clazz;

    @ManyToOne
    private Dynasty dynasty;

    public User(String name, String account) {
        this.name = name;
        this.account = account;
    }

}
