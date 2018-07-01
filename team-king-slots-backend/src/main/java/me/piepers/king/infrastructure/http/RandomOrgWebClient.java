package me.piepers.king.infrastructure.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceException;

import java.util.UUID;

/**
 * Connects to the random.org web service to obtain random numbers. Expects the api key to be present on the vertx
 * config. Uses {@link io.vertx.reactivex.ext.web.client.WebClient} to connect to the actual service. Note: this
 * webclient is not responsible for keeping track of how many requests are still allowed by this service.
 *
 * @author Bas Piepers
 */
public class RandomOrgWebClient {

    private final String apiKey;
    //    private final WebClient webClient;
    private final static String baseUrl = "/json-rpc/1/invoke";
    private final Vertx vertx;

    public RandomOrgWebClient(Vertx vertx) {
        this.apiKey = vertx.getOrCreateContext().config().getJsonObject("random_number_client").getString("api_key");
//        this.webClient = WebClient.create(vertx, new WebClientOptions().setSsl(true).setMaxPoolSize(HttpClientOptions.DEFAULT_MAX_POOL_SIZE));
        this.vertx = vertx;
    }

    /**
     * Calls the random.org api to obtain random numbers, typically used to acquire a block of random numbers.
     *
     * @param lowest,        the lowest value of the random numbers.
     * @param highest,       the highest value of the random numbers.
     * @param amount,        the amount of random numbers.
     * @param resultHandler, the resultHandler returns the response of the service as a JsonObject. Note that the
     *                       response code of the service can still be 200 (Ok) while the JsonObject contains an
     *                       "error".
     */
    public void getRandomNumbers(Integer lowest, Integer highest, Integer amount, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject payLoad = new JsonObject();
        payLoad.put("jsonRpc", "2.0");
        WebClient webClient = WebClient.create(this.vertx);
        webClient
                .post(443, "api.random.org", this.baseUrl)
                .ssl(true)
                .rxSendJsonObject(this.generatePostJsonObject(lowest, highest, amount))
                .doFinally(webClient::close)
                .subscribe(response -> {
                    if (response.statusCode() == 200) {
                        resultHandler.handle(Future.succeededFuture(response.bodyAsJsonObject()));
                    } else {
                        resultHandler.handle(ServiceException.fail(response.statusCode(), response.statusMessage()));
                    }

                }, throwable -> resultHandler.handle(Future.failedFuture(throwable)));
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
}
