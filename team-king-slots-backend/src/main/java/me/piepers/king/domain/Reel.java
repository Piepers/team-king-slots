package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * The Reel class represents the reels part of a slot. The name is somewhat misleading as it is not one reel but the
 * collection of cells on the reels of a slot. It represents a Grid of cells and the cells represent an image on the
 * reel which is randomly chosen. The main purpose of this class is to store the cells a slot has and offers a way to
 * calculate how many random numbers we need to request for one spin.
 * <p>
 * Currently the cells are represented by a 2D array which in principle for most slots is a array of cells where each
 * row contains the same amount of cells (columns).
 * <p>
 * It is, however, also possible that not each row in the cell has the same amount of columns for some slots. For now,
 * however, it offers only one constructor that creates a fixed symmetric grid of cells (with the given amount of rows
 * and columns).
 *
 * @author Bas Piepers
 */
@DataObject
public class Reel implements JsonDomainObject {
    List<List<ReelCell>> rows;
//    private final JsonArray rows;
//    private final ReelCell[][] cells;

    public Reel(JsonObject jsonObject) {
        JsonArray rows = jsonObject.getJsonArray("cells");
//        this.cells = new ReelCell[1][1];
//        this.cells = new ReelCell[rows.size()][];
//        rows.stream()
//        rows.stream().mapToInt(value -> )

//        this.cells = Arrays.jsonObject.getJsonArray("cells");
    }

    public Reel(int rows, int columns) {
//        cells = new ReelCell[rows][columns];

    }

    /**
     * To be able to ascertain how many cells we have to help ascertain how many random numbers we need.
     *
     * @return the amount of cells we have for this reel.
     */
    @JsonIgnore
    public int getCellAmount() {
        // Done like this to accommodate for varying columns per row (counts columns per row).
//        int amount = Arrays.stream(cells).mapToInt(value -> value.length).sum();
//        return amount;
        return 0;
    }

//    public ReelCell[][] getCells() {
//        return this.cells;
//    }
}
