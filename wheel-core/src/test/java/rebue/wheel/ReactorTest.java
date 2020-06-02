package rebue.wheel;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * https://www.ibm.com/developerworks/cn/java/j-cn-with-reactor-response-encode/index.html
 *
 */
public class ReactorTest {

    /**
     * 通过 Flux 类的静态方法创建 Flux 序列
     */
    @Test
    @Disabled
    public void test01() throws InterruptedException {
        Flux.just("Hello", "World").subscribe(System.out::println);
        Flux.fromArray(new Integer[] { 1, 2, 3 }).subscribe(System.out::println);
        Flux.empty().subscribe(System.out::println);
        Flux.range(1, 10).subscribe(System.out::println);
        Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
        Flux.interval(Duration.of(1000, ChronoUnit.MILLIS)).subscribe(System.out::println);
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
    }

    /**
     * 使用 generate()方法生成 Flux 序列
     */
    @Test
    @Disabled
    public void test02() {
        Flux.generate(sink -> {
            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);

        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            final int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);
    }

    /**
     * 使用 create()方法生成 Flux 序列
     */
    @Test
    @Disabled
    public void test03() {
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    /**
     * 创建 Mono 序列
     */
    @Test
    @Disabled
    public void test04() {
        Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
    }

    /**
     * buffer 相关操作符的使用示例
     */
    @Test
    @Disabled
    public void test05() {
        Flux.range(1, 100).buffer(20).subscribe(System.out::println);
        Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).buffer(Duration.of(1001, ChronoUnit.MILLIS)).take(3).toStream().forEach(System.out::println);
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
    }

    /**
     * filter 操作符使用示例
     */
    @Test
    @Disabled
    public void test06() {
        Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);
    }

    /**
     * window 操作符使用示例
     */
    @Test
    @Disabled
    public void test07() {
        Flux.range(1, 100).window(20).subscribe(System.out::println);
        Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).window(Duration.of(1001, ChronoUnit.MILLIS)).take(2).toStream().forEach(System.out::println);
    }

    /**
     * zipWith 操作符使用示例
     */
    @Test
    @Disabled
    public void test08() {
        Flux.just("a", "b").zipWith(Flux.just("c", "d")).subscribe(System.out::println);
        Flux.just("a", "b").zipWith(Flux.just("c", "d"), (s1, s2) -> String.format("%s-%s", s1, s2)).subscribe(System.out::println);
    }

    /**
     * take 系列操作符使用示例
     */
    @Test
    @Disabled
    public void test09() {
        Flux.range(1, 1000).take(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);
    }

    /**
     * reduce 和 reduceWith 操作符使用示例
     */
    @Test
    @Disabled
    public void test10() {
        Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
        Flux.range(1, 100).reduceWith(() -> 100, (x, y) -> x + y).subscribe(System.out::println);
    }

    /**
     * merge 和 mergeSequential 操作符使用示例
     */
    @Test
    @Disabled
    public void test11() {
        Flux.merge(Flux.interval(Duration.ZERO, Duration.of(100, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5)).toStream().forEach(System.out::println);
        Flux.mergeSequential(Flux.interval(Duration.ZERO, Duration.of(100, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5)).toStream().forEach(System.out::println);
    }

    /**
     * flatMap 操作符使用示例
     */
    @Test
    @Disabled
    public void test12() {
        Flux.just(5, 10).flatMap(x -> Flux.interval(Duration.of(x * 10, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(x)).toStream().forEach(System.out::println);
    }

    /**
     * concatMap 操作符使用示例
     */
    @Test
    @Disabled
    public void test13() {
        Flux.just(5, 10).concatMap(x -> Flux.interval(Duration.of(x * 10, ChronoUnit.MILLIS)).take(x)).toStream().forEach(System.out::println);
    }

    /**
     * combineLatest 操作符使用示例
     */
    @Test
    @Disabled
    public void test14() {
        Flux.combineLatest(Arrays::toString, Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5)).toStream().forEach(System.out::println);
    }

    /**
     * 通过 subscribe()方法处理正常和错误消息
     */
    @Test
    @Disabled
    public void test15() {
        Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).subscribe(System.out::println);
        Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).subscribe(System.out::println, System.err::println);
    }

    /**
     * 出现错误时返回默认值
     */
    @Test
    @Disabled
    public void test16() {
        Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).onErrorReturn(0).subscribe(System.out::println);
    }

    /**
     * 出现错误时根据异常类型来选择流
     */
    @Test
    @Disabled
    public void test17() {
        Flux.just(1, 2).concatWith(Mono.error(new IllegalArgumentException())).onErrorResume(e -> Mono.just(0)).subscribe(System.out::println);
        Flux.just(1, 2).concatWith(Mono.error(new IllegalArgumentException())).onErrorResume(e -> {
            if (e instanceof IllegalStateException) {
                return Mono.just(0);
            } else if (e instanceof IllegalArgumentException) {
                return Mono.just(-1);
            }
            return Mono.empty();
        }).subscribe(System.out::println);
    }

    /**
     * 出现错误时根据异常类型来选择流
     */
    @Test
    @Disabled
    public void test18() {
        Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).retry(1).subscribe(System.out::println, System.err::println);
        Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).retry(1).subscribe(System.out::println);
    }

    /**
     * 使用调度器切换操作符执行方式
     */
    @Test
//    @Disabled
    public void test19() {
        Flux.create(sink -> {
            sink.next(Thread.currentThread().getName());
            sink.complete();
        }).publishOn(Schedulers.single()).map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x)).publishOn(Schedulers.elastic())
                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x)).subscribeOn(Schedulers.parallel()).toStream().forEach(System.out::println);
    }
}
