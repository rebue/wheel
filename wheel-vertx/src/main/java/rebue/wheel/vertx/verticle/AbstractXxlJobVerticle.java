package rebue.wheel.vertx.verticle;

import com.xxl.job.core.executor.impl.XxlJobSimpleExecutor;
import com.xxl.job.core.handler.IJobHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.XxlJobProperties;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class AbstractXxlJobVerticle extends AbstractVerticle {

    @Inject
    @Named("mainId")
    private String mainId;

    private MessageConsumer<Void> startConsumer;

    @Inject
    private XxlJobSimpleExecutor xxlJobExecutor;

    @Override
    public void start() throws Exception {
        log.info("XxlJobVerticle start");

        final XxlJobProperties xxlJobProperties = config().mapTo(XxlJobProperties.class);

        xxlJobExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        xxlJobExecutor.setAccessToken(xxlJobProperties.getAdmin().getAccessToken());
        xxlJobExecutor.setAppname(xxlJobProperties.getExecutor().getAppName());
        xxlJobExecutor.setAddress(xxlJobProperties.getExecutor().getAddress());
        xxlJobExecutor.setIp(xxlJobProperties.getExecutor().getIp());
        xxlJobExecutor.setPort(xxlJobProperties.getExecutor().getPort());
        xxlJobExecutor.setLogRetentionDays(xxlJobProperties.getExecutor().getLogRetentionDays());
        List<IJobHandler> jobs = new LinkedList<>();
        addJob(jobs);
        xxlJobExecutor.setXxlJobBeanList(Collections.unmodifiableList(jobs));


        log.info("配置消费EventBus事件-MainVerticle部署成功事件");
        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is " + address);
        this.startConsumer = this.vertx.eventBus()
                .consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("XxlJobVerticle Started");
    }

    protected abstract void addJob(List<IJobHandler> jobs);

    @Override
    public void stop() throws Exception {
        log.info("XxlJobVerticle stop");
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }

    private void handleStart(final Message<Void> message) {
        this.startConsumer.unregister();
        xxlJobExecutor.start();
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("Event Bus register success: consumer.start");
        } else {
            log.error("Event Bus register fail: consumer.start", res.cause());
        }
    }

}
