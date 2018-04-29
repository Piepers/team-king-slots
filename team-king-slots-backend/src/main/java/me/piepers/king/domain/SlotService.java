package me.piepers.king.domain;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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

    /**
     * Creates a machine and returns the id of the machine to the caller. The id is used for subsequent calls to
     * do additional actions.
     *
     * @param resultHandler, the result handler with the created id for the slot.
     */
    void start(Handler<AsyncResult<SlotId>> resultHandler);

    /**
     * A signal that a player quits playing a slot so that it can be discarded.
     *
     * @param uuid,          the id of the slot to clean up. Effectively removes the slot.
     * @param resultHandler, doesn't return anything. Silently removes the slot if it exist and doesn't return an error
     *                       if it doesn't exist.
     */
    void quit(String uuid, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Simulates spinning the wheels on a slot with the given id. Returns the slot so that the front-end may do
     * something with the contents. For example: in the future it may be possible to stop one of the reels of the slot
     * instead of all of them.
     *
     * @param uuid, the id of the slot.
     * @param resultHandler, contains the slot that was just triggered or an error in case the given id doesn't exist.
     */
    void spin(String uuid, Handler<AsyncResult<Slot>> resultHandler);

    /**
     * Stops spinning the reels manually. This is also done with a timer automatically but a user can also stop the
     * spin. At the moment the service is only capable of stopping all the slots but later on it may be
     * possible to stop just one or a sub-set of the reels that are spinning.
     * @param uuid, the id of the slot to stop the reels.
     * @param resultHandler, contains the Slot that was stopped with the score or an error in case the slot was not
     *                       found or in case the reels were not spinning.
     */
    void stop(String uuid, Handler<AsyncResult<SpinResult>> resultHandler);
}
