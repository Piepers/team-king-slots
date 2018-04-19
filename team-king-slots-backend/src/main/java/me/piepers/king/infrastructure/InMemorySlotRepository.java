package me.piepers.king.infrastructure;

import io.vertx.reactivex.core.Vertx;
import me.piepers.king.domain.Slot;
import me.piepers.king.domain.SlotId;

import java.util.HashMap;
import java.util.Map;

/**
 * For prototyping purposes: store the slot instances that were created by users (and their instances)
 *
 * @author Bas Piepers
 */
public class InMemorySlotRepository implements SlotRepository {

    private Vertx rxVertx;
    private Map<SlotId, Slot> slots;

    public InMemorySlotRepository(io.vertx.core.Vertx vertx) {
        this.rxVertx = new Vertx(vertx);
        this.slots = new HashMap<>();
    }
}
