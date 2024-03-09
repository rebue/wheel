package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 数据库元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbMeta {
    /**
     * 数据库名称
     */
    private String          name;
    /**
     * 数据库产品名称
     */
    private String          productName;
    /**
     * 数据库JDBC驱动类
     */
    private String          driverClass;
    /**
     * 数据库JDBC驱动名称
     */
    private String          driverName;
    /**
     * 数据库JDBC驱动的版本
     */
    private String          driverVersion;
    /**
     * 数据库JDBC驱动的主版本
     */
    private int             driverMajorVersion;
    /**
     * 数据库JDBC驱动的次版本
     */
    private int             driverMinorVersion;
    /**
     * 数据库连接URL
     */
    private String          url;
    /**
     * 数据库连接用户名称
     */
    private String          userName;
    /**
     * 数据库连接密码
     */
    private String          password;
    /**
     * 表名匹配模式
     */
    private String          tableNamePattern;
    /**
     * 表元数据的列表
     */
    @Builder.Default
    private List<TableMeta> tables = new LinkedList<>();
}
