package rebue.wheel.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import rebue.wheel.core.file.FileSearcher;
import rebue.wheel.core.file.FileUtils;

import java.io.File;
import java.util.Objects;

@Slf4j
public class DroolsUtils {
    private static final KieServices  kieServices = KieServices.Factory.get();
    private static       KieContainer kieContainer;

    static {
        // 初始化时创建容器
        newKieContainer();

        // 监听drools目录是否有文件变化，如果有就重新创建新的容器
        watchDroolsDir();
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
     * 创建新的会话
     *
     * @param kSessionName 会话名
     *                     在kmodule.xml文件中的ksession节点定义
     *                     在kmodule.xml文件中定义必须是唯一的，不能有重名
     * @return 会话
     */
    public static KieSession newKieSession(String kSessionName) {
        return kieContainer.newKieSession(kSessionName);
    }


}
