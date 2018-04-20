package me.piepers.king.application;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.AbstractVerticle;
import me.piepers.king.reactivex.domain.SlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main command handler that dispatches incoming commands to a service.
 */
public class SlotCommandHandlerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlotCommandHandlerVerticle.class);

    private SlotService slotService;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.slotService = SlotService.createProxy(super.vertx);
    }

    @Override
    public void start() throws Exception {
        // TODO: code for dispatching incoming commands to services.
    }
}
