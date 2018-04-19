package me.piepers.king.domain;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import me.piepers.king.infrastructure.SlotServiceImpl;

/**
 * Main service that handles slot related logic.
 *
 * @author Bas Piepers
 */
@VertxGen
@ProxyGen
public interface SlotService {
    String EVENT_BUS_ADDRESS = "com.piepers.king.domain.SlotService";

    static SlotService create(Vertx vertx) {
        return new SlotServiceImpl(vertx);
    }

    static SlotService createProxy(Vertx vertx) {
        return new SlotServiceVertxEBProxy(vertx, EVENT_BUS_ADDRESS);
    }
}
