package me.piepers.king.infrastructure;
;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import me.piepers.king.reactivex.infrastructure.RandomNumberService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

@RunWith(VertxUnitRunner.class)
public class LocalRandomNumberServiceImplTest {

    private Vertx vertx;
    private RandomNumberService randomNumberService;

    @Before
    public void prepare(TestContext context) {
        this.vertx = Vertx.vertx();
        this.randomNumberService = RandomNumberService.create(this.vertx);
    }

    @After
    public void finish(TestContext context) {
        this.vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test_that_when_generate_local_numbers_that_list_is_generated(TestContext context) {
        Async async = context.async();
        this.randomNumberService
                .rxGetRandomNumbers(10, 0, 100)
                .doAfterTerminate(async::complete)
                .subscribe(integers -> context.assertTrue(integers.size() == 10), throwable -> context.fail());
    }
}
