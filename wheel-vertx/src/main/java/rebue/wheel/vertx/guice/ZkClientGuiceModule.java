package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import rebue.wheel.vertx.config.ZkClientProperties;

@Slf4j
public class ZkClientGuiceModule extends AbstractModule {

    public ZkClientGuiceModule() {
        log.info("new ZkClientGuiceModule");
    }

    @Singleton
    @Provides
    CuratorFramework getZkClient(@Named("config") final JsonObject config) {
        log.info("ZkClientGuiceModule.getZkClient");

        final JsonObject zkClientPropertiesJsonObject = config.getJsonObject("zkClient");

        final ZkClientProperties zkClientProperties = zkClientPropertiesJsonObject == null
                ? new ZkClientProperties()
                : zkClientPropertiesJsonObject.mapTo(ZkClientProperties.class);

        log.debug("配置zkClient的失败重试策略");
        final ZkClientProperties.RetryPolicyProperties retryPolicyProperties = zkClientProperties.getRetryPolicy();
        final ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(
                retryPolicyProperties.getBaseSleepTimeMs(),
                retryPolicyProperties.getMaxRetries(),
                retryPolicyProperties.getMaxSleepMs());

        try {
            log.debug("根据配置创建zkClient");
            final CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zkClientProperties.getConnectString())
                    .sessionTimeoutMs(zkClientProperties.getSessionTimeoutMs())
                    .connectionTimeoutMs(zkClientProperties.getConnectionTimeoutMs())
                    .retryPolicy(retryPolicy)
                    .build();

            log.debug("启动zkClient");
            zkClient.start();
            zkClient.blockUntilConnected();
            log.debug("zkClient启动完成");
            return zkClient;
        } catch (final Exception e) {
            log.error("获取zkClient出现异常", e);
            throw new RuntimeException(e);
        }
    }

}
