package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoGuiceModule extends AbstractModule {

    public MongoGuiceModule() {
        log.info("new MongoGuiceModule");
    }

    @Singleton
    @Provides
    MongoClient getMongoClient(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("MongoGuiceModule.getMongoClient");
        return MongoClient.createShared(vertx, config.getJsonObject("mongo"));
    }

}
