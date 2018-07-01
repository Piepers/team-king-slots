package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Set;

/**
 * Local random number implementation not for production use. Uses the java.util.{@link java.util.Random} class to
 * generate numbers.
 *
 * @author Bas Piepers
 */
public class LocalRandomNumberServiceImpl implements RandomNumberService {


    @Override
    public void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<Set<Integer>>> resultHandler) {

    }
}
