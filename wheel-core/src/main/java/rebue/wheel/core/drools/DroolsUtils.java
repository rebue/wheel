package rebue.wheel.core.drools;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaGroup;
import rebue.wheel.core.file.FileSearcher;
import rebue.wheel.core.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class DroolsUtils {
    /**
     * 读取规则文件到Map列表中
     *
     * @param ruleFileDirPath 规则文件所属目录的路径
     * @return 规则文件内容Map列表
     */
    @SneakyThrows
    public static Map<String, String> readRuleFiles(Path ruleFileDirPath) {
        File ruleDir = ruleFileDirPath.toFile();
        if (!ruleDir.exists())
            return null;

        Map<String, String> ruleFiles = new LinkedHashMap<>();
        FileSearcher.searchFiles(ruleDir, ".*\\.drl", file -> {
            try {
                ruleFiles.put(ruleFileDirPath.relativize(file.toPath()).toString(), FileUtils.readToString(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return ruleFiles;
    }

    /**
     * 创建新的容器
     *
     * @param configFileDirPath 配置文件(kmodule.xml)所在目录的路径
     *                          为null时自动生成配置文件
     * @param rules             规则文件内容Map列表
     * @return 返回新创建的容器
     */
    @SneakyThrows
    public static KieContainer newKieContainer(Path configFileDirPath, Map<String, String> rules) {
        KieServices   kieServices   = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        log.info("加载kmodule文件");
        Path kmodulePath;
        if (configFileDirPath != null) {
            kmodulePath = configFileDirPath.resolve("kmodule.xml");
            kieFileSystem.writeKModuleXML(FileUtils.readToString(kmodulePath.toString()));
        } else {
            // XXX kmodule.xml文件的路径必须是 src/main/resources/META-INF/kmodule.xml
            // 可以用 .writeKModuleXML(""" 替换
            kieFileSystem.write("src/main/resources/META-INF/kmodule.xml", """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <kmodule xmlns="http://www.drools.org/xsd/kmodule">
                        <kbase default="true">
                            <ksession name="default" default="true" />
                        </kbase>
                    </kmodule>
                    """);
        }
        for (Map.Entry<String, String> rule : rules.entrySet()) {
            // XXX 规则文件路径必须放是 src/main/resources/
            kieFileSystem.write("src/main/resources/" + rule.getKey(), rule.getValue());
        }
        kieServices.newKieBuilder(kieFileSystem).buildAll();
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    /**
     * 触发规则
     *
     * @param kieContainer    容器
     * @param kieSessionName  会话名称
     *                        在kmodule.xml文件中的ksession节点定义
     *                        在kmodule.xml文件中定义必须是唯一的，不能有重名
     *                        如果为null则使用默认会话配置来创建会话(属性default设为true的会话)
     * @param agendaGroupName 议程分组
     * @param fact            要传递给规则执行的参数
     * @return 触发执行的规则数
     */
    public static int fireRules(KieContainer kieContainer, String kieSessionName, String agendaGroupName, Object fact) {
        log.debug("fireRules: kieSessionName-{} agendaGroupName-{}", kieSessionName, agendaGroupName);
        // 创建会话
        KieSession kieSession;
        if (StringUtils.isBlank(kieSessionName)) {
            kieSession = kieContainer.newKieSession();
        } else {
            kieSession = kieContainer.newKieSession(kieSessionName);
        }
        try {
            // 是否激活议程分组
            if (StringUtils.isNotBlank(agendaGroupName)) {
                AgendaGroup agendaGroup = kieSession.getAgenda().getAgendaGroup(agendaGroupName);
                if (agendaGroup == null) {
                    log.warn("找不到议程分组(AgendaGroup)为{}的规则", agendaGroupName);
                    return 0;
                }
                agendaGroup.setFocus();
            }
            // 插入规则执行的参数
            kieSession.insert(fact);
            // 执行
            int firedRulesCount = kieSession.fireAllRules();
            log.info("触发执行了议程分组(AgendaGroup)为{}的规则数为{}", agendaGroupName, firedRulesCount);
            return firedRulesCount;
        } finally {
            kieSession.dispose();
        }
    }

}
