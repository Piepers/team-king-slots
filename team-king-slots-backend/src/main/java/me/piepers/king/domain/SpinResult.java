package me.piepers.king.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Represents the result of one spin for a particular slot.
 *
 * @author Bas Piepers
 */
@DataObject
public class SpinResult implements JsonDomainObject {
    private final Slot slot;
    // TODO: just a bogus result for now
    private final String result;

    private SpinResult(Slot slot, String result) {
        this.slot = slot;
        this.result = result;
    }

    public SpinResult(JsonObject jsonObject) {
        this.slot = new Slot(jsonObject.getJsonObject("slot"));
        this.result = jsonObject.getString("result");
    }

    // A factory that is responsible to generate a result for the given Slot
    public static SpinResult create(Slot slot) {
        return new SpinResult(slot, "YOU ARE A WINNER!!!");
    }

    public Slot getSlot() {
        return slot;
    }

    public String getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinResult that = (SpinResult) o;

        if (!slot.equals(that.slot)) return false;
        return result.equals(that.result);
    }

    @Override
    public int hashCode() {
        int result1 = slot.hashCode();
        result1 = 31 * result1 + result.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        return "SpinResult{" +
                "slot=" + slot +
                ", result='" + result + '\'' +
                '}';
    }
}
