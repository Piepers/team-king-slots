package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Local random number implementation not for production use. Uses the java.util.{@link java.util.Random} class to
 * generate numbers.
 *
 * @author Bas Piepers
 */
public class LocalRandomNumberServiceImpl implements RandomNumberService {


    /**
     * Uses the {@link Random#ints()} method to obtain the given amount of int random numbers and puts it into a
     * {@link java.util.ArrayList} because duplicates may be present.
     *
     * @param amount,        the amount of numbers to obtain.
     * @param min,           the lowest value a number in the list of random numbers should have.
     * @param max,           the highest value a number in the list of random number should have.
     * @param resultHandler, the result handler that contains a collection (a list) with the numbers it received from the
     */
    @Override
    public void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<List<Integer>>> resultHandler) {
        Random r = new Random();
        ArrayList<Integer> randomNumbers = r.ints(amount, min, max).boxed().collect(Collectors.toCollection(ArrayList::new));
        resultHandler.handle(Future.succeededFuture(randomNumbers));
    }
}
