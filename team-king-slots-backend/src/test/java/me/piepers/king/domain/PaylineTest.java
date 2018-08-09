package me.piepers.king.domain;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Bas Piepers
 */

public class PaylineTest {
    @Test
    public void test_that_when_coordinates_are_given_that_they_are_mapped_as_expected() {
        String json = "{\n" +
                "  \"reference\":1,\n" +
                "  \"coordinates\":[0,1,2,1,0],\n" +
                "  \"active\":true,\n" +
                "  \"bet\":1\n" +
                "}";
        JsonObject jsonObject = new JsonObject(json);

        Payline payline = new Payline(jsonObject);

        assertThat(payline.getCoordinates()).isNotEmpty();
        assertThat(payline.getCoordinates().size()).isEqualTo(5);
        assertThat(payline.getReference()).isEqualTo(1);
        assertThat(payline.isActive()).isTrue();
        assertThat(payline.getCoordinates()).containsExactly(0, 1, 2, 1, 0);
    }

    @Test
    public void test_that_when_empty_coordinates_are_given_that_exception_is_thrown() {
        // TODO: might want to test the message as well.
        assertThrows(IllegalArgumentException.class, () -> Payline.of(1, null, 1));
    }
}
