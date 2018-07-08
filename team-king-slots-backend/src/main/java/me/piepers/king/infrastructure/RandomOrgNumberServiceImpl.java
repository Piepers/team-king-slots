package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import me.piepers.king.infrastructure.http.RandomOrgWebClient;

import java.util.List;

/**
 * An implementation of the random service that uses the random.org service to obtain real random numbers. Also
 * keeps track of how many requests can still be made and fails in case too much requests have been made.
 *
 * @author Bas Piepers
 */
public class RandomOrgNumberServiceImpl implements RandomNumberService {

    private RandomOrgWebClient randomClient;

    public RandomOrgNumberServiceImpl(Vertx vertx) {
        this.randomClient = new RandomOrgWebClient(new io.vertx.reactivex.core.Vertx(vertx));
    }

    @Override
    public void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<List<Integer>>> resultHandler) {
        // TODO
    }
}
