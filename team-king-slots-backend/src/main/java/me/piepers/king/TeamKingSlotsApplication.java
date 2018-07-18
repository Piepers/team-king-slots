package me.piepers.king;

import io.reactivex.Completable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;
import me.piepers.king.application.HttpServerVerticle;
import me.piepers.king.application.RandomNumberVerticle;
import me.piepers.king.domain.SlotService;
import me.piepers.king.infrastructure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * Entry point for the slots backend application.
 *
 * @author Bas Piepers
 */
public class TeamKingSlotsApplication extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamKingSlotsApplication.class);

    @Override
    public void start(Future<Void> startFuture) {

        // Determine in which profile this application runs based on the start variables. Takes local as the default.
        final ConfigStoreOptions envStore = new ConfigStoreOptions().setType("env");

        // The main configuration of the application from the configuration file.
        final ConfigStoreOptions mainConfigStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "config/app-conf.json"));

        // The non-public config store for items like api-keys and other stuff that we do not commit to a public scm
        final ConfigStoreOptions nonPublicConfigStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "config/non-public-conf.json"));

        final ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(nonPublicConfigStore)
                .addStore(mainConfigStore)
                .addStore(envStore);

        final ConfigRetriever configRetriever = ConfigRetriever.create(this.vertx, options);

        configRetriever
                .rxGetConfig()
                .flatMapCompletable(configuration -> {

                            ApplicationProfile profile = ApplicationProfile
                                    .resolve(Optional.ofNullable(configuration.getString("profile"))
                                            .orElse("local"));

                            LOGGER.info("Using profile: {}", profile.getName());

                            // Register event bus services
                            new ServiceBinder(vertx.getDelegate())
                                    .setAddress(SlotRepository.EVENT_BUS_ADDRESS)
                                    .register(SlotRepository.class, SlotRepository.create(vertx.getDelegate()));
                            new ServiceBinder(vertx.getDelegate()).setAddress(SlotService.EVENT_BUS_ADDRESS)
                                    .register(SlotService.class, SlotService.create(vertx.getDelegate()));
                            new ServiceBinder(vertx.getDelegate()).setAddress(RandomNumberService.EVENT_BUS_ADDRESS)
                                    // The profile determines which random number service to choose. Always falls back to local.
                                    .register(RandomNumberService.class, profile == ApplicationProfile.LOCAL ?
                                            new LocalRandomNumberServiceImpl() :
                                            new RandomOrgNumberServiceImpl(vertx.getDelegate(), configuration));

                            return Completable.fromAction(() -> LOGGER.debug("Deploying Team King Slot machine backend"))
//                    .andThen(this.vertx.rxDeployVerticle(SlotCommandHandlerVerticle.class.getName(), new DeploymentOptions().setConfig(configuration)))
//                    .toCompletable()
                                    .andThen(this.vertx.rxDeployVerticle(HttpServerVerticle.class.getName(),
                                            new DeploymentOptions().setConfig(configuration)))
                                    .toCompletable()
                                    .andThen(this.vertx.rxDeployVerticle(RandomNumberVerticle.class.getName(),
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
