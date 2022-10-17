package rebue.wheel.vertx.util;

import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import rebue.wheel.core.idworker.IdWorker3;

import java.util.List;

@Slf4j
public class IdWorkerUtils {

    public static IdWorker3 create3(Object thisObj, JsonObject config, CuratorFramework zkClient) {
        log.info("IdWorkerUtils.create3");
        final String options = config.getString("idworker");
        if (StringUtils.isBlank(options)) {
            log.info("默认为单机模式创建IdWorker3");
            return new IdWorker3();
        }

        String key = options.trim();

        // 解析key
        // 如果"nodeId"开头，且值为0~31，则是指定nodeId;
        // 如果"auto"或以"auto:"开头，则由zookeeper自动分配nodeId，"auto:"后面跟nodeIdBits的值
        if (key.startsWith("nodeId:")) {
            log.info("根据配置指定nodeId的方式创建IdWorker3");
            key = key.replaceFirst("nodeId:", "").trim();
            final int nodeId = Integer.parseInt(key);
            return new IdWorker3(nodeId);
        }

        log.info("从zookeeper获取nodeId的方式创建IdWorker3");
        Integer nodeIdBits;
        if (key.equals("auto")) {
            nodeIdBits = 5;
        } else if (key.startsWith("auto:")) {
            key = key.replaceFirst("auto:", "").trim();
            nodeIdBits = Integer.getInteger(key);
        } else {
            throw new RuntimeException("idworker值应该是整数、”auto“或以”auto:“开头的字符串");
        }

        final String packageName = thisObj.getClass().getPackage().getName();
        final String className = thisObj.getClass().getSimpleName();
        final String reducePackageName = packageName.replaceAll(".svc.impl.ex", "").replaceAll(".svc.impl", "");
        final String reduceClassName = className.replaceAll("SvcImpl", "");
        final String zkNodePath = "/idworker/" + reducePackageName + "/" + reduceClassName;

        Integer nodeId;
        try {
            LOOP:
            while (true) {
                log.debug("准备连接zookeeper获取路径全名: zkNodePath-{}", zkNodePath);
                final String zkNodeFullName;
                zkNodeFullName = zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath(zkNodePath + "/id_");
                log.debug("连接zookeeper获取到的路径全名: {}", zkNodeFullName);
                final String zkNodeSimpleName = zkNodeFullName.substring(zkNodeFullName.lastIndexOf("/") + 1);
                nodeId = getNodeId(zkNodeFullName, nodeIdBits);
                final List<String> zkNodes = zkClient.getChildren().forPath(zkNodePath);
                for (final String zkNodeSimpleNameTemp : zkNodes) {
                    if (zkNodeSimpleNameTemp.equals(zkNodeSimpleName)) {
                        continue;
                    }
                    final Integer nodeIdTemp = getNodeId(zkNodeSimpleNameTemp, nodeIdBits);
                    if (nodeIdTemp.equals(nodeId)) {
                        zkClient.delete().forPath(zkNodeFullName);
                        continue LOOP;
                    }
                }
                break;
            }
        } catch (Exception e) {
            log.error("连接zookeeper进行操作出现异常", e);
            throw new RuntimeException(e);
        }

        log.info("生成的nodeId为: {}", nodeId);
        return new IdWorker3(nodeId);
    }

    private static Integer getNodeId(final String path, final int nodeIdBits) {
        return Integer.parseInt(StringUtils.right(path, 10)) % (2 << nodeIdBits - 1);
    }

}
