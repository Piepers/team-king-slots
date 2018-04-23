package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceException;
import me.piepers.king.domain.Slot;
import me.piepers.king.domain.SlotId;
import me.piepers.king.domain.SlotService;
import me.piepers.king.reactivex.infrastructure.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * The service that responds to requests pertaining to slot machines.
 *
 * @author Bas Piepers
 */
public class SlotServiceImpl implements SlotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlotServiceImpl.class);
    private final SlotRepository repository;

    public SlotServiceImpl(Vertx vertx) {
        // TODO: investigate the need for instantiating a new Vertx of re-use an existing one.
        this.repository = SlotRepository.createProxy(new io.vertx.reactivex.core.Vertx(vertx));
    }

    @Override
    public void start(Handler<AsyncResult<String>> resultHandler) {
        // Instantiate a Slot
        SlotId id = SlotId.create();
        Slot slot = new Slot(id, "Free Slot " + id.getId(), 0L, Instant.now(), "John Doe");
        repository
                // Store it
                .rxAdd(slot)
                .subscribe(s -> resultHandler
                                // And return it
                                .handle(Future.succeededFuture(s.getId().getId())),
                        throwable -> resultHandler
                                .handle(ServiceException.fail(503, throwable.getMessage())));
    }

    @Override
    public void quit(String uuid, Handler<AsyncResult<Void>> resultHandler) {
        repository
                // Don't do anything in case an error occured.
                .rxDeleteById(uuid)
                .doOnError(throwable -> LOGGER.error("Unable to delete slot with id {}", uuid))
                .subscribe(slot -> resultHandler.handle(Future.succeededFuture()));

    }

    @Override
    public void spin(String uuid, Handler<AsyncResult<Slot>> resultHandler) {

    }

    @Override
    public void stop(String uuid, Handler<AsyncResult<Slot>> resultHandler) {

    }
}
