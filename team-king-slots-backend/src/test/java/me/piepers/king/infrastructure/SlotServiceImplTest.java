package me.piepers.king.infrastructure;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import me.piepers.king.reactivex.domain.SlotService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.ws.Service;

@RunWith(VertxUnitRunner.class)
public class SlotServiceImplTest {
    private Vertx vertx;
    private SlotService service;

    @Before
    public void prepare(TestContext context) {
        this.vertx = Vertx.vertx();
        this.service = SlotService.create(vertx);
        // TODO: find a way to mock the repository to prevent this kind of integration test behavior
        new ServiceBinder(vertx.getDelegate())
                .setAddress(SlotRepository.EVENT_BUS_ADDRESS)
                .register(SlotRepository.class, SlotRepository.create(vertx.getDelegate()));
    }

    @After
    public void finish(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test_that_when_slot_stop_while_slot_not_spinning_that_result_is_failure(TestContext context) {
        Async async = context.async();

        service
                .rxStart()
                .flatMap(slotId -> service
                        .rxStop(slotId.getId()))
                .doAfterTerminate(async::complete)
                .subscribe(slot ->  context.fail("Did not expect a successful return"),
                        throwable -> context.assertTrue(throwable instanceof ServiceException));
    }
}
