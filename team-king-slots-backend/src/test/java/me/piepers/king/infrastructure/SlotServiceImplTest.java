package me.piepers.king.infrastructure;

import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import me.piepers.king.reactivex.domain.SlotService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class SlotServiceImplTest {
    private Vertx vertx;
    private SlotService service;

    @BeforeEach
    public void prepare() {
        this.vertx = Vertx.vertx();
        this.service = SlotService.create(vertx);
        // TODO: find a way to mock the repository to prevent this kind of integration test behavior
        new ServiceBinder(vertx.getDelegate())
                .setAddress(SlotRepository.EVENT_BUS_ADDRESS)
                .register(SlotRepository.class, SlotRepository.create(vertx.getDelegate()));
    }

    @AfterEach
    public void finish() {
        vertx.close();
    }

    @Test
    public void test_that_when_slot_stop_while_slot_not_spinning_that_result_is_failure(VertxTestContext context) {
        Checkpoint serviceCallCheckpoint = context.checkpoint();
        service
                .rxStart()
                .flatMap(slotId -> service
                        .rxStop(slotId.getId()))
                .subscribe(slot -> context.failing(id -> serviceCallCheckpoint.flag()),
                        throwable -> context.verify(() -> {
                            assertThat(throwable).isExactlyInstanceOf(ServiceException.class);
                            serviceCallCheckpoint.flag();
                        }));
    }
}
