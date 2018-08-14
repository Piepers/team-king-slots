package me.piepers.king.domain;

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
}
