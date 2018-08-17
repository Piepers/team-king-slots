package me.piepers.king.domain;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Some simple tests for the {@link ReelConfig} class.
 *
 * @author Bas Piepers
 */
public class ReelConfigTest {

    @Test
    public void test_that_instantiating_with_invalid_range_throws_expected_exception() {
        assertThatThrownBy(() -> ReelConfig.of(100, 1)).isExactlyInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ReelConfig.of(-1, 50)).isExactlyInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void test_that_adding_to_empty_config_works_as_expected() {
        ReelConfig rc = ReelConfig.of(1, 100);
        CellSymbolConfig cs1 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        CellSymbolConfig cs2 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 3, 4, 5, 6, 7, 8, 9, 10);
        rc.addCellConfig(cs1).addCellConfig(cs2);
        assertThat(rc.getCellConfig().size()).isEqualTo(2);
        // Not all numbers are assigned yet.
        assertThat(rc.isValid()).isFalse();
    }

    @Test
    public void test_that_adding_to_config_that_already_has_the_numbers_assigned_throws_expected_exception_overlap() {
        ReelConfig rc = ReelConfig.of(1, 100);
        CellSymbolConfig cs1 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        CellSymbolConfig cs2 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertThatThrownBy(() -> rc.addCellConfig(cs1).addCellConfig(cs2)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_that_adding_to_config_that_already_has_the_numbers_assigned_throws_expected_exception_identical() {
        ReelConfig rc = ReelConfig.of(1, 100);
        CellSymbolConfig cs1 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        CellSymbolConfig cs2 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        assertThatThrownBy(() -> rc.addCellConfig(cs1).addCellConfig(cs2)).isExactlyInstanceOf(IllegalArgumentException.class);
        // Not all numbers are assigned yet.
        assertThat(rc.isValid()).isFalse();
    }

    @Test
    public void test_that_config_is_valid_when_entire_range_is_assigned() {
        ReelConfig rc = ReelConfig.of(1, 10);

        CellSymbolConfig cs1 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        CellSymbolConfig cs2 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 2);
        CellSymbolConfig cs3 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, 3, 11, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}));
        rc.addCellConfig(cs1).addCellConfig(cs2).addCellConfig(cs3);

        assertThat(rc.isValid()).isTrue();
    }

    @Test
    public void test_that_when_number_is_not_present_that_it_can_not_be_mapped_to_a_symbol() {
        ReelConfig rc = ReelConfig.of(1, 10);
        CellSymbolConfig cs1 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        CellSymbolConfig cs2 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 2);
        CellSymbolConfig cs3 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, 3, 11, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}));
        rc.addCellConfig(cs1).addCellConfig(cs2).addCellConfig(cs3);

        assertThatThrownBy(() -> rc.getSymbolByNumber(-1)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_that_when_number_is_present_that_it_is_mapped_to_a_symbol() {
        ReelConfig rc = ReelConfig.of(1, 10);
        CellSymbolConfig cs1 = CellSymbolConfig.of(CellSymbolConfig.Symbol.CHERRY, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 1);
        CellSymbolConfig cs2 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BAR, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}), 2);
        CellSymbolConfig cs3 = CellSymbolConfig.of(CellSymbolConfig.Symbol.BELL, 3, 11, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE}, new Integer[]{1}));
        rc.addCellConfig(cs1).addCellConfig(cs2).addCellConfig(cs3);

        assertThat(rc.getSymbolByNumber(1)).isEqualByComparingTo(CellSymbolConfig.Symbol.CHERRY);
        assertThat(rc.getSymbolByNumber(2)).isEqualByComparingTo(CellSymbolConfig.Symbol.BAR);
        assertThat(rc.getSymbolByNumber(9)).isEqualByComparingTo(CellSymbolConfig.Symbol.BELL);
    }

    @Test
    public void test_that_mapping_of_json_is_as_expected() {
        JsonObject jsonObject = new JsonObject(this.generateDefaultValidReelConfig());

        ReelConfig rc = new ReelConfig(jsonObject);

        assertThat(rc).isNotNull();
        assertThat(rc.getCellConfig()).isNotNull();
        assertThat(rc.getCellConfig().size()).isEqualTo(10);
        assertThat(rc.getSymbolByNumber(1)).isEqualByComparingTo(CellSymbolConfig.Symbol.SEVEN);
    }

    private String generateDefaultValidReelConfig() {
       return "{\n" +
               "  \"from\": 1,\n" +
               "  \"to\": 100,\n" +
               "  \"cellConfig\": [\n" +
               "    {\n" +
               "      \"symbol\": \"SEVEN\",\n" +
               "      \"numberAssignment\": [\n" +
               "        1,\n" +
               "        2,\n" +
               "        3,\n" +
               "        4,\n" +
               "        5,\n" +
               "        6,\n" +
               "        7,\n" +
               "        8,\n" +
               "        9,\n" +
               "        10,\n" +
               "        11,\n" +
               "        12\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 50,\n" +
               "        \"THREE\": 100\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"TWO_SEVENS\",\n" +
               "      \"numberAssignment\": [\n" +
               "        13,\n" +
               "        14,\n" +
               "        15,\n" +
               "        16,\n" +
               "        17,\n" +
               "        18,\n" +
               "        19,\n" +
               "        20,\n" +
               "        21,\n" +
               "        22\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 80,\n" +
               "        \"THREE\": 160\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"THREE_SEVENS\",\n" +
               "      \"numberAssignment\": [\n" +
               "        23,\n" +
               "        24,\n" +
               "        25\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 100,\n" +
               "        \"THREE\": 200\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"CHERRY\",\n" +
               "      \"numberAssignment\": [\n" +
               "        26,\n" +
               "        27,\n" +
               "        28,\n" +
               "        29,\n" +
               "        30,\n" +
               "        31,\n" +
               "        32,\n" +
               "        33,\n" +
               "        34,\n" +
               "        35,\n" +
               "        36,\n" +
               "        37,\n" +
               "        38,\n" +
               "        39\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 40,\n" +
               "        \"THREE\": 80\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"TWO_CHERRIES\",\n" +
               "      \"numberAssignment\": [\n" +
               "        40,\n" +
               "        41,\n" +
               "        42,\n" +
               "        43,\n" +
               "        44,\n" +
               "        45,\n" +
               "        46,\n" +
               "        47,\n" +
               "        48\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 90,\n" +
               "        \"THREE\": 180\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"THREE_CHERRIES\",\n" +
               "      \"numberAssignment\": [\n" +
               "        49,\n" +
               "        50\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 120,\n" +
               "        \"THREE\": 240\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"BELL\",\n" +
               "      \"numberAssignment\": [\n" +
               "        51,\n" +
               "        52,\n" +
               "        53,\n" +
               "        54,\n" +
               "        55,\n" +
               "        56,\n" +
               "        57,\n" +
               "        58,\n" +
               "        59,\n" +
               "        60,\n" +
               "        61,\n" +
               "        62,\n" +
               "        63\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 45,\n" +
               "        \"THREE\": 90\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"TWO_BELLS\",\n" +
               "      \"numberAssignment\": [\n" +
               "        64,\n" +
               "        65,\n" +
               "        66,\n" +
               "        67,\n" +
               "        68,\n" +
               "        69,\n" +
               "        70,\n" +
               "        71,\n" +
               "        72,\n" +
               "        73,\n" +
               "        74\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 95,\n" +
               "        \"THREE\": 190\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"THREE_BELLS\",\n" +
               "      \"numberAssignment\": [\n" +
               "        75\n" +
               "      ],\n" +
               "      \"scoreValues\": {\n" +
               "        \"TWO\": 130,\n" +
               "        \"THREE\": 260\n" +
               "      }\n" +
               "    },\n" +
               "    {\n" +
               "      \"symbol\": \"EMPTY\",\n" +
               "      \"numberAssignment\": [\n" +
               "        76,\n" +
               "        77,\n" +
               "        78,\n" +
               "        79,\n" +
               "        80,\n" +
               "        81,\n" +
               "        82,\n" +
               "        83,\n" +
               "        84,\n" +
               "        85,\n" +
               "        86,\n" +
               "        87,\n" +
               "        88,\n" +
               "        89,\n" +
               "        90,\n" +
               "        91,\n" +
               "        92,\n" +
               "        93,\n" +
               "        94,\n" +
               "        95,\n" +
               "        96,\n" +
               "        97,\n" +
               "        98,\n" +
               "        99,\n" +
               "        100\n" +
               "      ]\n" +
               "    }\n" +
               "  ]\n" +
               "}";
    }
}
