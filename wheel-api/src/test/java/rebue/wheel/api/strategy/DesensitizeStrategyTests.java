package rebue.wheel.api.strategy;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesensitizeStrategyTests {

    @Test
    public void test01() {
        log.info(DesensitizeStrategy.USERNAME.getDesensitizer().apply("张"));
        log.info(DesensitizeStrategy.USERNAME.getDesensitizer().apply("张三"));
        log.info(DesensitizeStrategy.USERNAME.getDesensitizer().apply("李四"));
        log.info(DesensitizeStrategy.USERNAME.getDesensitizer().apply("周五五"));
        log.info(DesensitizeStrategy.USERNAME.getDesensitizer().apply("吴六六六"));
        log.info(DesensitizeStrategy.TEL.getDesensitizer().apply("3284773"));
        log.info(DesensitizeStrategy.MOBILE.getDesensitizer().apply("13276494937"));
        log.info(DesensitizeStrategy.MOBILE.getDesensitizer().apply("13933253233"));
        log.info(DesensitizeStrategy.MOBILE.getDesensitizer().apply("13832733403"));
        log.info(DesensitizeStrategy.ID_CARD.getDesensitizer().apply("450104198709372096"));
        log.info(DesensitizeStrategy.ADDRESS.getDesensitizer().apply("广西壮族自治区南宁市五象新区新湖街道38-3号1栋2单元303号房"));
    }

}
