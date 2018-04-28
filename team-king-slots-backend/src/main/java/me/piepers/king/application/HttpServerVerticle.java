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
import java.util.Objects;
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
        subRouter.route(HttpMethod.GET, "/quit/:slotId").handler(this::quitHandler);
        subRouter.route(HttpMethod.POST, "/spin/:slotId").handler(this::spinHandler);
        subRouter.route(HttpMethod.POST, "/stop/:slotId").handler(this::stopHandler);

        router.mountSubRouter("/api", subRouter);

        // Start the server
        vertx.
                createHttpServer()
                .requestHandler(router::accept)
                .rxListen(this.port)
                .subscribe(result -> {
                    LOGGER.debug("Http server has started on port {}.", this.port);
                    future.complete();
                }, throwable -> {
                    LOGGER.error("Http server failed to start.");
                    future.fail(throwable);
                });
    }

    private void startHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking start end-point");

        slotService
                .rxStart()
                .subscribe(slotId -> {
                    LOGGER.debug("Started slot. Received id: {}", slotId);
                    routingContext
                            .response()
                            .setStatusCode(200)
                            .putHeader("Content-Type", "application/json; charset=UTF-8")
                            .end(slotId.toJson().encode(), StandardCharsets.UTF_8.name());
                }, throwable -> {
                    LOGGER.error("An error occurred while trying to start a new Slot.", throwable.getMessage());
                    routingContext
                            .response()
                            .setStatusCode(503)
                            .putHeader("Content-Type", "application/json; charset=UTF-8")
                            .end(new JsonObject()
                                    .put("Error", throwable.getMessage())
                                    .encode(), StandardCharsets.UTF_8.name());
                });


    }

    private void quitHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking quit end-point");

        String id = routingContext.request().getParam("slotId");
        if (Objects.isNull(id)) {
            routingContext
                    .response()
                    .setStatusCode(400)
                    .end();
        } else {
            slotService
                    .rxQuit(id)
                    .subscribe(() -> {
                        LOGGER.debug("Quit Slot with id {}", id);
                        routingContext
                                .response()
                                .setStatusCode(200)
                                .end();
                    }, throwable -> {
                        LOGGER.error("An error occured while trying to stop slot with id {}", id);
                        routingContext
                                .response()
                                .setStatusCode(503)
                                .putHeader("Content-Type", "application/json; charset=UTF-8")
                                .end(new JsonObject().put("Error", throwable.getMessage()).encode(), StandardCharsets.UTF_8.name());
                    });
        }
    }

    private void spinHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking spin end-point");
        String id = routingContext.request().getParam("slotId");
        if (Objects.isNull(id)) {
            routingContext
                    .response()
                    .setStatusCode(400)
                    .end();

        } else {
            slotService.rxSpin(id)
                    .subscribe(spinResult -> {
                        LOGGER.debug("Spinresult is {}", spinResult.toString());
                        routingContext.response().putHeader("Content-Type", "application/json; charset=UTF-8")
                                .end(spinResult.toJson().encode(), StandardCharsets.UTF_8.name());
                    }, throwable -> {
                        LOGGER.error("Error while trying to invoke \"spin\".", throwable);
                        routingContext
                                .response()
                                .putHeader("Content-Type", "application/json; charset=UTF-8")
                                .end(new JsonObject()
                                        .put("Error", throwable.getMessage())
                                        .encode(), StandardCharsets.UTF_8.name());
                    });
        }
    }

    private void stopHandler(RoutingContext routingContext) {
        LOGGER.debug("Invoking sop end-point");

        String id = routingContext.request().getParam("slotId");
        if (Objects.isNull(id)) {
            routingContext
                    .response()
                    .setStatusCode(400)
                    .end();
        } else {
            slotService.rxStop(id)
                    .subscribe(slot -> {
                        LOGGER.debug("Stopped spinning for id {}", id);
                        routingContext
                                .response()
                                .putHeader("Content-Type", "application/json; charset=UTF-8")
                                .end(slot.toJson().encode(), StandardCharsets.UTF_8.name());
                    }, throwable -> {
                        LOGGER.error("Failure while trying to stop spinning.", throwable);
                        routingContext
                                .response()
                                .putHeader("Content-Type", "application/json; charset=UTF-8")
                                .end(new JsonObject()
                                        .put("Error", throwable.getMessage())
                                        .encode(), StandardCharsets.UTF_8.name());
                    });
        }
    }
}
