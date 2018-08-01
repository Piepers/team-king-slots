package me.piepers.king.infrastructure;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * An implementation of the random service that uses the random.org service to obtain real random numbers. Inspects
 * responses to verify if a next request might be problematic. Maintains this in the sharedData of Vertx to prevent
 * superfluous querying of the data.
 *
 * @author Bas Piepers
 */
// TODO: make unit-testable and write unit tests.
public class RandomOrgNumberServiceImpl implements RandomNumberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomOrgNumberServiceImpl.class);

    private final static String BASE_URL = "/json-rpc/1/invoke";
    private final String apiKey;
    private final io.vertx.reactivex.core.Vertx rxVertx;

    public RandomOrgNumberServiceImpl(Vertx vertx, JsonObject configuration) throws Exception {
        this.rxVertx = new io.vertx.reactivex.core.Vertx(vertx);

        // Fixme: why am I not getting the configuration I added to the context earlier?
        LOGGER.debug("The config in the service is: " + vertx.getOrCreateContext().config());

        JsonObject randomNrClientConfig = Optional.ofNullable(configuration.getJsonObject("random_number_client"))
                .orElseThrow(() -> new Exception("This service requires specific configuration to start."));

        this.apiKey = Optional
                .ofNullable(randomNrClientConfig.getString("api_key"))
                .orElseThrow(() -> new Exception("This service requires an api_key configuration item to be present in the provided configuration object"));

    }

    @Override
    public void getRandomNumbers(Integer amount, Integer min, Integer max, Handler<AsyncResult<RandomNumberResponseDto>> resultHandler) {
        LOGGER.debug("Requesting {} of numbers with a lowest value of {} and a highest value of {}", amount, min, max);
        JsonObject payLoad = new JsonObject();
        payLoad.put("jsonRpc", "2.0");
        WebClient webClient = WebClient.create(this.rxVertx);
        webClient
                .post(443, "api.random.org", BASE_URL)
                .ssl(true)
                .rxSendJsonObject(this.generatePostJsonObject(min, max, amount))
                .doFinally(webClient::close)
                .subscribe(response -> {
                    if (response.statusCode() == 200) {
                        LOGGER.debug("Retrieved valid response from random.org for {} items.", amount);
                        RandomNumberResponseDto result = this.generateResultFromResponse(response.bodyAsJsonObject());
                        LOGGER.debug("Generated result out of response: {}", result.toString());
                        resultHandler.handle(Future.succeededFuture(result));
                    } else {
                        // TODO: code 402 or 403 codes indicate that not enough requests or bits are left to generate numbers.
                        LOGGER.error("Failure was retrieved while trying to get a response: {}|{}", response.statusCode(), response.statusMessage());
                        resultHandler.handle(ServiceException.fail(response.statusCode(), response.statusMessage()));
                    }

                }, throwable -> {
                    LOGGER.error("Failure while generating a response result({}).", throwable.getMessage());
                    resultHandler.handle(Future.failedFuture(throwable));
                });
    }

    private JsonObject generatePostJsonObject(Integer lowest, Integer highest, Integer amount) {
        JsonObject payLoad = new JsonObject();
        payLoad.put("jsonrpc", "2.0");
        payLoad.put("method", "generateIntegers");
        JsonObject params = new JsonObject();
        params.put("apiKey", this.apiKey);
        params.put("n", amount);
        params.put("min", lowest);
        params.put("max", highest);
        payLoad.put("params", params);
        payLoad.put("id", UUID.randomUUID().toString());
        return payLoad;
    }

    private RandomNumberResponseDto generateResultFromResponse(JsonObject jsonObject) {
//        LOGGER.debug(jsonObject.encodePrettily());
        JsonObject result = Optional.ofNullable(jsonObject.getJsonObject("result")).orElseThrow(this::unexpectedContentException);
        JsonObject random = Optional.ofNullable(result.getJsonObject("random")).orElseThrow(this::unexpectedContentException);
        JsonArray data = Optional.ofNullable(random.getJsonArray("data")).orElseThrow(this::unexpectedContentException);

        // FIXME: since we know these are integers, can we do something to prevent having to cast?
        List<Integer> numbers = data.stream().map(i -> (Integer) i).collect(Collectors.toList());
        Integer bitsUsed = result.getInteger("bitsUsed");
        Integer bitsLeft = result.getInteger("bitsLeft");
        Integer requestsLeft = result.getInteger("requestsLeft");
        String id = jsonObject.getString("id");

        return new RandomNumberResponseDto(id, bitsUsed, bitsLeft, requestsLeft, numbers);
    }

    private ServiceException unexpectedContentException() {
        return new ServiceException(500, "Unexpected content in the response.");
    }
}
