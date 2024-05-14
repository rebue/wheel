package rebue.wheel.api.ra;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带有Boolean结果的附加内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(Include.NON_NULL)
public class BooleanRa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回Boolean的值
     */
    private Boolean           value;

}