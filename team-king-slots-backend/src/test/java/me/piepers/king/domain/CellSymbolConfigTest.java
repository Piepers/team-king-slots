package me.piepers.king.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A small test to experiment with mapping this Data Object
 *
 * @author Bas Piepers
 */

public class CellSymbolConfigTest {

    @Test
    public void test_that_range_is_created_as_expected() {
        // Instantiate a config of a specific symbol and assign a score to it (three, four and five symbols represent a score of 100, 200 and 250
        CellSymbolConfig csc = CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, 1, 20, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE}, new Integer[]{100, 200, 250}));
        assertThat(csc.getNumberAssignment().size()).isEqualTo(19);

        // FIXME: how to express the fact that a list is a sequence of numbers?
        assertThat(csc.getNumberAssignment().stream().sorted().mapToInt(Integer::valueOf).sum()).isEqualTo(190);
    }
}
