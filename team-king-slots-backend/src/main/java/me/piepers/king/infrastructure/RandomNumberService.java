package me.piepers.king.infrastructure;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.Set;

/**
 *
 * A service that generates random numbers for the slot machines.
 *
 * @author Bas Piepers
 *
 */
@VertxGen
@ProxyGen
public interface RandomNumberService {

    String EVENT_BUS_ADDRESS = "random-number-service";

//    static RandomNumberService(Vertx vertx)

    /**
     * Obtain a list of random numbers with a min/max and amount.
     *
     * @param amount,        the amount of numbers to obtain.
     * @param min,           the lowest value a number in the list of random numbers should have.
     * @param max,           the highest value a number in the list of random number should have.
     * @param resultHandler, the result handler that contains a collection (a set) with the numbers it received from the
     *                       web service.
     */
    void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<Set<Integer>>> resultHandler);
}
