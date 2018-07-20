package me.piepers.king.application;

import io.vertx.core.shareddata.LocalMap;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for the {@link RandomNumberVerticle}
 *
 * @author Bas Piepers
 */
@Disabled
@ExtendWith(VertxExtension.class)
public class RandomNumberVerticleTest {

    private Vertx vertx;
    private RandomNumberVerticle verticle;

    @BeforeEach
    public void prepare() {
        this.vertx = Vertx.vertx();
    }

    @AfterEach
    public void finish() {
        vertx.close();
    }

    @Test
    public void test_what_happens_with_shared_map(VertxTestContext context) {
        LocalMap<String, Long> sharedMap = this.vertx.getDelegate().sharedData().getLocalMap("test");
        assertThat(sharedMap).isNotNull();
        Long value = sharedMap.put("test", 1L);
        assertThat(value).isNull();
        value = sharedMap.put("test", 2L);
        assertThat(value).isEqualTo(1L);

    }
}
