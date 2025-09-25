package rebue.wheel.core.db;

import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class JdbcUtilsTests {
    @Test
    public void test01() throws SQLException {
        String     url              = "jdbc:postgresql://%s:%s/%s";
        String[]   params           = new String[] { "pgsql", "5432", "oss" };
        Properties properties       = new Properties();
        properties.setProperty("user", "oss");
        properties.setProperty("password", "oss");
        String     tableNamePattern = "oss_%";
        JdbcUtils.getDbMeta(JdbcUtils.ConnectParam.builder()
                .url(url).params(params).properties(properties).build(), tableNamePattern);
    }
}
