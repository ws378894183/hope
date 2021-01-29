package validation.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dynasty")
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(appliesTo = "dynasty", comment = "朝代表")
public class Dynasty extends AbstractModel {

    private static final long serialVersionUID = 1141042753917522064L;

    private String name;

    @OneToMany(mappedBy = "dynasty")
    private List<User> user;

    public Dynasty(String name) {
        this.name = name;
    }

}
