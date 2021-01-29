package validation.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "class")
@org.hibernate.annotations.Table(appliesTo = "class", comment = "班级表")
public class Clazz extends AbstractModel {

    private static final long serialVersionUID = -5129864874544719027L;

    private String name;

    @ManyToOne
    private Grade grade;

    @OneToMany(mappedBy = "clazz")
    private List<User> user;
}
