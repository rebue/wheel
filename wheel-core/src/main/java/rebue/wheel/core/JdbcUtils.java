package rebue.wheel.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUtils {

    private static final String ORACLE_JDBC_DRIVER    = "oracle.jdbc.driver.OracleDriver";
    private static final String MYSQL_JDBC_DRIVER     = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_JDBC_URL_PREFIX = "jdbc:mysql://";
    // serverTimezone=Shanghai&?
    private static final String MYSQL_JDBC_URL_SUFFIX = "?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true";
    private static Connection   connection            = null;
    private static String       mysqlUsername         = "etl";
    private static String       mysqlPassword         = "etl";
    private static String       mysqlUrl              = "jdbc:mysql://127.0.0.1:3306/etl?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true";
    private static String       oracleUsername        = "magic";
    private static String       oraclePassword        = "maiyue123";
    private static String       oracleUrl             = "jdbc:oracle:thin:@116.63.72.107:1521/dbsrv2";

    /**
     * 
     * @param ip           数据库所在IP
     * @param port         端口
     * @param databaseName 数据库名称
     * 
     * @return 返回链接数据库的url
     */
    public static String getUrl(String ip, int port, String databaseName, String sqlTpye) {
        if (sqlTpye.equals(1)) {
            return getMysqlUrl(ip, port, databaseName);
        }
        else {
            return getOracleUrl(ip, port, databaseName);
        }
    }

    public static String getMysqlUrl(String ip, int port, String databaseName) {
        return MYSQL_JDBC_URL_PREFIX + ip + ":" + port + "/" + databaseName + MYSQL_JDBC_URL_SUFFIX;
    }

    // FIXME 修改oracle
    public static String getOracleUrl(String ip, int port, String databaseName) {
        return MYSQL_JDBC_URL_PREFIX + ip + ":" + port + "/" + databaseName + MYSQL_JDBC_URL_SUFFIX;
    }

    public static Connection getConnection(String url, String userName, String password) {
        try {
            Class.forName(MYSQL_JDBC_DRIVER);
            connection = DriverManager.getConnection(url, userName, password);
            return connection;
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
            ResultSet tables = metaData.getTables(null, null, null, new String[] { "TABLE"
            });
            while (tables.next()) {
                tables.getString(3);
                list.add(tables.getString(3));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取表的列名字段
     * 
     * @param url       数据库连接url
     * @param userName  用户名
     * @param password  密码
     * @param TableName 表的名称
     * 
     * @return 返回表的列名字段
     */
    public static List<String> getColumnsByTableName(String url, String userName, String password, String TableName) {
        Connection   connection = getConnection(url, userName, password);
        List<String> list       = new ArrayList<String>();
        // 根据表名 拼接成SQL语句 查询到某个表的所有列
        try {
            String            sql  = "SELECT  *  FROM " + TableName + ";";
            PreparedStatement prep = connection.prepareStatement(sql);
            ResultSet         set  = prep.executeQuery(sql);
            ResultSetMetaData data = set.getMetaData();
            // 迭代取到所有列信息
            for (int i = 1; i <= data.getColumnCount(); i++) {
                // 获得指定列的列名
                list.add(data.getColumnName(i));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
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
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return boo;
    }

    public static void main(String[] args) {
        List<String> tables = new ArrayList<String>();
        try {
            Class.forName(ORACLE_JDBC_DRIVER);
            connection = DriverManager.getConnection(oracleUrl, oracleUsername, oraclePassword);
            tables     = getTables(connection);
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(tables);
    }
}
