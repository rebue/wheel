package rebue.wheel.vertx.guice;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;

@SuppressWarnings("deprecation")
public class WebGuiceModule extends AbstractModule {

    @Singleton
    @Provides
    SchemaParser getSchemaParser(final Vertx vertx) {
        return SchemaParser.createOpenAPI3SchemaParser(
                SchemaRouter.create(vertx, new SchemaRouterOptions()));
    }

}
