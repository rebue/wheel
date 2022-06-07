
package rebue.wheel.vertx.guice;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.verticle.CompilingClassLoader;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VerticleFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiceVerticleFactory implements VerticleFactory {

    private JsonObject config;
    private Injector   injector;

    public GuiceVerticleFactory(final JsonObject config) {
        this.config = config;
    }

    @Override
    public String prefix() {
        return "guice";
    }

    @Override
    public void init(final Vertx vertx) {
        final List<Module> modules = Lists.<Module>newLinkedList(ServiceLoader.load(Module.class));
        modules.add(new VertxGuiceModule(vertx, this.config));
        this.injector = Guice.createInjector(modules);
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
                verticleClassName   = compilingLoader.resolveMainClassName();
                verticleClassLoader = compilingLoader;
            }
            clazz = (Class<Verticle>) verticleClassLoader.loadClass(verticleClassName);
            promise.complete(() -> this.injector.getInstance(clazz));
        } catch (final ClassNotFoundException e) {
            promise.fail(e);
            return;
        }

    }

}
