package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * One cell on a reel of the slot machine. The {@link Payline} uses this to determine if cells represent a win. Cells
 * are also stored on a {@link Reel} because a Reel maintains these cells.
 *
 * @author Bas Piepers
 */
// TODO: the value can be empty to accommodate for reels that have a varying amount of columns. So value needs to be changed to something that can represent that.
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
