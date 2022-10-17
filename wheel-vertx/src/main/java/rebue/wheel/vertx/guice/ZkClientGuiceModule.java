package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import rebue.wheel.vertx.config.ZkClientProperties;

import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
public class ZkClientGuiceModule extends AbstractModule {

    @Singleton
    @Provides
    CuratorFramework getZkClient(@Named("config") final JsonObject config) {
        log.info("ZkClientGuiceModule.getZkClient");

        JsonObject curatorPropertiesJsonObject = config.getJsonObject("zkClient");

        ZkClientProperties zkClientProperties = curatorPropertiesJsonObject == null
                ? new ZkClientProperties()
                : curatorPropertiesJsonObject.mapTo(ZkClientProperties.class);

        log.debug("配置zkClient的失败重试策略");
        ZkClientProperties.RetryPolicyProperties retryPolicyProperties = zkClientProperties.getRetryPolicy();
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(
                retryPolicyProperties.getBaseSleepTimeMs(),
                retryPolicyProperties.getMaxRetries(),
                retryPolicyProperties.getMaxSleepMs());

        try {
            log.debug("根据配置创建zkClient");
            CuratorFramework zkClient = CuratorFrameworkFactory.builder()
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
        } catch (Exception e) {
            log.error("获取zkClient出现异常", e);
            throw new RuntimeException(e);
        }
    }

}
