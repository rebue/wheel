package rebue.wheel.vertx.guice;

import com.google.inject.Injector;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.impl.verticle.CompilingClassLoader;
import io.vertx.core.spi.VerticleFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class GuiceVerticleFactory implements VerticleFactory {

    private final Injector injector;

    public GuiceVerticleFactory(final Injector injector) {
        this.injector = injector;
    }

    @Override
    public String prefix() {
        return "guice";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createVerticle(final String verticleName, final ClassLoader classLoader, final Promise<Callable<Verticle>> promise) {
        log.info("GuiceVerticleFactory.createVerticle");
        String                verticleClassName = VerticleFactory.removePrefix(verticleName);
        final Class<Verticle> clazz;
        try {
            ClassLoader verticleClassLoader = classLoader;
            if (verticleName.endsWith(".java")) {
                final CompilingClassLoader compilingLoader = new CompilingClassLoader(classLoader, verticleClassName);
                verticleClassName = compilingLoader.resolveMainClassName();
                verticleClassLoader = compilingLoader;
            }
            clazz = (Class<Verticle>) verticleClassLoader.loadClass(verticleClassName);
            log.info("注入{}实例的属性", verticleClassName);
            promise.complete(() -> {
                Verticle instance = this.injector.getInstance(clazz);
                if (instance instanceof InjectorVerticle injectorVerticle) {
                    injectorVerticle.setInjector(injector);
                }
                return instance;
            });
        } catch (final ClassNotFoundException e) {
            promise.fail(e);
        }

    }

}
