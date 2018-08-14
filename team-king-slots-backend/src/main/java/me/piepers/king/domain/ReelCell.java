package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import io.vertx.core.json.JsonObject;

/**
 * One cell on a reel of the slot machine. The {@link Payline} uses this to determine if cells represent a win. Cells
 * are also stored on a {@link Reel} because a Reel maintains these cells.
 * <p>
 * Contains a method that instructs how to map this object (see asJson method).
 *
 * @author Bas Piepers
 */
// TODO: value has a -1 value in case this cell represents an empty cell (to accommodate for variable lengths for columns).
public class ReelCell {
    private int value;
    private CellSymbolConfig.Symbol symbol;


    private ReelCell(int value) {
        this.value = value;
        this.symbol = CellSymbolConfig.Symbol.NONE;
    }

    private ReelCell(int value, CellSymbolConfig.Symbol symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public static ReelCell of(int value, CellSymbolConfig.Symbol symbol) {
        return new ReelCell(value, symbol);
    }

    public static  ReelCell of(int value, String symbol) {
        CellSymbolConfig.Symbol s = CellSymbolConfig.Symbol.resolve(symbol);
        return new ReelCell(value, s);
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

    public CellSymbolConfig.Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(CellSymbolConfig.Symbol symbol) {
        this.symbol = symbol;
    }

    public ReelCell assignSymbol(Slot.SymbolAssigner assigner, CellSymbolConfig config) {
        this.symbol = assigner.assignForNumber(this.value, config);
        return this;
    }

    @JsonValue
    public JsonObject asJson() {
        return new JsonObject().put(String.valueOf(this.value), this.symbol.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReelCell reelCell = (ReelCell) o;

        if (value != reelCell.value) return false;
        return symbol == reelCell.symbol;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + symbol.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ReelCell{" +
                "value=" + value +
                ", symbol=" + symbol +
                '}';
    }
}
