package me.piepers.king.application;

import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;

/**
 * A Verticle that keeps a random number stock obtained from one of the random service services. Depending on the
 * profile, this is either the Random.org web service or a local random service.
 *
 * @author Bas Piepers
 */
public class RandomNumberVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomNumberVerticle.class);

    // The cache of random numbers.
    private ArrayDeque<Integer> rcache;

    // The threshold at which this service requests a new batch of random numbers.
    private static final Integer MIN_THRESHOLD = 100;

}
