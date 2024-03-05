package rebue.wheel.core.drools;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.kie.api.runtime.KieContainer;
import rebue.wheel.core.file.FileUtils;

import java.io.File;
import java.nio.file.Path;

import static rebue.wheel.core.drools.DroolsUtils.newKieContainer;
import static rebue.wheel.core.drools.DroolsUtils.readRuleFiles;

@Slf4j
public class DroolsWatcher {

    private static final FileAlterationMonitor monitor = new FileAlterationMonitor(5 * 1000);

    private static       KieContainer kieContainer;
    //    private static final Path         configFileDirPath;
    private static final Path         ruleFileDirPath;

    static {
        log.info("初始化drools");
        String classesPath = FileUtils.getClassesPath();
        ruleFileDirPath = Path.of(classesPath, "drools", "rule");
        // 初始化时创建容器
        kieContainer = newKieContainer(null, readRuleFiles(ruleFileDirPath));
        // 监听drools目录是否有文件变化，如果有就重新创建新的容器
        watchDroolsDir();
    }

    /**
     * 初始化方法
     * 通过调用此方法，提前激活此监视器
     */
    public static void init() {

    }

    /**
     * 监听drools目录是否有文件变化，如果有就重新创建新的容器
     */
    @SneakyThrows
    private static void watchDroolsDir() {
        FileAlterationObserver observer = new FileAlterationObserver(ruleFileDirPath.toFile());
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                log.info("Drools目录下有新建的文件: {}", file);
                kieContainer = newKieContainer(null, readRuleFiles(ruleFileDirPath));
            }

            @Override
            public void onFileDelete(File file) {
                log.info("Drools目录下有文件被删除: {}", file);
                kieContainer = newKieContainer(null, readRuleFiles(ruleFileDirPath));
            }

            @Override
            public void onFileChange(File file) {
                log.info("Drools目录下有文件发生改变: {}", file);
                kieContainer = newKieContainer(null, readRuleFiles(ruleFileDirPath));
            }
        };
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
    }

    /**
     * 触发规则
     *
     * @param kieSessionName  会话名称
     *                        在kmodule.xml文件中的ksession节点定义
     *                        在kmodule.xml文件中定义必须是唯一的，不能有重名
     *                        如果为null则使用默认会话配置来创建会话(属性default设为true的会话)
     * @param agendaGroupName 议程分组
     * @param fact            要传递给规则执行的参数
     * @return 触发执行的规则数
     */
    public static int fireRules(String kieSessionName, String agendaGroupName, Object fact) {
        return DroolsUtils.fireRules(kieContainer, kieSessionName, agendaGroupName, fact);
    }

}
