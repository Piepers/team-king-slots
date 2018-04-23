package me.piepers.king;

import io.reactivex.Completable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;
import me.piepers.king.application.HttpServerVerticle;
import me.piepers.king.domain.SlotService;
import me.piepers.king.infrastructure.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Entry point for the slots backend application.
 *
 * @author Bas Piepers
 */
public class TeamKingSlotsApplication extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamKingSlotsApplication.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // The main configuration of the application from the configuration file.
        final ConfigStoreOptions configStore = new ConfigStoreOptions().setType("file")
                .setConfig(new JsonObject().put("path", "app-conf.json"));
        final ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(configStore);

        final ConfigRetriever configRetriever = ConfigRetriever.create(this.vertx, options);

        configRetriever.rxGetConfig().flatMapCompletable(configuration -> {

                    // Register event bus services
                    new ServiceBinder(vertx.getDelegate())
                            .setAddress(SlotRepository.EVENT_BUS_ADDRESS)
                            .register(SlotRepository.class, SlotRepository.create(vertx.getDelegate()));
                    new ServiceBinder(vertx.getDelegate()).setAddress(SlotService.EVENT_BUS_ADDRESS)
                            .register(SlotService.class, SlotService.create(vertx.getDelegate()));

                    return Completable.fromAction(() -> LOGGER.debug("Deploying Team King Slot machine backend"))
//                    .andThen(this.vertx.rxDeployVerticle(SlotCommandHandlerVerticle.class.getName(), new DeploymentOptions().setConfig(configuration)))
//                    .toCompletable()
                            .andThen(this.vertx.rxDeployVerticle(HttpServerVerticle.class.getName(),
                                    new DeploymentOptions().setConfig(configuration)))
                            .toCompletable();
                }
        ).subscribe(() -> {
            LOGGER.info("King Slots application has been deployed successfully.");
            startFuture.complete();
        }, throwable -> {
            LOGGER.error("King Slots application has not been deployed due to: ", throwable);
            startFuture.fail(throwable);
        });
    }
}
