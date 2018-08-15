package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The Reel class represents the reels part of a slot. The name is somewhat misleading as it is not one reel but the
 * collection of cells on the reels of a slot. Cells represent a grid of cells and the cells represent an image on the
 * reel which is randomly chosen. The main purpose of this class is to store the cells a slot has and offers a way to
 * calculate how many random numbers we need to request for one spin.
 * <p>
 * Currently the cells are represented by a List of a list of cells because Vert.x is unable to map a simple 2D array.
 * Unlike a reel in a slot, the list of cells are horizontally oriented meaning they are read from left to right. Reels
 * in a slot spin vertically but that is not relevant for the backend. The cells in the row can be empty to support
 * slots that have a varying amount of cells per column. This is represented by a cell that has a number of -1.
 * <p>
 * A Reel has a collection of {@link Payline} objects that are used in this reel to calculate the wins for spins.
 * Paylines are created with coordinates that dictate how a line runs from left to right (right to left and vertical
 * lines are not yet supported). A Reel validates that only one payline with a particular reference number and
 * coordinates exist and that the paylines do not reference non-existent cells. While the reel is active, paylines
 * can be made active or un-active. Paylines can only be added on construction. A reel must always have at least one
 * payline. A payline can also span an empty line but this cell is not considered when calculating the win.
 * <p>
 * Also see javadoc for {@link Payline}
 *
 * @author Bas Piepers
 */
@DataObject
public class Reel implements JsonDomainObject {

    private final List<List<ReelCell>> cells;
    private Map<Integer, Payline> payLines;
    private ReelConfig reelConfig;

    ////////////////////////////////////////////////////////////////////////////
    //////////////Constructors and factory methods/////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    public Reel(JsonObject jsonObject) {
        JsonArray rows = jsonObject.getJsonArray("cells");

        // FIXME: how to fix this in a way we don't have to explicitly cast raw types?
        List<List<ReelCell>> rcs = new ArrayList<>();
        // The rows may contain empty cells/null values.
        rows
                .stream()
                .forEach(o -> {
                    List<ReelCell> row =
                            ((JsonArray) o)
                                    .stream()
                                    // FIXME: better representation of null values would be nice.
                                    .map(item -> Objects.isNull(item) ? null : this.toReelCell((JsonObject) item))
                                    .collect(Collectors.toList());
                    rcs.add(row);
                });
        this.cells = Collections.unmodifiableList(rcs);

        JsonArray lines = jsonObject.getJsonArray("payLines");

        // Paylines are in principle optional although a slot machine without paylines is not useful.
        if (Objects.nonNull(lines)) {
            this.payLines = lines.stream().map(jo -> new Payline((JsonObject) jo)).collect(Collectors.toMap(p -> p.getReference(), p -> p));
        }

        JsonObject config = jsonObject.getJsonObject("reelConfig");
        if (Objects.nonNull(config)) {
            this.reelConfig = new ReelConfig(config);

        }
    }

    // FIXME: how to map a simple key - value pair properly?
    private ReelCell toReelCell(JsonObject item) {
        Map<String, Object> representation = item.getMap();
        List<Integer> number = representation.keySet().stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
        List<String> value = representation.values().stream().map(i -> String.valueOf(i)).collect(Collectors.toList());
        return ReelCell.of(number.get(0), value.get(0));
    }

    /**
     * Initialize a Reel with a certain amount of rows and columns. This constructor is there for convenience and
     * supports a fixed amount of columns for each row only. What we are trying to achieve is to get an unmodifiable
     * 2D list of {@link ReelCell} items. They are initialized with Cells that represent a 0 value.
     *
     * @param rows,    the amount of rows in the reel
     * @param columns, the amount of columns in the reel
     */
    private Reel(int rows, int columns) {
        ReelCell[][] cs = new ReelCell[rows][columns];
        List<List<ReelCell>> rcs = new ArrayList<>();
        for (int i = 0; i < cs.length; i++) {
            ReelCell[] row = cs[i];
            for (int j = 0; j < row.length; j++) {
                row[j] = ReelCell.of(0);
            }

            rcs.add(Arrays.asList(cs[i]));
        }
        this.cells = Collections.unmodifiableList(rcs);
    }

    /**
     * The factory method for the above convenience constructor.
     *
     * @param rows,    the (fixed) amount of rows of the reels.
     * @param columns, the (fixed) amount of columns.
     * @return in instance of {@link Reel} with 0 values for the cell.
     */
    public static Reel of(int rows, int columns) {
        return new Reel(rows, columns);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////// Business logic //////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public Reel addPayline(int reference, Integer[] coordinates) {
        if (Objects.isNull(this.payLines)) {
            this.payLines = new HashMap<>();
        }

        Payline p = Payline.of(reference, coordinates, 0);
        if (!this.hasPayline(p) && this.payLineInGrid(coordinates)) {
            this.payLines.put(p.getReference(), p);
        } else {
            throw new IllegalArgumentException("This payline is either not valid or already exists.");
        }
        return this;
    }

    /**
     * Reel configuration can only be set once and it needs to be valid.
     *
     * @param reelConfig, the configuration to set.
     */
    public void setReelConfig(ReelConfig reelConfig) {
        if (Objects.isNull(this.reelConfig) && reelConfig.isValid()) {
            this.reelConfig = reelConfig;
        } else {
            throw new IllegalStateException("A Reel configuration has already been set for this Reel or the provided reelconfig is not valid (" + reelConfig.isValid() + ").");
        }

    }

    // Must completely fit in the grid from start to finish. Note that a payline's coordinate start at 1. Variable
    // row sizes are not supported.
    private boolean payLineInGrid(Integer[] paylineCoords) {

        // Length of coords must always be the same as the lengts of the lists in the cells
        if (paylineCoords.length != this.getColSize()) {
            return false;
        }
        // The depth of (the value in the elements of) the coords must never be larger than the size of cells
        for (Integer c : paylineCoords) {
            if (c > this.getRowSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean hasPayline(Payline p) {
        return Objects.nonNull(this.payLines) &&
                (this.payLines.containsKey(p.getReference()) ||
                this.payLines
                        .values()
                        .stream()
                        .anyMatch(payline ->
                                payline.getCoordinates()
                                        .equals(p.getCoordinates())));
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("cells", new JsonArray(cells))
                .put("payLines", Objects.nonNull(this.payLines) ? new JsonArray(new ArrayList(this.payLines.values())) : null);
    }

    public Reel assignNumbersToReels(List<Integer> numbers) {
        if (numbers.size() < this.getCellAmount()) {
            throw new IllegalArgumentException("Expect the amount of numbers to be equal to the size of the reel");
        }

        AtomicInteger count = new AtomicInteger(0);

        this.getCells()
                .stream()
                .forEach(row -> row
                        .stream()
                        .forEach(cell -> cell
                                .setValue(numbers
                                        .get(count.getAndIncrement()))));
        return this;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////// Accessors ///////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int getColSize() {
        return Objects.nonNull(this.cells) && Objects.nonNull(this.cells.get(0)) ? this.cells.get(0).size() : 0;
    }

    public int getRowSize() {
        return Objects.nonNull(this.getCells()) ? this.getCells().size() : 0;
    }

    /**
     * To be able to ascertain how many cells we have to help determine how many random numbers we need.
     * Note: varying column sizes are not supported yet.
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

    public Map<Integer, Payline> getPayLines() {
        return payLines;
    }

    public ReelConfig getReelConfig() {
        return reelConfig;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Equals, hashcode, toString //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reel reel = (Reel) o;

        if (!cells.equals(reel.cells)) return false;
        if (payLines != null ? !payLines.equals(reel.payLines) : reel.payLines != null) return false;
        return reelConfig != null ? reelConfig.equals(reel.reelConfig) : reel.reelConfig == null;
    }

    @Override
    public int hashCode() {
        int result = cells.hashCode();
        result = 31 * result + (payLines != null ? payLines.hashCode() : 0);
        result = 31 * result + (reelConfig != null ? reelConfig.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Reel{" +
                "cells=" + cells +
                ", payLines=" + payLines +
                ", reelConfig=" + reelConfig +
                '}';
    }
}
