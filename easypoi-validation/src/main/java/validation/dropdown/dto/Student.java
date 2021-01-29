package validation.dropdown.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student {
    private long id;
    private String name;
    private Grade grade;
}
