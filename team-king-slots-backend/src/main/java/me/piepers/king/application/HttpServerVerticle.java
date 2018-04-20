package me.piepers.king.application;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The Http server which responds to Rest API calls.
 *
 * @author Bas Piepers
 */
public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private Integer port;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.vertx = new io.vertx.reactivex.core.Vertx(vertx);

        final Optional<JsonObject> httpServerOptional = Optional.ofNullable(context.config().getJsonObject("http_server"));

        this.port = httpServerOptional.map(obj -> obj.getInteger("port"))
                .orElse(8080);
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        // TODO: doesn't exist yet (index.html).
        router.route("/*").handler(StaticHandler.create("index.html"));

        // Rest endpoints
        // TODO

        // Start the server
        vertx.createHttpServer().requestHandler(router::accept).rxListen(this.port).subscribe(result -> {
            LOGGER.debug("Http server has started on port {}.", this.port);
            future.complete();
        }, throwable -> {
            LOGGER.error("Http server failed to start.");
            future.fail(throwable);
        });
    }
}
