package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.serviceproxy.ServiceException;
import me.piepers.king.domain.Slot;
import me.piepers.king.domain.SlotId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * For prototyping purposes: store the slot instances that were created by users (and their instances)
 *
 * @author Bas Piepers
 */
public class InMemorySlotRepository implements SlotRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySlotRepository.class);

    private Vertx rxVertx;
    private Map<SlotId, Slot> slots;

    public InMemorySlotRepository(io.vertx.core.Vertx vertx) {
        // TODO: check the need for instantiating a reactive vertx.
        this.rxVertx = new Vertx(vertx);
        this.slots = new HashMap<>();
    }

    @Override
    public void add(Slot slot, Handler<AsyncResult<Slot>> resultHandler) {
        if (Objects.isNull(slot.getId())) {
            resultHandler.handle(ServiceException.fail(503, "Unable to create slot due to missing id."));
        } else if (slots.containsKey(slot.getId())) {
            LOGGER.debug("Unable to create a slot with id {} since a slot machine with that id already exists.", slot.getId().getId());
            resultHandler.handle(ServiceException.fail(503, "Unable to create slot because the given id already exists."));
        } else {
            slots.put(slot.getId(), slot);
            LOGGER.debug("Slot has been added to store with id {}.", slot.getId().getId());
            resultHandler.handle(Future.succeededFuture(slots.get(slot.getId())));
        }
    }

    @Override
    public void save(Slot slot, Handler<AsyncResult<Slot>> resultHandler) {
        LOGGER.debug("Updating slot in repository with id: {}", slot.getId().getId());

        // TODO: probably good to at least validate if an id is present.
        slots.put(slot.getId(), slot);

        resultHandler.handle(Future.succeededFuture(slots.get(slot.getId())));
    }

    @Override
    public void deleteById(String uuid, Handler<AsyncResult<Slot>> resultHandler) {
        if (Objects.isNull(uuid)) {
            resultHandler.handle(ServiceException.fail(503, "Unable to delete slot due to missing slot id"));
        } else {
            Slot removed = slots.remove(SlotId.of(uuid));
            if (Objects.isNull(removed)) {
                resultHandler.handle(ServiceException.fail(404, "Unable to remove Slot with id " + uuid));
            } else {
                resultHandler.handle(Future.succeededFuture(removed));
            }
        }


    }

    @Override
    public void findById(String uuid, Handler<AsyncResult<Slot>> resultHandler) {
        if (Objects.isNull(uuid)) {
            resultHandler.handle(ServiceException.fail(503, "Unable to find a slot due to missing id."));
        } else {
            Slot slot = slots.get(SlotId.of(uuid));
            if (Objects.nonNull(slot)) {
                resultHandler.handle(Future.succeededFuture(slot));
            } else {
                resultHandler.handle(ServiceException.fail(404, "Unable to find a slot with id " + uuid));
            }
        }
    }

}
