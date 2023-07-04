package rebue.wheel.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    public static Connection getConnection(ConnectParam connectParam) throws SQLException {
        return DriverManager.getConnection(
                String.format(connectParam.getUrl(), (Object[]) connectParam.getParams()),
                connectParam.getProperties());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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

}
