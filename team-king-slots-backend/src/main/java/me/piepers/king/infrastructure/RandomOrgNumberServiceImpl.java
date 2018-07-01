package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Set;

/**
 * An implementation of the random service that uses the random.org service to obtain real random numbers. Also
 * keeps track of how many requests can still be made and fails in case too much requests have been made.
 *
 * @author Bas Piepers
 *
 */
public class RandomOrgNumberServiceImpl implements RandomNumberService {


    @Override
    public void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<Set<Integer>>> resultHandler) {

    }
}
