package rebue.wheel.serialization.jackson;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategiesEx.SnakeCaseStrategy.class)
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;
    private String            num;
    private String            name;
    private short             age;
    private Date              birthDate;

}
