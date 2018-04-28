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
    private final SlotId slotId;
    // TODO: just a bogus result for now
    private final String result;

    private SpinResult(SlotId slotId, String result) {
        this.slotId = slotId;
        this.result = result;
    }

    public SpinResult(JsonObject jsonObject) {
        this.slotId = SlotId.of(jsonObject.getString("id"));
        this.result = jsonObject.getString("result");
    }

    // A factory that is responsible to generate a result for the given slot id
    public static SpinResult create(SlotId slotId) {
        return new SpinResult(slotId, "YOU ARE A WINNER!!!");
    }

    public SlotId getSlotId() {
        return slotId;
    }

    public String getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinResult that = (SpinResult) o;

        if (!slotId.equals(that.slotId)) return false;
        return result.equals(that.result);
    }

    @Override
    public int hashCode() {
        int result1 = slotId.hashCode();
        result1 = 31 * result1 + result.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        return "SpinResult{" +
                "slotId=" + slotId +
                ", result='" + result + '\'' +
                '}';
    }
}
