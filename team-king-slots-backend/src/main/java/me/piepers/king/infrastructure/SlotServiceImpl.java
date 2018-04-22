package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.piepers.king.domain.Slot;
import me.piepers.king.domain.SlotService;

/**
 * The service that responds to requests pertaining to slot machines.
 *
 * @author Bas Piepers
 *
 */
public class SlotServiceImpl implements SlotService {

    public SlotServiceImpl(Vertx vertx) {
    }

    @Override
    public void start(Handler<AsyncResult<String>> resultHandler) {

    }

    @Override
    public void quit(String uuid, Handler<AsyncResult<Void>> resultHandler) {

    }

    @Override
    public void spin(String uuid, Handler<AsyncResult<Slot>> resultHandler) {

    }

    @Override
    public void stop(String uuid, Handler<AsyncResult<Slot>> resultHandler) {

    }
}
