package me.piepers.king.infrastructure;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import me.piepers.king.reactivex.infrastructure.RandomNumberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class LocalRandomNumberServiceImplTest {

    private Vertx vertx;
    private RandomNumberService randomNumberService;

    @BeforeEach
    public void prepare() {
        this.vertx = Vertx.vertx();
        this.randomNumberService = RandomNumberService.create(this.vertx);
    }

    @AfterEach
    public void finish() {
        this.vertx.close();
    }

    @Test
    public void test_that_when_generate_local_numbers_that_list_is_generated(VertxTestContext context) {
        this.randomNumberService
                .rxGetRandomNumbers(10, 0, 100)
                .doFinally(() -> context.completeNow())
                .subscribe(result -> assertThat(result.getData().size() == 10),
                        throwable -> context.failed());
    }
}
