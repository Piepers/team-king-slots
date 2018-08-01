package me.piepers.king.infrastructure;

import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;
import me.piepers.king.domain.*;
import me.piepers.king.reactivex.infrastructure.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The service that responds to requests pertaining to slot machines.
 *
 * @author Bas Piepers
 */
public class SlotServiceImpl implements SlotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlotServiceImpl.class);
    private final SlotRepository repository;
    private final io.vertx.reactivex.core.Vertx rxVertx;

    public SlotServiceImpl(Vertx vertx) {
        this.rxVertx = new io.vertx.reactivex.core.Vertx(vertx);
        this.repository = SlotRepository.createProxy(rxVertx);
    }

    @Override
    public void start(Handler<AsyncResult<SlotId>> resultHandler) {
        // Instantiate a Slot
        Slot slot = Slot.of(SlotType.CLASSIC, "John Doe");
        repository
                // Store it
                .rxAdd(slot)
                .subscribe(s -> resultHandler
                                // And return it
                                .handle(Future.succeededFuture(s.getId())),
                        throwable -> resultHandler
                                .handle(ServiceException.fail(503, throwable.getMessage())));
    }

    @Override
    public void quit(String uuid, Handler<AsyncResult<Void>> resultHandler) {
        repository
                .rxDeleteById(uuid)
                .subscribe(slot -> resultHandler.handle(Future.succeededFuture()),
                        throwable -> {
                            LOGGER.error("Unable to delete slot with id {}", uuid);
                            resultHandler.handle(ServiceException.fail(503, throwable.getMessage()));
                        });

    }

    @Override
    public void spin(String uuid, Handler<AsyncResult<Slot>> resultHandler) {
        repository
                .rxFindById(uuid)
                .flatMap(slot -> Single.just(slot
                        .spin()))
                .flatMap(slot -> repository.rxSave(slot))
                .subscribe(slot -> resultHandler
                                .handle(Future.succeededFuture(slot)),
                        throwable -> resultHandler.handle(Future.failedFuture(throwable)));
    }

    @Override
    public void stop(String uuid, Handler<AsyncResult<SpinResult>> resultHandler) {
        repository
                .rxFindById(uuid)
                .doOnSuccess(slot -> LOGGER.debug("Obtained slot from repo: {}", slot.toJson().encodePrettily()))
                // We need some kind of status check here to prevent us from wasting random numbers.
                .flatMap(slot -> slot.stop(this::getNumbersForReel))
                .doOnSuccess(spinResult -> {
                    LOGGER.debug("Updating slot {}", spinResult.getSlot().toJson().encodePrettily());
                    repository
                            .rxSave(spinResult.getSlot())
                            .subscribe();
                })
                .subscribe(spinResult -> resultHandler.handle(Future.succeededFuture(spinResult)),
                        throwable -> resultHandler.handle(ServiceException.fail(503, throwable.getMessage())));
    }


    private Single<List<Integer>> getNumbersForReel(Slot slot) {
        return this.rxVertx
                .eventBus()
                .<JsonObject>rxSend("get.numbers", new JsonObject()
                        .put("amount", slot.getReel().getCellAmount()))
                .flatMap(message -> Single
                        .just((List<Integer>)message
                                .body()
                                .getJsonArray("numbers")
                                .getList()));
    }
}
