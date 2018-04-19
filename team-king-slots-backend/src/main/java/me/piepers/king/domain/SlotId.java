package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Represents the unique identifier of a {@link Slot}
 *
 * @author Bas Piepers
 */
@DataObject
public class SlotId {
    private final Long id;

    public SlotId(JsonObject jsonObject) {
        this.id = jsonObject.getLong("id");
    }

    private SlotId(final Long id) {
        this.id = id;
    }

    @JsonCreator
    public static SlotId of(final Long id) {
        return new SlotId(id);
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SlotId{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlotId slotId = (SlotId) o;

        return id.equals(slotId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
