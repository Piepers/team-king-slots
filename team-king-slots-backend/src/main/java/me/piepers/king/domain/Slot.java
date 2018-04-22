package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * Slot domain model object that stores the state of a slot
 *
 * @author Bas Piepers
 *
 */
@DataObject
public class Slot implements JsonDomainObject {

    @JsonUnwrapped
    private final SlotId id;
    private final String name;
    private final Long score;
    private final Instant created;
    private final String player;

    public Slot(JsonObject jsonObject) {
     this.id = SlotId.of(jsonObject.getString("id"));
     this.name = jsonObject.getString("name");
     this.score = jsonObject.getLong("score");
     this.created = jsonObject.getInstant("created");
     this.player = jsonObject.getString("player");

    }

    public Slot(SlotId id, String name, Long score, Instant created, String player) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.created = created;
        this.player = player;
    }

    public SlotId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getScore() {
        return score;
    }

    public Instant getCreated() {
        return created;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;

        if (!id.equals(slot.id)) return false;
        if (!name.equals(slot.name)) return false;
        if (!score.equals(slot.score)) return false;
        if (!created.equals(slot.created)) return false;
        return player.equals(slot.player);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + score.hashCode();
        result = 31 * result + created.hashCode();
        result = 31 * result + player.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", created=" + created +
                ", player='" + player + '\'' +
                '}';
    }
}
