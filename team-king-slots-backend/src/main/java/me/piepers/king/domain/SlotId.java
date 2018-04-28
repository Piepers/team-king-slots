package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/**
 * Represents the unique identifier of a {@link Slot}
 *
 * @author Bas Piepers
 */
@DataObject
public class SlotId implements JsonDomainObject {
    private final String id;

    public SlotId(JsonObject jsonObject) {
        this.id = jsonObject.getString("id");
    }

    private SlotId(final String id) {
        this.id = id;
    }

    @JsonCreator
    public static SlotId of(final String id) {
        return new SlotId(id);
    }

    // Convenience method to instantiate with a UUID.
    public static SlotId create() {
        return new SlotId(UUID.randomUUID().toString());
    }

    public String getId() {
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
