package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * One cell on a reel of the slot machine. The {@link Payline} uses this to determine if cells represent a win. Cells
 * are also stored on a {@link Reel} because a Reel maintains these cells.
 *
 * @author Bas Piepers
 */
// TODO: value has a 0 value in case this cell represents an empty cell (to accommodate for variable lengths for columns).
public class ReelCell {
    @JsonValue
    private int value;

    private ReelCell(int value) {
        this.value = value;
    }

    public static ReelCell of(int value) {
        return new ReelCell(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "ReelCell{" +
                "value=" + value +
                '}';
    }
}
