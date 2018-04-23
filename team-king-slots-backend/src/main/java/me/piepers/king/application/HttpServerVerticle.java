package me.piepers.king.application;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import me.piepers.king.reactivex.domain.SlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * The Http server which responds to Rest API calls, handles static content as well as web-socket communication.
 *
 * @author Bas Piepers
 */
public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private Integer port;

    // FIXME: for now a direct reference to the (proxy of) the SlotService. Must be decoupled to a command handler later on.
    private SlotService slotService;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.vertx = new io.vertx.reactivex.core.Vertx(vertx);

        final Optional<JsonObject> httpServerOptional = Optional.ofNullable(context.config().getJsonObject("http_server"));

        this.port = httpServerOptional.map(obj -> obj.getInteger("port"))
                .orElse(8080);

        this.slotService = SlotService.createProxy(super.vertx);
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        // Instantiate the service we will communicate with for the prototype. Later on this must be decoupled.
        slotService = SlotService.createProxy(vertx);

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        // TODO: doesn't exist yet (index.html).
        router.route("/*").handler(StaticHandler.create("index.html"));

        // Rest endpoints in a subrouter
        Router subRouter = Router.router(vertx);
        subRouter.route(HttpMethod.GET, "/start").handler(this::startHandler);
        subRouter.route(HttpMethod.POST, "/quit/:slotId").handler(this::quitHandler);
        subRouter.route(HttpMethod.POST, "/spin/:slotId").handler(this::spinHandler);
        subRouter.route(HttpMethod.POST, "/stop/:slotId").handler(this::stopHandler);

        router.mountSubRouter("/api", subRouter);

        // Start the server
        vertx.createHttpServer().requestHandler(router::accept).rxListen(this.port).subscribe(result -> {
            LOGGER.debug("Http server has started on port {}.", this.port);
            future.complete();
        }, throwable -> {
            LOGGER.error("Http server failed to start.");
            future.fail(throwable);
        });
    }

    private void startHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking start end-point");
        routingContext
                .response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(example("Start").encode(), StandardCharsets.UTF_8.name());
    }

    private void quitHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking quit end-point");
        routingContext
                .response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(example("Quit").encode(), StandardCharsets.UTF_8.name());
    }

    private void spinHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking spin end-point");
        routingContext
                .response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(example("Spin").encode(), StandardCharsets.UTF_8.name());
    }

    private void stopHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking sop end-point");
        routingContext
                .response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(example("Stop").encode(), StandardCharsets.UTF_8.name());
    }

    private JsonObject example(String function) {
        return new JsonObject().put("Function", function).put("Result", "Ok");
    }
}
