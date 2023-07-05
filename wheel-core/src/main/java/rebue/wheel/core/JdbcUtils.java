package rebue.wheel.core;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdbcUtils {
    public static Connection getConnection(ConnectParam connectParam) throws SQLException {
        String url;
        if (connectParam.getParams() == null || connectParam.getParams().length == 0) url = connectParam.getUrl();
        else url = String.format(connectParam.getUrl(), (Object[]) connectParam.getParams());
        return DriverManager.getConnection(url, connectParam.getProperties());
    }

    /**
     * 获取数据库元数据
     *
     * @param connectParam     连接参数
     * @param tableNamePattern 过滤表名的正则表达式
     * @return 数据库元数据
     */
    public static DbMeta getDbMeta(ConnectParam connectParam, String tableNamePattern) throws SQLException {
        DbMeta dbMeta = new DbMeta();
        try (Connection conn = JdbcUtils.getConnection(connectParam)) {
            DatabaseMetaData metaData = conn.getMetaData();
            dbMeta.name = conn.getCatalog();
            dbMeta.productName = metaData.getDatabaseProductName();
            // 获取所有表
            try (ResultSet tables = metaData.getTables(null, null, tableNamePattern, new String[]{"TABLE"})) {
                // 遍历表元数据并读出表名和结构
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    TableMeta table = new TableMeta();
                    dbMeta.tables.add(table);
                    table.name = tableName;
                    table.remark = tables.getString("REMARKS");
                    ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
                    while (primaryKeysResultSet.next()) {
                        table.primaryKeys.add(primaryKeysResultSet.getString("COLUMN_NAME"));
                    }
                    ResultSet uniquesResultSet = metaData.getIndexInfo(null, null, tableName, true, false);
                    while (uniquesResultSet.next()) {
                        table.uniques.add(uniquesResultSet.getString("COLUMN_NAME"));
                    }
                    // 获取表的列元数据
                    try (ResultSet columnResultSet = metaData.getColumns(null, null, tableName, null)) {
                        // 遍历列元数据并读出列名和数据类型
                        while (columnResultSet.next()) {
                            ColumnMeta column = new ColumnMeta();
                            table.columns.add(column);
                            column.name = columnResultSet.getString("COLUMN_NAME");
                            column.type = columnResultSet.getInt("DATA_TYPE");
                            column.typeName = columnResultSet.getString("TYPE_NAME");
                            column.precision = columnResultSet.getInt("COLUMN_SIZE");
                            column.scale = columnResultSet.getInt("DECIMAL_DIGITS");
                            column.isPrimaryKey = table.primaryKeys.contains(column.name);
                            column.isUnique = table.uniques.contains(column.name);
                            column.isNullable = columnResultSet.getBoolean("IS_NULLABLE");
                            column.remark = columnResultSet.getString("REMARKS");
                        }
                    }
                }
            }
            return dbMeta;
        }
    }

    /**
     * 连接参数
     */
    @Data
    public static class ConnectParam {
        /**
         * 数据库连接字符串
         */
        private String     url;
        /**
         * url参数
         */
        private String[]   params;
        /**
         * 连接属性
         */
        private Properties properties;
    }

    /**
     * 数据库元
     */
    @Data
    public static class DbMeta {
        /**
         * 数据库名称
         */
        private String          name;
        /**
         * 数据库产品名称
         */
        private String          productName;
        /**
         * 表集合
         */
        private List<TableMeta> tables = new LinkedList<>();
    }

    @Data
    public static class TableMeta {
        /**
         * 表名
         */
        private String           name;
        /**
         * 表注释
         */
        private String           remark;
        /**
         * 表的主键列表
         */
        private List<String>     primaryKeys = new LinkedList<>();
        /**
         * 表的unique列表
         */
        private List<String>     uniques     = new LinkedList<>();
        /**
         * 列集合
         */
        private List<ColumnMeta> columns     = new LinkedList<>();
    }

    @Data
    public static class ColumnMeta {
        /**
         * 列名
         */
        private String  name;
        /**
         * 列类型
         */
        private Integer type;
        /**
         * 列类型名称
         */
        private String  typeName;
        /**
         * 精度
         */
        private Integer precision;
        /**
         * 数值范围
         */
        private Integer scale;
        /**
         * 是否主键
         */
        private Boolean isPrimaryKey;
        /**
         * 是否唯一
         */
        private Boolean isUnique;
        /**
         * 是否可空
         */
        private Boolean isNullable;
        /**
         * 列注释
         */
        private String  remark;
    }

}
