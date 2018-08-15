package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Objects;

/**
 * Paylines represent the structure of the paylines for a particular slot. The score is calculated based on these
 * paylines using DFS (depth-first-search).
 * <p>
 * For inspiration of a relatively complex set of paylines, see:
 * <p>
 * https://www.casinosonline.com/wp-content/uploads/2013/12/lions-pride-slot-free-pay-lines.jpg
 * <p>
 * The payline has a field called "reference" which is just a number to represent it in the front-end to show how the
 * line runs. "1", for example, is typically a straight line that runs from left to right in the middle of the reels.
 * Per reel, there can only be one Payline with a particular reference number.
 * <p>
 * The coordinates represent how the payline runs from left to right. In principle, a payline can have every
 * orientation on the reel (left to right, right to left, top to bottom, bottom to top) although typically
 * a payline in a normal machine only runs from left to right. <b>In this version of the application</b>, paylines are indeed
 * left to right oriented so coordinates are always evaluated that way. Eg. 1, 2, 3, 4, 5 for a reel with five columns
 * and five rows would mean a line that runs from ([x-y]) 1-1, 2-2, 3-3, 4-4, 5-5 (so a diagonal line).
 * Or: 1, 2, 1, 1 would be 1-1, 2-2, 3-1, 4-1.
 * <p>
 * A payline ALWAYS starts on the first possible cell in a row but if a cell is missing in a row, that payline can
 * still reference a cell in that row. For example a reel that has the following layout:
 * <p>
 * **_____
 * **| | |
 * ---------
 * | | | | |
 * | | | | |
 * --| | |--
 * **-----
 * <p>
 * (so with 4 columns and the middle columns have 2 more rows, one at the top and one at the bottom) Then a payline
 * that is left-to-right oriented in the first row is expected to still have the coordinates 1, 1, 1, 1. A
 * representation in the UI would be:
 * ***_____
 * |X|X|
 * ---------
 * | | | | |
 * | | | | |
 * --| | |--
 * **-----
 * <p>
 * During the calculation of the score the engine makes sure that theses cells are simply not present.
 * Coordinates of 2, 1, 1, 2 are valid, however:
 * **_____
 * **|x|x|
 * ---------
 * |x| | |x|
 * | | | | |
 * --| | |--
 * **-----
 * <p>
 * The payline calculates equality of the cells from left to right. Of two equal images would yield a score, then, in
 * the above example, if 2 and 1 are equal, this is a score. However if 1 and 2 are equal this is not a score.
 * <p>
 * <p>
 * 2, 1, 1 would, for example, be an invalid payline (although the Payline class does not know that). What the
 * orientation of a payline is, exactly, is the responsibility of the reel that calculates the score. 2, 1, 2, 3 would
 * be:
 * <p>
 * **|x| |
 * ---------
 * |x| |x| |
 * | | | |x|
 * --| | |--
 * **-----
 * <p>
 * <p>
 * A {@link Reel} typically validates if a payline is unique within that reel based on the reference and the
 * coordinates. It is also the job of the {@link Reel} to validate that the coordinates represent a cell in the reel.
 * <p>
 * A payline also gets the "bet" that the player used in that spin but the payline does not validate if the player
 * has enough credits to make the bet. It is stored here to calculate the win for a spin. This can (and typically)
 * changes per spin. Some slots have one bet value that is valid for all active paylines but some slot allow users to
 * bet on each payline individually.
 * <p>
 * The active flag,  determines of the payline is active.  This is also a setting a player can configure
 * while playing. A "bet" does not signify the value it represents. This can be one cent or one euro, dollar or
 * whatever.
 * <p>
 * If more than one paylines are resulting in a score, this is summed up to a total score of that spin.
 *
 * @author Bas Piepers
 */
@DataObject
public class Payline implements JsonDomainObject {
    private final int reference;
    // A row of coordinates, starts at 1.
    private final JsonArray coordinates;
    private boolean active;
    private int bet;

    public Payline(JsonObject jsonObject) {
        this.reference = jsonObject.getInteger("reference");
        this.coordinates = jsonObject.getJsonArray("coordinates");
        this.active = jsonObject.getBoolean("active");
        this.bet = jsonObject.getInteger("bet");
    }

    private Payline(int reference, JsonArray coordinates, boolean active, int bet) {
        if (Objects.isNull(coordinates)) {
            throw new IllegalArgumentException("Coordinates are mandatory.");
        }

        this.reference = reference;
        this.coordinates = coordinates;
        this.active = active;
        this.bet = bet;
    }

    @JsonCreator
    private Payline(@JsonProperty("reference") int reference, @JsonProperty("coordinates")Integer[] coordinates, @JsonProperty("active") boolean active, @JsonProperty("bet") int bet) {
        if (Objects.isNull(coordinates)) {
            throw new IllegalArgumentException("Coordinates are mandatory.");
        }

        this.reference = reference;
        this.coordinates = new JsonArray();
        this.coordinates.getList().addAll(Arrays.asList(coordinates));
        this.active = active;
        this.bet = bet;

    }

    public static Payline of(int reference, Integer[] coordinates, int bet) {
        return new Payline(reference, coordinates, false, bet);
    }

    public static Payline of(int reference, Integer[] coordinates, boolean active, int bet) {
        return new Payline(reference, coordinates, active, bet);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payline payline = (Payline) o;

        if (reference != payline.reference) return false;
        if (active != payline.active) return false;
        if (bet != payline.bet) return false;
        return coordinates.equals(payline.coordinates);
    }

    @Override
    public int hashCode() {
        int result = reference;
        result = 31 * result + coordinates.hashCode();
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + bet;
        return result;
    }

    @Override
    public String toString() {
        return "Payline{" +
                "reference=" + reference +
                ", coordinates=" + coordinates +
                ", active=" + active +
                ", bet=" + bet +
                '}';
    }

    // For convenience.
    @JsonIgnore
    @GenIgnore
    public int[] getCoordsAsArray() {
        return this.coordinates.stream().mapToInt(coordinate -> (Integer) coordinate).toArray();
    }

    public int getReference() {
        return reference;
    }

    public JsonArray getCoordinates() {
        return coordinates;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
