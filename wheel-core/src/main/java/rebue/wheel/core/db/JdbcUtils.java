package rebue.wheel.core.db;

import com.google.common.base.CaseFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.db.meta.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.Types.*;

@Slf4j
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
     * @param connection       连接
     * @param tableNamePattern 过滤表名的正则表达式
     * @return 数据库元数据
     */
    public static DbMeta getDbMeta(Connection connection, String tableNamePattern) throws SQLException {
        DbMeta           dbMeta   = new DbMeta();
        DatabaseMetaData metaData = connection.getMetaData();
        dbMeta.setName(connection.getCatalog());
        dbMeta.setProductName(metaData.getDatabaseProductName());
        dbMeta.setDriverName(metaData.getDriverName());
        dbMeta.setDriverVersion(metaData.getDriverVersion());
        dbMeta.setDriverMajorVersion(metaData.getDriverMajorVersion());
        dbMeta.setDriverMinorVersion(metaData.getDriverMinorVersion());
        dbMeta.setUrl(metaData.getURL());
        dbMeta.setUserName(metaData.getUserName());
        dbMeta.setTableNamePattern(tableNamePattern);
        // 获取所有表
        try (ResultSet tables = metaData.getTables(null, null, tableNamePattern, new String[]{"TABLE"})) {
            // 遍历表元数据并读出表名和结构
            while (tables.next()) {
                String    tableName = tables.getString("TABLE_NAME");
                TableMeta table     = new TableMeta();
                dbMeta.getTables().add(table);
                table.setName(tableName);
                table.setRemark(tables.getString("REMARKS").replaceAll("\\\\n", "\n"));
                // 获取主键
                ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);
                while (primaryKeysResultSet.next()) {
                    table.getPrimaryKeys().add(primaryKeysResultSet.getString("COLUMN_NAME"));
                }
                // 获取unique字段
                ResultSet uniquesResultSet = metaData.getIndexInfo(null, null, tableName, true, false);
                while (uniquesResultSet.next()) {
                    String columnName = uniquesResultSet.getString("COLUMN_NAME");
                    // 排除主键
                    if (table.getPrimaryKeys().contains(columnName)) continue;
                    table.getUniques().add(columnName);
                }
                // 获取外键
                ResultSet foreignKeyResultSet = metaData.getImportedKeys(null, null, tableName);
                char      c                   = 'a';    // 数据库别名(主键表别名为a，外键表按字母表顺序b、c、d......)
                while (foreignKeyResultSet.next()) {
                    ForeignKeyMeta foreignKey = new ForeignKeyMeta();
                    foreignKey.setFkTableName(foreignKeyResultSet.getString("FKTABLE_NAME"));
                    foreignKey.setFkFieldName(foreignKeyResultSet.getString("FKCOLUMN_NAME"));
                    foreignKey.setPkTableName(foreignKeyResultSet.getString("PKTABLE_NAME"));
                    foreignKey.setPkFieldName(foreignKeyResultSet.getString("PKCOLUMN_NAME"));
                    foreignKey.setPkTableAlias(String.valueOf(++c));
                    table.getForeignKeys().add(foreignKey);
                }
                // 获取表的列元数据
                try (ResultSet columnResultSet = metaData.getColumns(null, null, tableName, null)) {
                    // 遍历列元数据并读出列名和数据类型
                    while (columnResultSet.next()) {
                        FieldMeta field = new FieldMeta();
                        table.getFields().add(field);
                        field.setName(columnResultSet.getString("COLUMN_NAME"));
                        field.setType(columnResultSet.getInt("DATA_TYPE"));
                        field.setTypeName(columnResultSet.getString("TYPE_NAME"));
                        field.setPrecision(columnResultSet.getInt("COLUMN_SIZE"));
                        field.setScale(columnResultSet.getInt("DECIMAL_DIGITS"));
                        field.setIsPrimaryKey(table.getPrimaryKeys().contains(field.getName()));
                        field.setIsUnique(table.getUniques().contains(field.getName()));
                        field.setIsNullable(columnResultSet.getBoolean("IS_NULLABLE"));
                        field.setRemark(columnResultSet.getString("REMARKS").replaceAll("\\\\n", "\n"));
                        field.setIsForeignKey(false);
                        // 判断是否是外键
                        for (ForeignKeyMeta foreignKey : table.getForeignKeys()) {
                            if (field.getName().equalsIgnoreCase(foreignKey.getFkFieldName())) {
                                String pkTableName = foreignKey.getPkTableName();
                                String pkFieldName = foreignKey.getPkFieldName();
                                field.setIsForeignKey(true);
                                field.setReferencedTableName(pkTableName);
                                field.setReferencedTableClassName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, pkTableName));
                                field.setReferencedTableInstanceName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, pkTableName));
                                field.setReferencedColumnName(pkFieldName);
                                field.setReferencedColumnClassName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, pkFieldName));
                                field.setReferencedColumnInstanceName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, pkFieldName));
                            }
                        }
                        // 如果是MySQL，判断是否是无符号
                        if ("MySQL".equalsIgnoreCase(dbMeta.getProductName())) {
                            PreparedStatement preparedStatement = connection.prepareStatement("""
                                    select count(*) from information_schema.COLUMNS
                                    where TABLE_NAME=? and COLUMN_NAME=? and COLUMN_TYPE LIKE '%unsigned'
                                    """);
                            preparedStatement.setString(1, tableName);
                            preparedStatement.setString(2, field.getName());
                            ResultSet resultSet = preparedStatement.executeQuery();
                            int       count     = 0;
                            if (resultSet.next()) {
                                count = resultSet.getInt(1);
                            }
                            field.setIsUnsigned(count > 0);
                        }
                    }
                }
            }
        }
        return dbMeta;
    }

    /**
     * 获取数据库元数据
     *
     * @param connectParam     连接参数
     * @param tableNamePattern 过滤表名的正则表达式
     * @return 数据库元数据
     */
    public static DbMeta getDbMeta(ConnectParam connectParam, String tableNamePattern) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection(connectParam)) {
            return getDbMeta(connection, tableNamePattern);
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
     * 将数据库元数据转换为POJO元数据列表
     *
     * @param dbMeta 数据库元数据
     * @return 返回POJO元数据列表
     */
    public static List<PojoMeta> dbMetaToPojoMetas(DbMeta dbMeta) {
        List<PojoMeta> pojoMetas = new ArrayList<>();
        for (TableMeta table : dbMeta.getTables()) {
            PojoMeta pojo = PojoMeta.builder()
                    .className(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table.getName()))
                    .instanceName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, table.getName()))
                    .lowerHyphenName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, table.getName()))
                    .remark(table.getRemark())
                    .remarks(RemarkUtils.getRemarks(table.getRemark()))
                    .title(RemarkUtils.getTitle(table.getRemark()))
                    .table(table)
                    .build();
            // 计算实例简名(不带项目前缀)
            String tableNameWithoutPrefix = table.getName();
            int    beginIndex             = tableNameWithoutPrefix.indexOf("_") + 1;
            pojo.setInstanceSimpleName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableNameWithoutPrefix.substring(beginIndex)));
            // 计算小写连字号名(不带项目前缀)
            beginIndex = pojo.getLowerHyphenName().indexOf("-") + 1;
            pojo.setLowerHyphenNameWithoutPrefix(pojo.getLowerHyphenName().substring(beginIndex));

            for (FieldMeta field : table.getFields()) {
                PropertyMeta property = PropertyMeta.builder()
                        .name(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName()))
                        .alias(pojo.getInstanceSimpleName() + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, field.getName()))
                        .remark(field.getRemark())
                        .remarks(RemarkUtils.getRemarks(field.getRemark()))
                        .title(pojo.getTitle() + RemarkUtils.getTitle(field.getRemark()))
                        .field(field)
                        .build();
                // 根据属性的字段元数据设置属性
                setPropertyByFieldMeta(property);
                // 添加到pojo的属性列表中
                pojo.getProperties().add(property);
                // 是否添加到pojo的ID列表中
                if (field.getIsPrimaryKey()) {
                    pojo.getIds().add(property);
                }
            }
            pojoMetas.add(pojo);
        }
        return pojoMetas;
    }

    /**
     * 根据属性的字段元数据设置属性
     *
     * @param property 属性元数据
     */
    private static void setPropertyByFieldMeta(PropertyMeta property) {
        Class<?> clazz;
        String   jsType;
        boolean  isKeyWord = false;
        Integer  fieldType = property.getField().getType();
        switch (fieldType) {
            case BIT, BOOLEAN -> {
                clazz = Boolean.class;
                jsType = "boolean";
            }
            case TINYINT -> {
                if (property.getName().startsWith("is")) {
                    clazz = Boolean.class;
                    jsType = "boolean";
                } else {
                    clazz = Short.class;
                    jsType = "number";
                    // 如果不是字典类字段，加入keyword
                    if (!property.getField().getName().endsWith("_DIC")) {
                        isKeyWord = true;
                    }
                }
            }
            case SMALLINT -> {
                clazz = Short.class;
                jsType = "number";
                isKeyWord = true;
            }
            case INTEGER -> {
                clazz = Integer.class;
                jsType = "number";
                isKeyWord = true;
            }
            case BIGINT -> {
                // 判断BIGINT为雪花算法生成的ID字段，所以不会加入keyword
                clazz = Long.class;
                jsType = "number";
            }
            case FLOAT, REAL, DOUBLE, NUMERIC, DECIMAL -> {
                clazz = BigDecimal.class;
                jsType = "number";
                // 如果不是UUID字段，则加入keyword(判断精度为32的字符串为UUID字段)
                if (property.getField().getPrecision() != 32) {
                    isKeyWord = true;
                }
            }
            case CHAR, NCHAR, VARCHAR, NVARCHAR, LONGVARCHAR -> {
                clazz = String.class;
                jsType = "string";
                isKeyWord = true;
            }
            case DATE -> {
                clazz = LocalDate.class;
                jsType = "string";
            }
            case TIME -> {
                clazz = LocalTime.class;
                jsType = "string";
            }
            case TIMESTAMP -> {
                clazz = LocalDateTime.class;
                jsType = "string";
            }
            default -> throw new IllegalArgumentException("not support sql type: " + fieldType);
        }
        if (property.getRemark().contains("@密钥")) {
            property.setIsKey(true);
        }
        if (property.getIsKey() || property.getRemark().contains("@非关键字")) {
            isKeyWord = false;
        }
        property.setClassName(clazz.getName());
        property.setClassSimpleName(clazz.getSimpleName());
        property.setJsType(jsType);
        property.setIsKeyWord(isKeyWord);
    }
}
