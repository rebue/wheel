package rebue.wheel.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class DbUtils {

    private static String mssqlUsername;
    private static String mssqlPassword;
    private static String mssqlUrl;

    private static String mysqlUsername;
    private static String mysqlPassword;
    private static String mysqlUrl;

    static {
        try {
            Properties properties = new Properties();
            properties.load(DbUtils.class.getResourceAsStream("/db.properties"));
            mssqlUrl = properties.getProperty("mssql.url");
            mssqlUsername = properties.getProperty("mssql.username");
            mssqlPassword = properties.getProperty("mssql.password");
            mysqlUrl = properties.getProperty("mysql.url");
            mysqlUsername = properties.getProperty("mysql.username");
            mysqlPassword = properties.getProperty("mysql.password");

            // 注册数据库驱动
            String mssqlDriver = properties.getProperty("mssql.driver");
            if (!StringUtils.isBlank(mssqlDriver))
                Class.forName(mssqlDriver);
            String mysqlDriver = properties.getProperty("mysql.driver");
            if (!StringUtils.isBlank(mysqlDriver))
                Class.forName(mysqlDriver);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getMsSqlConn() throws SQLException {
        return DriverManager.getConnection(mssqlUrl, mssqlUsername, mssqlPassword);
    }

    public static Connection getMySqlConn() throws SQLException {
        return DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
    }

    public static String getInsertSql(String tableName, Map<String, Object> fields) {
        String keys = "";
        String values = "";
        for (Entry<String, Object> item : fields.entrySet()) {
            if (item.getValue() != null) {
                if (item.getValue() instanceof String) {
                    String temp = (String) item.getValue();
                    if (StringUtils.isBlank(temp)) {
                        continue;
                    }
                    values += "'" + temp.trim() + "',";
                } else {
                    values += item.getValue() + ",";
                }
                keys += item.getKey() + ",";
            }
        }
        // 去掉最后一个逗号
        keys = StringUtils.left(keys, keys.length() - 1);
        values = StringUtils.left(values, values.length() - 1);
        // 拼接SQL语句
        return "insert into " + tableName + " (" + keys + ")values(" + values + ")";
    }

    public static String getUpdateSql(String tableName, Map<String, Object> fields) {
        String sSet = "";
        for (Entry<String, Object> item : fields.entrySet()) {
            String key = item.getKey();
            String value = "";
            if (item.getValue() == null) {
                value = "null";
            } else {
                if (item.getValue() instanceof String) {
                    value += "'" + item.getValue() + "'";
                } else {
                    value += item.getValue();
                }
            }
            sSet += key + "=" + value + ",";
        }
        // 去掉最后一个逗号
        sSet = StringUtils.left(sSet, sSet.length() - 1);
        // 拼接SQL语句
        return "update " + tableName + " set " + sSet;
    }

}
