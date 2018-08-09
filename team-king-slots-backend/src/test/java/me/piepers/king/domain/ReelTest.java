package me.piepers.king.domain;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReelTest {

    @Test
    public void test_that_2d_json_is_mapped_as_expected() {
        String json = "{\"cells\":[" +
                "[1,2,3]," +
                "[3,4,5]," +
                "[6,7,8]" +
                "]}";
        JsonObject jsonObject = new JsonObject(json);
        Reel reel = new Reel(jsonObject);
        assertThat(reel.getCells().size()).isEqualTo(3);
        assertThat(reel.getCells().get(0)).isNotNull();
        assertThat(reel.getCells().get(0).size()).isEqualTo(3);
        assertThat(reel.getCells().get(1)).isNotNull();
        assertThat(reel.getCells().get(1).size()).isEqualTo(3);
        assertThat(reel.getCells().get(2)).isNotNull();
        assertThat(reel.getCells().get(2).size()).isEqualTo(3);
        int[][] expectedValues = {{1, 2, 3}, {3, 4, 5}, {6, 7, 8}};
        assertExpectedValues(reel, expectedValues);
        assertThat(reel.getCellAmount()).isEqualTo(9);
    }

    @Test
    public void test_that_uneven_rows_are_still_mapped_correctly() {
        String json = "{\"cells\":[" +
                "[20,31]," +
                "[3,4,5,6]," +
                "[18,19,20,21,22]" +
                "]}";
        JsonObject jsonObject = new JsonObject(json);
        Reel reel = new Reel(jsonObject);
        assertThat(reel.getCells().size()).isEqualTo(3);
        assertThat(reel.getCells().get(0)).isNotNull();
        assertThat(reel.getCells().get(0).size()).isEqualTo(2);
        assertThat(reel.getCells().get(1)).isNotNull();
        assertThat(reel.getCells().get(1).size()).isEqualTo(4);
        assertThat(reel.getCells().get(2)).isNotNull();
        assertThat(reel.getCells().get(2).size()).isEqualTo(5);
        int[][] expectedValues = {{20, 31}, {3, 4, 5, 6}, {18, 19, 20, 21, 22}};
        assertExpectedValues(reel, expectedValues);

        assertThat(reel.getCellAmount()).isEqualTo(11);

    }

    @Test
    public void test_that_when_of_is_called_with_rows_and_columns_that_expected_reel_is_created() {
        Reel reel = Reel.of(3, 4);
        assertThat(reel).isNotNull();
        assertThat(reel.getCells()).isNotNull();
        assertThat(reel.getCells().size()).isEqualTo(3);
        assertThat(reel.getCells().get(0)).isNotNull();
        assertThat(reel.getCells().get(1)).isNotNull();
        assertThat(reel.getCells().get(2)).isNotNull();
        assertThat(reel.getCells().get(0).get(0).getValue()).isEqualTo(0);
        assertThat(reel.getCells().get(0).get(1).getValue()).isEqualTo(0);
        assertThat(reel.getCells().get(0).get(2).getValue()).isEqualTo(0);
        assertThat(reel.getCells().get(0).get(3).getValue()).isEqualTo(0);
        assertThatThrownBy(() -> reel.getCells().get(0).get(4)).isExactlyInstanceOf(ArrayIndexOutOfBoundsException.class);

        assertThatThrownBy(() -> reel.getCells().get(3)).isExactlyInstanceOf(IndexOutOfBoundsException.class);
        String expectedJson = "{\"cells\":[[0,0,0,0],[0,0,0,0],[0,0,0,0]]}";
        assertThat(reel.toJson().encode()).isEqualTo(expectedJson);
    }

    @Test
    public void test_that_paylines_are_mapped_as_expected() {
        String json = "{\n" +
                "\"cells\": [\n" +
                "[0, 0, 0, 0, 0],\n" +
                "[0, 0, 0, 0, 0],\n" +
                "[0, 0, 0, 0, 0]\n" +
                "],\n" +
                "\"payLines\": [{\n" +
                "\"reference\": 1,\n" +
                "\"coordinates\": [0, 0, 0, 0, 0],\n" +
                "\"active\": true,\n" +
                "\"bet\": 1\n" +
                "}, {\n" +
                "\"reference\": 2,\n" +
                "\"coordinates\": [0, 1, 3, 4, 1],\n" +
                "\"active\": false,\n" +
                "\"bet\": 1\n" +
                "}]\n" +
                "}";

        Reel reel = new Reel(new JsonObject(json));
        assertThat(reel.getCells().size()).isEqualTo(3);
        assertThat(reel.getPayLines().size()).isEqualTo(2);
    }

    @Test
    public void test_that_payline_must_span_entire_row() {
        Reel reel = Reel.of(4, 5);

        assertThatThrownBy(() -> reel.addPayline(1, new Integer[]{1, 2, 1})).isExactlyInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reel.addPayline(1, new Integer[]{1,1,1,1,1,2}));
    }

    @Test
    public void test_that_duplicate_payline_references_are_not_allowed() {

        Reel reel = Reel.of(3, 3);

        reel.addPayline(1, new Integer[]{1, 1, 1});
        assertThatThrownBy(() -> reel.addPayline(1, new Integer[]{1, 2, 3})).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_that_duplicate_payline_coordinates_are_not_allowed() {
        Reel reel = Reel.of(3, 6);

        reel.addPayline(1, new Integer[]{1, 2, 3, 2, 2, 1});
        assertThatThrownBy(() -> reel.addPayline(2, new Integer[]{1, 2, 3, 2, 2, 1})).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    private void assertExpectedValues(Reel reel, int[][] expectedValues) {
        assertThat(reel.getCells()).isNotNull();
        for (int i = 0; i < expectedValues.length; i++) {
            int[] row = expectedValues[i];
            for (int j = 0; j < row.length; j++) {
                assertThat(reel.getCells().get(i).get(j)).isEqualToComparingOnlyGivenFields(ReelCell.of(row[j]), "value");
            }
        }
    }
}