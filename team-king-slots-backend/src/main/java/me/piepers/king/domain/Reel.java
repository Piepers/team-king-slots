package me.piepers.king.domain;

import io.vertx.codegen.annotations.DataObject;

/**
 * The Reel class represents the reels part of a slot. The name is somewhat misleading as it is not one reel but the
 * collection of cells on the reels of a slot. It represents a Grid of cells and the cells represent an image on the
 * reel which is randomly chosen. The main purpose of this class is to store the cells a slot has and offers a way to
 * calculate how many random numbers we need to request for one spin.
 *
 * @author Bas Piepers
 *
 */

public class Reel {

    private final ReelCell[][] cells;

    public Reel(int rows, int columns) {
        cells = new ReelCell[rows][columns];
    }


    /**
     * To be able to ascertain how many random numbers we need to collect.
     *
     * @return the amount of cells we have for this reel.
     *
     */
    public int getCellAmount() {
       return cells.length;
    }
}
