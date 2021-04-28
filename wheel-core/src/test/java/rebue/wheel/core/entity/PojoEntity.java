package rebue.wheel.core.entity;

import java.math.BigDecimal;

import lombok.Data;

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
	private Boolean name;

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
