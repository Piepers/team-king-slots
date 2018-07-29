package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * Slot domain model object that stores the state of a slot
 *
 * @author Bas Piepers
 */
@DataObject
public class Slot implements JsonDomainObject {

    @JsonUnwrapped
    private final SlotId id;
    private final String name;
    private final Long score;
    private final Instant created;
    private final String player;
    private final Reel reel;
    private SlotStatus status;

    public Slot(JsonObject jsonObject) {
        this.id = SlotId.of(jsonObject.getString("id"));
        this.name = jsonObject.getString("name");
        this.score = jsonObject.getLong("score");
        this.created = jsonObject.getInstant("created");
        this.player = jsonObject.getString("player");
        this.status = SlotStatus.resolve(jsonObject.getString("status"));
        this.reel = new Reel(jsonObject.getJsonObject("reel"));
    }

    private Slot(SlotId id, String name, Long score, Instant created, String player, Reel reel) {
        this.id = id;
        this.status = SlotStatus.INITIALIZED;
        this.name = name;
        this.score = score;
        this.created = created;
        this.player = player;
        this.reel = reel;
    }

    /**
     * Constructs a default slot implementation with a Reel of five columns and three rows.
     */
    public static Slot of(SlotType type, String player) {
        switch (type) {
            case CLASSIC:
                return new Slot(SlotId.create(), "Classic", 0L, Instant.now(), player, Reel.of(3, 3));
            case FIVE_BY_THREE:
                return new Slot(SlotId.create(), "FiveByThree", 0L, Instant.now(), player, Reel.of(3, 5));
            default:
                throw new UnsupportedOperationException("Unsupported slot type.");
        }
    }

    // BUSINESS LOGIC
    public Slot spin() {
        // TODO: status validation: can's spin a slot that is already spinning etc.
        this.status = SlotStatus.SPINNING;
        return this;
    }

    public SpinResult stop() {
        if (this.status == SlotStatus.SPINNING) {
            this.status = SlotStatus.IDLE;
            return SpinResult.create(this);
        } else {
            throw new IllegalStateException("This slot machine is not spinning and can therefore not be stopped.");
        }
    }

    // GETTERS
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

    public SlotStatus getStatus() {
        return status;
    }

    public Reel getReel() {
        return reel;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
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
        if (!player.equals(slot.player)) return false;
        return status == slot.status;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + score.hashCode();
        result = 31 * result + created.hashCode();
        result = 31 * result + player.hashCode();
        result = 31 * result + status.hashCode();
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
                ", reel=" + reel +
                ", status=" + status +
                '}';
    }
}
