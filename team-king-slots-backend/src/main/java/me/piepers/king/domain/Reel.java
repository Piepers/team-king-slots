package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Reel class represents the reels part of a slot. The name is somewhat misleading as it is not one reel but the
 * collection of cells on the reels of a slot. It represents a Grid of cells and the cells represent an image on the
 * reel which is randomly chosen. The main purpose of this class is to store the cells a slot has and offers a way to
 * calculate how many random numbers we need to request for one spin.
 * <p>
 * Currently the cells are represented by a List of a list of cells because Vert.x in unable to map a simple 2D array.
 * Unlike a reel in a slot, the list of cells are horizontally oriented meaning they are read from left to right. Reels
 * in a slot spin vertically but that is not relevant for the backend. The cells in the row can be empty to support
 * slots that have a varying amount of cells per column.
 *
 * @author Bas Piepers
 */
@DataObject
public class Reel implements JsonDomainObject {
    private final List<List<ReelCell>> cells;

    public Reel(JsonObject jsonObject) {
        JsonArray rows = jsonObject.getJsonArray("cells");
        // FIXME: how to fix this in a way we don't have to explicitly cast raw types?
        List<List<ReelCell>> rcs = new ArrayList<>();
        rows
                .stream()
                .forEach(o -> {
                    List<ReelCell> row =
                            ((JsonArray) o)
                                    .stream()
                                    .map(item -> ReelCell.of((Integer) item))
                                    .collect(Collectors.toList());
                    rcs.add(row);
                });
        this.cells = Collections.unmodifiableList(rcs);
    }

    /**
     * Initialize a Reel with a certain amount of rows and columns. This constructor is there for convenience and
     * supports a fixed amount of columns for each row only. What we are trying to achieve is to get an unmodifiable
     * 2D list of {@link ReelCell} items.
     *
     * @param rows,    the amount of rows in the reel
     * @param columns, the amount of columns in the reel
     */
    private Reel(int rows, int columns) {
        ReelCell[][] cs = new ReelCell[rows][columns];
        List<List<ReelCell>> rcs = new ArrayList<>();
        for (int i = 0; i < cs.length; i++) {
            rcs.add(Arrays.asList(cs[i]));
        }
        this.cells = Collections.unmodifiableList(rcs);
    }

    public static Reel of(int rows, int columns) {
        return new Reel(rows, columns);
    }

    /**
     * To be able to ascertain how many cells we have to help ascertain how many random numbers we need.
     *
     * @return the amount of cells we have for this reel.
     */
    @JsonIgnore
    public int getCellAmount() {
        // Done like this to accommodate for varying columns per row (counts columns per row).
        return cells.stream().mapToInt(row -> row.size()).sum();
    }

    public List<List<ReelCell>> getCells() {
        return cells;
    }

    //    public ReelCell[][] getCells() {
//        return this.cells;
//    }
}
