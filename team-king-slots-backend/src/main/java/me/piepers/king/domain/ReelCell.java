package me.piepers.king.domain;

/**
 * One cell on a reel of the slot machine. The {@link Payline} uses this to determine if cells represent a win. Cells
 * are also stored on a {@link Reel} because a Reel maintains these cells.
 *
 * @author Bas Piepers
 */
public class ReelCell implements JsonDomainObject {

    private int value;

    public ReelCell(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
