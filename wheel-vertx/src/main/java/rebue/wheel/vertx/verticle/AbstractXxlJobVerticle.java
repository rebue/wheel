package rebue.wheel.vertx.verticle;

import com.xxl.job.core.executor.impl.XxlJobSimpleExecutor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import rebue.wheel.vertx.config.XxlJobProperties;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class AbstractXxlJobVerticle extends AbstractVerticle {

    @Inject
    @Named("mainId")
    private String mainId;

    private MessageConsumer<Void> startConsumer;

    private XxlJobSimpleExecutor xxlJobExecutor = null;

    @Override
    public void start() {
        log.info("XxlJobVerticle start preparing");

        final XxlJobProperties xxlJobProperties = config().mapTo(XxlJobProperties.class);
        log.debug("xxlJobProperties: {}", xxlJobProperties);

        setExecutorProperties(xxlJobProperties);

        log.info("XxlJobVerticle配置消费EventBus事件-MainVerticle部署成功事件");
        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("XxlJobVerticle end preparing");
    }

    /**
     * 设置执行器属性
     *
     * @param xxlJobProperties 属性的配置
     */
    private void setExecutorProperties(final XxlJobProperties xxlJobProperties) {
        xxlJobExecutor = new XxlJobSimpleExecutor();

        xxlJobExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        final String accessToken = xxlJobProperties.getAdmin().getAccessToken();
        if (StringUtils.isNotBlank(accessToken)) {
            xxlJobExecutor.setAccessToken(accessToken);
        }

        xxlJobExecutor.setAppname(xxlJobProperties.getExecutor().getAppName());

        final String address = xxlJobProperties.getExecutor().getAddress();
        if (StringUtils.isNotBlank(address)) {
            xxlJobExecutor.setAddress(address);
        }

        final String ip = xxlJobProperties.getExecutor().getIp();
        if (StringUtils.isNotBlank(ip)) {
            xxlJobExecutor.setIp(ip);
        }

        final Integer port = xxlJobProperties.getExecutor().getPort();
        if (port != null) {
            xxlJobExecutor.setPort(port);
        }

        final String logPath = xxlJobProperties.getExecutor().getLogPath();
        if (logPath != null) {
            xxlJobExecutor.setLogPath(logPath);
        }

        final Integer logRetentionDays = xxlJobProperties.getExecutor().getLogRetentionDays();
        if (logRetentionDays != null) {
            xxlJobExecutor.setLogRetentionDays(logRetentionDays);
        }

        final List<Object> jobs = new LinkedList<>();
        addJob(jobs);
        xxlJobExecutor.setXxlJobBeanList(jobs);
    }

    protected abstract void addJob(List<Object> jobs);

    @Override
    public void stop() {
        log.info("XxlJobVerticle stop");
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }

    private void handleStart(final Message<Void> message) {
        log.info("XxlJobVerticle start");
        this.startConsumer.unregister();
        xxlJobExecutor.start();
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("XxlJobVerticle start success");
        } else {
            log.error("XxlJobVerticle start fail", res.cause());
        }
    }

}
