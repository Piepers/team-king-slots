package me.piepers.king.infrastructure;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.piepers.king.domain.Slot;

import java.util.List;

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

    /**
     * Adds a new instance of a machine to the repository. Fails in case it already exists (based on the UUID).
     *
     * @param slot,          the slot instance to create.
     * @param resultHandler, contains the {@link Slot} that was just added or an error message if adding it failed.
     */
    void add(Slot slot, Handler<AsyncResult<Slot>> resultHandler);

    /**
     * Save the given slot. In case the given slot does not already exists, automatically adds it.
     *
     * @param slot,          the {@link Slot} to update.
     * @param resultHandler, contains the {@link Slot} that was just updated or an error in case something failed.
     */
    void save(Slot slot, Handler<AsyncResult<Slot>> resultHandler);

    /**
     * Deletes a {@link Slot} with the given uuid. Throws an error in case the {@link Slot} was not found.
     *
     * @param uuid,          the uuid of the slot machine to remove.
     * @param resultHandler, in case the slot was removed it contains the removed slot. Could contain an error in case
     *                       the slot was not found.
     */
    void deleteById(String uuid, Handler<AsyncResult<Slot>> resultHandler);

    /**
     * Retrieves a {@link Slot} by its id.
     *
     * @param uuid,          the unique identifier of the {@link Slot}.
     * @param resultHandler, contains the {@link Slot} in case it was found.
     */
    void findById(String uuid, Handler<AsyncResult<Slot>> resultHandler);

}
