package me.piepers.king.domain;

import java.util.Objects;

/**
 * Represents how much symbols in a row represent a value. To limit the values that are possible.
 *
 * @author Bas Piepers
 */
public enum SubsequentSymbols {
    THREE(3), FOUR(4), FIVE(5);

    private int amount;

    SubsequentSymbols(int amount) {
        this.amount = amount;
    }

    public static SubsequentSymbols resolve(final String value) {
        final String localValue = value.toUpperCase();
        switch (localValue) {
            case "THREE":
                return SubsequentSymbols.THREE;
            case "FOUR":
                return SubsequentSymbols.FOUR;
            case "FIVE":
                return SubsequentSymbols.FIVE;
            default:
                throw new IllegalArgumentException("Unable to map " + (Objects.nonNull(value) ? value : ""));
        }
    }

    public int getAmount() {
        return amount;
    }
}
