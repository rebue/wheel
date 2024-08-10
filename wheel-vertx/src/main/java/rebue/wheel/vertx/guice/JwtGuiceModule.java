package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtGuiceModule extends AbstractModule {

    public JwtGuiceModule() {
        log.info("new JwtGuiceModule");
    }

    @Singleton
    @Provides
    JWTAuth getJwtAuth(Vertx vertx, @Named("config") final JsonObject config) {
        log.info("JwtGuiceModule.getJwtAuth");
        JsonObject jwtConfig = config.getJsonObject("jwt");
        if (jwtConfig == null) {
            log.warn("未配置JWT选项");
            return null;
        }

        JWTAuthOptions options = new JWTAuthOptions(jwtConfig);
        return JWTAuth.create(vertx, options);
    }

}
