package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.grpc.ManagedChannel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxChannelBuilder;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class GrpcNettyGuiceModule extends AbstractModule {

    public GrpcNettyGuiceModule() {
        log.info("new GRpcNettyGuiceModule");
    }

    @Singleton
    @Provides
    Map<String, ManagedChannel> getGrpcChannels(Vertx vertx, @Named("config") final JsonObject config) {
        log.info("GRpcNettyGuiceModule.getGRpcChannel");
        Map<String, ManagedChannel> result   = new LinkedHashMap<>();
        final JsonObject            grpc     = config.getJsonObject("grpc");
        JsonObject                  channels = grpc.getJsonObject("channels");
        channels.forEach(item -> {
            String     key   = item.getKey();
            JsonObject value = (JsonObject) item.getValue();
            result.put(key, VertxChannelBuilder
                    .forAddress(vertx, value.getString("host"), value.getInteger("port"))
                    .usePlaintext()
                    .build());
        });
        return result;
    }

}
