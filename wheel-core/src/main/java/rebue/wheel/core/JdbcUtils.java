package rebue.wheel.core;

import lombok.Data;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
                    String    tableName = tables.getString("TABLE_NAME");
                    TableMeta table     = new TableMeta();
                    dbMeta.tables.add(table);
                    table.name = tableName;
                    table.remark = tables.getString("REMARKS");
                    // 获取主键
                    ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
                    while (primaryKeysResultSet.next()) {
                        table.primaryKeys.add(primaryKeysResultSet.getString("COLUMN_NAME"));
                    }
                    // 获取unique字段
                    ResultSet uniquesResultSet = metaData.getIndexInfo(null, null, tableName, true, false);
                    while (uniquesResultSet.next()) {
                        String columnName = uniquesResultSet.getString("COLUMN_NAME");
                        // 排除主键
                        if (table.primaryKeys.contains(columnName)) continue;
                        table.uniques.add(columnName);
                    }
                    // 获取外键
                    ResultSet importedKeyResultSet = metaData.getImportedKeys(null, null, tableName);
                    while (importedKeyResultSet.next()) {
                        ImportKeyMeta importKey = new ImportKeyMeta();
                        importKey.fkTableName = importedKeyResultSet.getString("FKTABLE_NAME");
                        importKey.fkColumnName = importedKeyResultSet.getString("FKCOLUMN_NAME");
                        importKey.pkTableName = importedKeyResultSet.getString("PKTABLE_NAME");
                        importKey.pkColumnName = importedKeyResultSet.getString("PKCOLUMN_NAME");
                        table.importedKeys.add(importKey);
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
                            column.isForeignKey = false;
                            for (ImportKeyMeta importKey : table.importedKeys) {
                                if (column.name.equalsIgnoreCase(importKey.fkColumnName)) {
                                    column.isForeignKey = true;
                                    column.referencedTableName = importKey.pkTableName;
                                    column.referencedColumnName = importKey.pkColumnName;
                                }
                            }
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
        private String              name;
        /**
         * 表注释
         */
        private String              remark;
        /**
         * 表的主键列表
         */
        private List<String>        primaryKeys  = new LinkedList<>();
        /**
         * 表的unique列表
         */
        private List<String>        uniques      = new LinkedList<>();
        /**
         * 表的外键列表
         */
        private List<ImportKeyMeta> importedKeys = new LinkedList<>();
        /**
         * 列集合
         */
        private List<ColumnMeta>    columns      = new LinkedList<>();
    }

    @Data
    public static class ImportKeyMeta {
        /**
         * 外键表名
         */
        private String fkTableName;
        /**
         * 外键列名
         */
        private String fkColumnName;
        /**
         * 主键表名
         */
        private String pkTableName;
        /**
         * 主键列名
         */
        private String pkColumnName;
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
        /**
         * 是否外键
         */
        private Boolean isForeignKey;
        /**
         * 外键引用的表名
         */
        private String  referencedTableName;
        /**
         * 外键引用的列名
         */
        private String  referencedColumnName;
    }

}
