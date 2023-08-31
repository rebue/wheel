package rebue.wheel.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaGroup;
import org.kie.internal.io.ResourceFactory;
import rebue.wheel.core.file.FileSearcher;
import rebue.wheel.core.file.FileUtils;

import java.io.File;

@Slf4j
public class DroolsUtils {
    private static final KieServices  kieServices = KieServices.Factory.get();
    private static       KieContainer kieContainer;

    static {
        log.info("初始化drools");
        // 初始化时创建容器
        newKieContainer();
        // 监听drools目录是否有文件变化，如果有就重新创建新的容器
        watchDroolsDir();
    }

    public static void init() {

    }

    /**
     * 创建新的容器
     */
    @SneakyThrows
    private static void newKieContainer() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        log.info("加载kmodule文件");
        String kmodulePath = "drools/kmodule.xml";
        kieFileSystem.writeKModuleXML(FileUtils.readToString(FileUtils.getClassesPath() + kmodulePath));
        File drlDir = new File(FileUtils.getClassesPath() + "drools/rules/");
        FileSearcher.searchFiles(drlDir, ".*\\.drl", file -> {
            log.info("加载drl文件: {}", file.getPath());
            kieFileSystem.write(ResourceFactory.newFileResource(file));
        });
        kieServices.newKieBuilder(kieFileSystem).buildAll();
        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    /**
     * 监听drools目录是否有文件变化，如果有就重新创建新的容器
     */
    @SneakyThrows
    private static void watchDroolsDir() {
        FileAlterationObserver observer = new FileAlterationObserver(FileUtils.getClassesPath() + "drools/");
        FileAlterationMonitor  monitor  = new FileAlterationMonitor(5 * 1000);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                log.info("Drools目录下有新建的文件: {}", file);
                newKieContainer();
            }

            @Override
            public void onFileDelete(File file) {
                log.info("Drools目录下有文件被删除: {}", file);
                newKieContainer();
            }

            @Override
            public void onFileChange(File file) {
                log.info("Drools目录下有文件发生改变: {}", file);
                newKieContainer();
            }
        };
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
    }

    /**
     * 触发规则
     *
     * @param kSessionName    会话名称
     *                        在kmodule.xml文件中的ksession节点定义
     *                        在kmodule.xml文件中定义必须是唯一的，不能有重名
     * @param agendaGroupName 议程分组
     * @param fact            要传递给规则执行的参数
     * @return 触发执行的规则数
     */
    public static int fireRules(String kSessionName, String agendaGroupName, Object fact) {
        log.debug("fireRules: kSessionName-{} agendaGroupName-{} fact-{}", kSessionName, agendaGroupName, fact);
        // 执行规则引擎自定义绑定变量
        KieSession kieSession = kieContainer.newKieSession(kSessionName);
        try {
            if (StringUtils.isNotBlank(agendaGroupName)) {
                AgendaGroup agendaGroup = kieSession.getAgenda().getAgendaGroup(agendaGroupName);
                if (agendaGroup == null) {
                    log.warn("找不到议程分组(AgendaGroup)为{}的规则", agendaGroupName);
                    return 0;
                }
                agendaGroup.setFocus();
            }
            kieSession.insert(fact);
            int firedRulesCount = kieSession.fireAllRules();
            log.info("触发执行了议程分组(AgendaGroup)为{}的规则数为{}", agendaGroupName, firedRulesCount);
            return firedRulesCount;
        } finally {
            kieSession.dispose();
        }
    }

}
