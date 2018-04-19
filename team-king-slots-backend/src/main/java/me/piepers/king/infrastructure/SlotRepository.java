package me.piepers.king.infrastructure;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;

/**
 * A repository that stores the slot machines that are currently active.
 *
 * @author Bas Piepers
 */
@VertxGen
@ProxyGen
public interface SlotRepository {
    String EVENT_BUS_ADDRESS = "com.piepers.king.domain.SlotRepository";

    static SlotRepository create(Vertx vertx) {
        return new InMemorySlotRepository(vertx);
    }


    static SlotRepository createProxy(Vertx vertx) {
        return new SlotRepositoryVertxEBProxy(vertx, EVENT_BUS_ADDRESS);
    }
}
