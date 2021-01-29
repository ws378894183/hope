package validation.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "grade")
@org.hibernate.annotations.Table(appliesTo = "grade", comment = "年级表")
public class Grade extends AbstractModel {

    private static final long serialVersionUID = 3165379146611409071L;

    private String name;

    @OneToMany(mappedBy = "grade", cascade = CascadeType.ALL)
    private List<Clazz> clazz;

}
