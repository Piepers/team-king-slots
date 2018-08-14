package me.piepers.king.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A small test to experiment with mapping this Data Object
 *
 * @author Bas Piepers
 */

public class CellSymbolConfigTest {

    @Test
    public void test_that_when_from_and_to_are_not_valid_that_exception_is_thrown() {
        Map<SubsequentSymbols, ScoreValue> scoreConfig = CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE}, new Integer[]{100, 200, 250});
        assertThatThrownBy(() -> CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, 100, 1, scoreConfig)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_that_when_non_unique_numbers_are_used_that_exception_is_thrown() {
        assertThatThrownBy(() -> CellSymbolConfig
                .of(CellSymbolConfig.Symbol.SEVEN,
                        CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE},
                                new Integer[]{100, 200, 250}),
                        1, 2, 2, 3, 3, 4)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_that_when_no_numbers_for_array_are_given_that_exception_is_thrown() {
        assertThatThrownBy(() -> CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE}, new Integer[]{100, 200, 250}))).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_that_when_valid_numbers_are_given_that_this_is_instantiated_as_expected_with_range() {
        // Instantiate a config of a specific symbol and assign a score to it (three, four and five symbols represent a score of 100, 200 and 250)
        CellSymbolConfig csc = CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, 1, 20, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE}, new Integer[]{100, 200, 250}));
        assertThat(csc.getNumberAssignment().size()).isEqualTo(19);

        // FIXME: how to express the fact that a list is a sequence of numbers?
        assertThat(csc.getNumberAssignment().stream().sorted().mapToInt(Integer::valueOf).sum()).isEqualTo(190);
    }

    @Test
    public void test_that_when_valid_numbers_are_given_that_this_is_instantiated_as_expected_with_array_of_numbers() {
        CellSymbolConfig csc = CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE}, new Integer[]{100, 200, 250}), 1, 2, 3);

        assertThat(csc.getNumberAssignment().size()).isEqualTo(3);
        assertThat(csc.getNumberAssignment()).containsExactly(1, 2, 3);
    }

    @Test
    public void test_that_when_valid_numbers_are_given_that_this_is_instantiated_as_expected_with_one_number() {
        CellSymbolConfig csc = CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.THREE, SubsequentSymbols.FOUR, SubsequentSymbols.FIVE}, new Integer[]{100, 200, 250}), 100);

        assertThat(csc.getNumberAssignment().size()).isEqualTo(1);
        assertThat(csc.getNumberAssignment()).containsExactly(100);
    }
}
