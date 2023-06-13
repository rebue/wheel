package rebue.wheel.core;

import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import rebue.wheel.core.file.FileUtils;

import java.io.IOException;

public class DroolsUtils {
    private static final KieServices  kieServices = KieServices.Factory.get();
    private static       KieContainer kieContainer;

    public static void newKieContainer() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        String        kmodulePath   = "drools/kmodule.xml";
        String        drlPath       = "drools/rules/DefaultRules.drl";
        kieFileSystem.writeKModuleXML(FileUtils.readToString(FileUtils.getClassesPath() + kmodulePath));
        kieFileSystem.write(ResourceFactory.newClassPathResource(drlPath));
        kieServices.newKieBuilder(kieFileSystem).buildAll();
        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    public static KieSession newKieSession(String kSessionName) {
        return kieContainer.newKieSession(kSessionName);
    }


}
