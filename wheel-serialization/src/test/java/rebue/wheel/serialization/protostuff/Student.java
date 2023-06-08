package rebue.wheel.serialization.protostuff;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long   id;
    private String code;
    private String name;
    private short  age;
    private Date   birthDay;
}
