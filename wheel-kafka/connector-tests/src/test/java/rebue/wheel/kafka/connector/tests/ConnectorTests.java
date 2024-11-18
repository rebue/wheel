package rebue.wheel.kafka.connector.tests;

import java.util.Objects;

import org.apache.kafka.connect.cli.ConnectStandalone;
import org.junit.jupiter.api.Test;

public class ConnectorTests {

    @Test
    public void testRun() {
        String standalone = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("connect-standalone.properties")).getPath();
        ConnectStandalone.main(new String[] { standalone });
    }

}
