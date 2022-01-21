package rebue.wheel.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rebue.wheel.api.exception.RuntimeExceptionX;

/**
 * 获取数据库表以及表的字段名称
 * 
 * @author yuanman
 *
 */
public class JdbcUtils {
    private static final String SQL                    = "SELECT * FROM ";// 数据库操作
    private static final String ORACLE_JDBC_DRIVER     = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_JDBC_URL_PREFIX = "jdbc:oracle:thin:@";
    private static final String ORACLE_JDBC_URL_SUFFIX = "";
    private static final String MYSQL_JDBC_DRIVER      = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_JDBC_URL_PREFIX  = "jdbc:mysql://";
    // serverTimezone=Shanghai&?
    private static final String MYSQL_JDBC_URL_SUFFIX  = "?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true";
    private static Connection   connection             = null;
    @SuppressWarnings("unused")
    private static String       mysqlUsername          = "etl";
    @SuppressWarnings("unused")
    private static String       mysqlPassword          = "etl";
    @SuppressWarnings("unused")
    private static String       mysqlUrl               = "jdbc:mysql://127.0.0.1:3306/etl?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true";

    /**
     * 
     * @param ip           数据库所在IP
     * @param port         端口
     * @param databaseName 数据库名称
     * 
     * @return 返回链接数据库的url
     */
    public static String getUrl(String ip, int port, String databaseName, String dbTpye) {
        switch (dbTpye) {
        case "mysql":
            return getMysqlUrl(ip, port, databaseName);
        case "oracle":
            return getOracleUrl(ip, port, databaseName);
        default:
            throw new RuntimeExceptionX("不支持此类型数据库: " + dbTpye);
        }
    }

    public static String getMysqlUrl(String ip, int port, String databaseName) {
        return MYSQL_JDBC_URL_PREFIX + ip + ":" + port + "/" + databaseName + MYSQL_JDBC_URL_SUFFIX;
    }

    public static String getOracleUrl(String ip, int port, String databaseName) {
        return ORACLE_JDBC_URL_PREFIX + ip + ":" + port + "/" + databaseName + ORACLE_JDBC_URL_SUFFIX;
    }

    public static Connection getConnection(String url, String userName, String password) {
        connection = null;
        try {
            int indexOf = url.indexOf(MYSQL_JDBC_URL_PREFIX);
            if (indexOf != -1) {
                Class.forName(MYSQL_JDBC_DRIVER);
                connection = DriverManager.getConnection(url, userName, password);
                return connection;
            }
            else {
                Class.forName(ORACLE_JDBC_DRIVER);
                connection = DriverManager.getConnection(url, userName, password);
                return connection;
            }
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 获取表名
     * 
     * @param url      数据库连接url
     * @param userName 用户名
     * @param password 密码
     * 
     * @return 返回表名
     */
    public static List<String> getTables(String url, String userName, String password) {
        Connection   connection = getConnection(url, userName, password);
        List<String> list       = getTables(connection);
        return list;
    }

    /**
     * 获取表名
     * 
     * @param connection sql连接会话
     * 
     * @return 返回表名
     */
    public static List<String> getTables(Connection connection) {
        DatabaseMetaData metaData = null;
        List<String>     list     = new ArrayList<String>();
        try {
            metaData = connection.getMetaData();
            String    userName = metaData.supportsSchemasInTableDefinitions() ? metaData.getUserName() : null;
            ResultSet tables   = metaData.getTables(null, userName, null, new String[] { "TABLE"
            });
            while (tables.next()) {
                tables.getString(3);
                list.add(tables.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    /**
     * 获取表的列名字段
     * 
     * @param url       数据库连接url
     * @param userName  用户名
     * @param password  密码
     * @param tableName 表的名称
     * 
     * @return 返回Map<字段名, 字段类型>
     */
    public static Map<String, String> getColumnsByTableName(String url, String userName, String password, String tableName) {
        Connection          connection = getConnection(url, userName, password);
        // List<String> list = new ArrayList<String>();
        // List<String> columnTypes = new ArrayList<String>();
        Map<String, String> map        = new HashMap<String, String>();
        // 根据表名 拼接成SQL语句 查询到某个表的所有列
        PreparedStatement   prep       = null;
        try {
            String sql = SQL + tableName;
            prep = connection.prepareStatement(sql);
            ResultSet         set  = prep.executeQuery(sql);
            ResultSetMetaData data = set.getMetaData();
            // 迭代取到所有列信息
            for (int i = 1; i <= data.getColumnCount(); i++) {
                // 获得指定列的列名为key，类型为value
                map.put(data.getColumnName(i), data.getColumnTypeName(i));
                // list.add(data.getColumnName(i));
                // columnTypes.add(data.getColumnTypeName(i));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (prep != null) {
                try {
                    prep.close();
                    closeConnection(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 获取表中所有字段类型
     * 
     * @param tableName
     * 
     * @return
     */
    public static List<String> getColumnTypes(String url, String userName, String password, String tableName) {
        List<String>      columnTypes = new ArrayList<>();
        // 与数据库的连接
        Connection        conn        = getConnection(url, userName, password);
        PreparedStatement prep        = null;
        String            tableSql    = SQL + tableName;
        try {
            prep = conn.prepareStatement(tableSql);
            // 结果集元数据
            ResultSetMetaData rsmd = prep.getMetaData();
            // 表列数
            int               size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (prep != null) {
                try {
                    prep.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnTypes;
    }

    /**
     * 关闭数据库连接
     * 
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试连接
     * 
     * @param userName  用户名
     * @param password  密码
     * @param TableName 表的名称
     * 
     * @return Boolean
     */
    public static Boolean getTestConnection(String url, String userName, String userPswd) {
        Connection connection = getConnection(url, userName, userPswd);
        Boolean    boo        = connection == null ? false : true;
        try {
            if (boo) {
                connection.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return boo;
    }

    // public static void main(String[] args) {
    // List<String> tables = new ArrayList<String>();
    // Map<String, String> columnsByTableNameMap = new HashMap<String, String>();
    // List<String> columnTypes = new ArrayList<String>();
    // try {
    // Class.forName(MYSQL_JDBC_DRIVER);
    // connection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
    // tables = getTables(connection);
    // columnsByTableNameMap = getColumnsByTableName(mysqlUrl, mysqlUsername, mysqlPassword, tables.get(0));
    // columnTypes = getColumnTypes(mysqlUrl, mysqlUsername, mysqlPassword, tables.get(0));
    // // Class.forName(ORACLE_JDBC_DRIVER);
    // // connection = DriverManager.getConnection(oracleUrl, oracleUsername, oraclePassword);
    // // tables = getTables(connection);
    // // columnsByTableName = getColumnsByTableName(oracleUrl, oracleUsername, oraclePassword, tables.get(0));
    //
    // } catch (SQLException | ClassNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // System.out.println("数据库表名称" + tables);
    // System.out.println(tables.get(0) + "表的字段列名:" + columnsByTableNameMap);
    // System.out.println(tables.get(0) + "表的字段类型:" + columnTypes);
    // }
}
