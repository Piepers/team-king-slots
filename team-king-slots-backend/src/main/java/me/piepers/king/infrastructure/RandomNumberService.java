package me.piepers.king.infrastructure;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;

/**
 * A service that generates random numbers. The implementations of this method use either a local variant of the random
 * number generation for testing purposes or an external service to obtain the numbers. This service will be started
 * with either one of them (production or local).
 *
 * @author Bas Piepers
 */
@VertxGen
@ProxyGen
public interface RandomNumberService {

    String EVENT_BUS_ADDRESS = "random-number-service";

    static RandomNumberService create(Vertx vertx) {
        return new LocalRandomNumberServiceImpl();
    }

    static RandomNumberService createProxy(Vertx vertx) {
        return new RandomNumberServiceVertxEBProxy(vertx, EVENT_BUS_ADDRESS);
    }

    /**
     * Obtain a list of random numbers with a min/max and amount.
     *
     * @param amount,        the amount of numbers to obtain.
     * @param min,           the lowest value a number in the list of random numbers should have.
     * @param max,           the highest value a number in the list of random number should have.
     * @param resultHandler, the result handler that contains a response dto that contains the random numbers but also
     *                       information about how many requests we have left etc.
     */
    void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<RandomNumberResponseDto>> resultHandler);
}
