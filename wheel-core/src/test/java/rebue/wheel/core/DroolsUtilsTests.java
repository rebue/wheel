package rebue.wheel.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import rebue.wheel.core.fact.RequestFact;
import rebue.wheel.core.file.FileModifier;
import rebue.wheel.core.file.FileUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class DroolsUtilsTests {
    @RepeatedTest(1)
    @Execution(ExecutionMode.CONCURRENT)
    public void test01() throws IOException, InterruptedException {
        String              random = System.nanoTime() + UUID.randomUUID().toString().replaceAll("-", "");
        Map<String, String> body   = execute(random);
        Assertions.assertEquals("{0=" + random + ", c=CCCCC, d=DDDDD, f=FFF, g=GGG, h=HHHHH, i=IIIII, j=CCCCC, k=EEE, l=EEE}", body.toString());

        String       drlPath      = FileUtils.getClassesPath() + "drools/rules/DefaultRules.drl";
        FileModifier fileModifier = new FileModifier(drlPath);
        fileModifier.modifyLine("IIIII", "IIIII IIIII IIIII");
        fileModifier.modifyLine("ABC OK!", "ABC OK!OK!OK!");
        fileModifier.process();
        Thread.sleep(10000);

        random = System.nanoTime() + UUID.randomUUID().toString().replaceAll("-", "");
        body   = execute(random);
        Assertions.assertEquals("{0=" + random + ", c=CCCCC, d=DDDDD, f=FFF, g=GGG, h=HHHHH, i=IIIII IIIII IIIII, j=CCCCC, k=EEE, l=EEE}", body.toString());

        fileModifier = new FileModifier(drlPath);
        fileModifier.modifyLine("IIIII IIIII IIIII", "IIIII");
        fileModifier.modifyLine("ABC OK!OK!OK!", "ABC OK!");
        fileModifier.process();
        Thread.sleep(10000);

        random = System.nanoTime() + UUID.randomUUID().toString().replaceAll("-", "");
        body   = execute(random);
        Assertions.assertEquals("{0=" + random + ", c=CCCCC, d=DDDDD, f=FFF, g=GGG, h=HHHHH, i=IIIII, j=CCCCC, k=EEE, l=EEE}", body.toString());
    }

    public Map<String, String> execute(String random) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("0", random);
        body.put("a", "AAA");
        body.put("b", "BBB");
        body.put("c", "CCC");
        body.put("d", "DDD");
        body.put("e", "EEE");
        body.put("f", "FFF");
        body.put("g", "GGG");
        int rulesCount = DroolsUtils.fireRules("test01", null, RequestFact.builder()
                .uri("/abc")
                .body(body)
                .build());
        log.info("result: {}", body);
        Assertions.assertEquals(1, rulesCount);
        return body;
    }

}
