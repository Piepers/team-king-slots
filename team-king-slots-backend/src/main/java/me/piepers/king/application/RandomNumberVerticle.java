package me.piepers.king.application;

import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;

/**
 * A Verticle that keeps a random number stock obtained from a web service at random.org.
 *
 * @author Bas Piepers
 */
public class RandomNumberVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomNumberVerticle.class);

    // The cache of random numbers.
    private ArrayDeque<Integer> stash;

    // The threshold at which this service requests a new batch of random numbers.
    private static final Integer MIN_THRESHOLD = 100;

}
