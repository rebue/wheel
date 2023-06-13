package rebue.wheel.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import rebue.wheel.core.fact.RequestFact;
import rebue.wheel.core.file.FileUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;


@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class DroolsTests {
    private static final KieServices  kieServices = KieServices.Factory.get();
    private static       KieContainer kieContainer;

    @RepeatedTest(1)
    @Execution(ExecutionMode.CONCURRENT)
    public void test01() throws IOException, InterruptedException {
        String              random = System.nanoTime() + UUID.randomUUID().toString().replaceAll("-", "");
        Map<String, String> body   = execute(random);
        Assertions.assertEquals("{0=" + random + ", c=CCCCC, d=DDDDD, f=FFF, g=GGG, h=HHHHH, i=IIIII, j=CCCCC, k=EEE, l=EEE}", body.toString());

        String       drlPath    = FileUtils.getClassesPath() + "drools/rules/DefaultRules.drl";
        StringJoiner drlContent = new StringJoiner("\n");
        try (BufferedReader in = new BufferedReader(new FileReader(drlPath))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("IIIII")) {
                    line = line.replaceAll("IIIII", "IIIII IIIII IIIII");
                }
                if (line.contains("ABC OK!")) {
                    line = line.replaceAll("ABC OK!", "ABC OK!OK!OK!");
                }
                drlContent.add(line);
            }
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(drlPath))) {
            out.write(drlContent.toString());
        }
        Thread.sleep(10000);

        random = System.nanoTime() + UUID.randomUUID().toString().replaceAll("-", "");
        body = execute(random);
        Assertions.assertEquals("{0=" + random + ", c=CCCCC, d=DDDDD, f=FFF, g=GGG, h=HHHHH, i=IIIII IIIII IIIII, j=CCCCC, k=EEE, l=EEE}", body.toString());

        drlContent = new StringJoiner("\n");
        try (BufferedReader in = new BufferedReader(new FileReader(drlPath))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("IIIII IIIII IIIII")) {
                    line = line.replaceAll("IIIII IIIII IIIII", "IIIII");
                }
                if (line.contains("ABC OK!OK!OK!")) {
                    line = line.replaceAll("ABC OK!OK!OK!", "ABC OK!");
                }
                drlContent.add(line);
            }
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(drlPath))) {
            out.write(drlContent.toString());
        }
        Thread.sleep(10000);

        random = System.nanoTime() + UUID.randomUUID().toString().replaceAll("-", "");
        body = execute(random);
        Assertions.assertEquals("{0=" + random + ", c=CCCCC, d=DDDDD, f=FFF, g=GGG, h=HHHHH, i=IIIII, j=CCCCC, k=EEE, l=EEE}", body.toString());
    }

    public Map<String, String> execute(String random) throws IOException {
        DroolsUtils.newKieContainer();
        KieSession kieSession = DroolsUtils.newKieSession("test01");   // kSessionName在kmodule.xml文件中定义

        Map<String, String> body = new LinkedHashMap<>();
        body.put("0", random);
        body.put("a", "AAA");
        body.put("b", "BBB");
        body.put("c", "CCC");
        body.put("d", "DDD");
        body.put("e", "EEE");
        body.put("f", "FFF");
        body.put("g", "GGG");
        kieSession.insert(RequestFact.builder()
                .uri("/abc")
                .body(body)
                .build());
        int rulesCount = kieSession.fireAllRules();
        log.info("result: {}", body);
        Assertions.assertEquals(1, rulesCount);

        kieSession.dispose();
        return body;
    }

}
