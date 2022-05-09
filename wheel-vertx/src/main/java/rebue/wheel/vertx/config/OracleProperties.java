package rebue.wheel.vertx.config;

import io.vertx.oracleclient.OracleConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import lombok.Data;

@Data
public class OracleProperties {

    private OracleConnectOptions connect;

    private PoolOptions          pool;

}
