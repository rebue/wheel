package rebue.wheel.protostuff;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;
    private String            num;
    private String            name;
    private short             age;
    private Date              birthday;

}
