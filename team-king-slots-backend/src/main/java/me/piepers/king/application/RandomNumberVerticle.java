package me.piepers.king.application;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import me.piepers.king.reactivex.infrastructure.RandomNumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A Verticle that keeps a random number stock obtained from one of the random service services. Depending on the
 * profile, this is either the Random.org web service or a local random service.
 *
 * @author Bas Piepers
 */
public class RandomNumberVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomNumberVerticle.class);
    private static final Integer DEFAULT_MIN_THRESHOLD = 100;
    private static final Integer DEFAULT_MAX_NRS_PER_REQUEST = 10;
    private static final Integer DEFAULT_HIGHEST_NUMBER = 1000;
    private static final Integer DEFAULT_LOWEST_NUMBER = 0;
    private static final Integer DEFAULT_BLOCK_AMOUNT = 2000;

    // The cache of random numbers.
    private ConcurrentLinkedDeque<Integer> rcache;

    // The service that is used to obtain blocks of random numbers.
    private RandomNumberService randomNumberService;

    // If we start this Verticle, do we want it to obtain a block of random numbers immediately?
    private boolean getBlockUponInit = false;
    // The threshold at which this service requests a new batch of random numbers.
    private Integer minThreshHold;
    // The amount of numbers a caller can request at once.
    private Integer maxNrsPerRequest;
    // The highest possible value in the block of random numbers
    private Integer highestNumber;
    // The lowest possible value in the block of random numbers
    private Integer lowestNumber;
    // The amount of random numbers we request per call to the random service
    private Integer blockAmount;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        this.randomNumberService = RandomNumberService.createProxy(super.vertx);

        // Obtain the configuration with the threshold and the indicator for the get-block-on-init
        JsonObject config = context.config().getJsonObject("random_number_service");
        if (Objects.nonNull(config)) {
            this.getBlockUponInit = Optional.ofNullable(config.getBoolean("initial_block_on_start")).orElse(false);
            this.minThreshHold = Optional.ofNullable(config.getInteger("min_threshold")).orElse(DEFAULT_MIN_THRESHOLD);
            this.maxNrsPerRequest = Optional.ofNullable(config.getInteger("max_nrs_per_request")).orElse(DEFAULT_MAX_NRS_PER_REQUEST);
            this.lowestNumber = Optional.ofNullable(config.getInteger("lowest_number")).orElse(DEFAULT_LOWEST_NUMBER);
            this.highestNumber = Optional.ofNullable(config.getInteger("highest_number")).orElse(DEFAULT_HIGHEST_NUMBER);
            this.blockAmount = Optional.ofNullable(config.getInteger("block_amount")).orElse(DEFAULT_BLOCK_AMOUNT);

        } else {
            LOGGER.warn("No configuration was found for the Random Number Verticle. Falling back to defaults.");
        }
        rcache = new ConcurrentLinkedDeque<>();

        LOGGER.info("Random number verticle initialized. \nUsing {} as the minimum amount of numbers that must be present in the cache before obtaining a new block. \nInitial request on start: {}; max amount per request: {}.\nRequesting {} of numbers when the threshold is reached.", minThreshHold, getBlockUponInit, maxNrsPerRequest, blockAmount);

        // TODO: if we had a block of cache previously, read it from storage and fill the cache with that list.
    }

    @Override
    public void start() {

        if (getBlockUponInit) {
            this.getNextBlock();
        }

        vertx.eventBus().<JsonObject>consumer("get.numbers", message -> {
            // TODO: expecting the message to contain a body with the "amount" of numbers that must be obtained but must handle this in case this is not present.
            Integer amount = message.body().getInteger("amount");
            if (amount > maxNrsPerRequest) {
                message.fail(500, "The amount of requested items is larger than allowed.");
            } else if (rcache.size() > amount) {
                // In this case, we are requesting more items than we have available.
                LOGGER.error("Amount of requested items ({}) is larger than we can handle (currently in stock: {}). Please increase the threshold to keep up with the amount of numbers that are requested.", amount, rcache.size());
                message.fail(500, "The amount of requested items is larger than we can handle at the moment.");
            } else {
                List<Integer> randomNumbers = new ArrayList<>(amount);
                while ((amount--) > 0) {
                    randomNumbers.add(rcache.pop());
                }
                message.reply(randomNumbers);

                this.getNextBlock();
            }

        });
    }

    // Obtain lock on shared data.
    // Get the map with random number service items.
    // If it doesn't exist, create it.
    // Test if the amount of requests is still enough to get a new block of numbers.
    // Else replace the configuration items with the ones from the response.
    // TODO: maintain the amount of requests and bits we are allowed to request in the vertx shared data.
    // TODO: handle fetch errors and monitor the amount of numbers that are available after a failure.
    private void getNextBlock() {
        randomNumberService
                .rxGetRandomNumbers(blockAmount, lowestNumber, highestNumber).subscribe(dto -> {
                    if (this.rcache.addAll(dto.getData())) {
                        LOGGER.info("Obtained next block of random numbers. Cache now contains {} items.",
                                rcache.size());
                    } else {
                        LOGGER.error("Failed to add {} random numbers to cache.", dto.getData().size());
                    }
                },
                throwable -> LOGGER.error("Unable to obtain next block of numbers.", throwable));
    }
}
