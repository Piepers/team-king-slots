package me.piepers.king.application;

import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

/**
 * Unit test for the {@link RandomNumberVerticle}
 *
 * @author Bas Piepers
 */
@Ignore
@RunWith(VertxUnitRunner.class)
public class RandomNumberVerticleTest {

    private Vertx vertx;
    private RandomNumberVerticle verticle;

    @Before
    public void prepare(TestContext context) {
        this.vertx = Vertx.vertx();
    }

    @After
    public void finish(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test_what_happens_with_shared_map(TestContext context) {
        LocalMap<String, Long> sharedMap = this.vertx.getDelegate().sharedData().getLocalMap("test");
        context.assertTrue(Objects.nonNull(sharedMap));
        Long value = sharedMap.put("test", 1L);
        context.assertTrue(Objects.isNull(value));
        value = sharedMap.put("test", 2L);
        context.assertTrue(value.equals(1L));
    }
}
