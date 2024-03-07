package rebue.wheel.core.clone;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PojoEntity {

    /**
     * 学生ID
     */
    private Long id;

    /**
     * 学号
     */
    private String studentCode;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话号码1
     */
    private Double phone1;

    /**
     * 电话号码2
     */
    private Integer phone2;

    /**
     * 金额
     */
    private BigDecimal price;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 年龄
     */
    private Byte age;

}