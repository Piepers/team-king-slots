package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.reactivex.Single;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.List;

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
    private final int lowestNr;
    private final int highestNr;

    public Slot(JsonObject jsonObject) {
        this.id = SlotId.of(jsonObject.getString("id"));
        this.name = jsonObject.getString("name");
        this.score = jsonObject.getLong("score");
        this.created = jsonObject.getInstant("created");
        this.player = jsonObject.getString("player");
        this.status = SlotStatus.resolve(jsonObject.getString("status"));
        this.reel = new Reel(jsonObject.getJsonObject("reel"));
        this.lowestNr = jsonObject.getInteger("lowestNr");
        this.highestNr = jsonObject.getInteger("highestNr");
    }

    private Slot(SlotId id, String name, Long score, Instant created, String player, Reel reel, int lowestNr, int highestNr) {
        this.id = id;
        this.status = SlotStatus.INITIALIZED;
        this.name = name;
        this.score = score;
        this.created = created;
        this.player = player;
        this.reel = reel;
        this.lowestNr = lowestNr;
        this.highestNr = highestNr;
    }

    /**
     * Constructs default slot implementations with a payline.
     */
    public static Slot of(SlotType type, String player) {
        switch (type) {
            case CLASSIC:
                Reel classicReel = Reel.of(3, 3);
                classicReel.addPayline(1, new Integer[]{2, 2, 2});
                return new Slot(SlotId.create(), "Classic", 0L, Instant.now(), player, classicReel, 0, 100);
            case FIVE_BY_THREE:
                Reel fiveByThree = Reel.of(3, 5);
                fiveByThree.addPayline(1, new Integer[]{2, 2, 2, 2, 2});
                return new Slot(SlotId.create(), "FiveByThree", 0L, Instant.now(), player, fiveByThree, 0, 100);
            case FIVE_BY_FOUR:
                Reel fiveByFour = Reel.of(4, 5);
                fiveByFour.addPayline(1, new Integer[]{2, 2, 2, 2, 2});
                return new Slot(SlotId.create(), "FiveByFour", 0L, Instant.now(), player, fiveByFour, 0, 100);
            default:
                throw new UnsupportedOperationException("Unsupported slot type.");
        }
    }

    // BUSINESS LOGIC
    public Slot spin() {
        if (this.status == SlotStatus.SPINNING) {
            throw new IllegalStateException("The slot is already spinning.");
        } else {
            this.status = SlotStatus.SPINNING;
        }
        return this;
    }

    // FIXME: it would be better if the cells of the reels are triggered to request a random number themselves rather than having the slot trigger that and assign the numbers.
    public Single<SpinResult> stop(RandomNumberFetcher randomNumberFetcher) {
        // Check if the status is appropriate to stop the reels on this instance.
        if (this.status == SlotStatus.SPINNING) {

            this.status = SlotStatus.IDLE;

            return randomNumberFetcher
                    .fetch(this)
                    .flatMap(numbers -> Single
                            .just(this.reel
                                    .assignNumbersToReels(numbers)))
                    .flatMap(reel -> Single.just(SpinResult.create(this)));
        } else {
            return Single.error(new IllegalStateException("This slot machine is not spinning and can therefore not be stopped."));
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

        if (lowestNr != slot.lowestNr) return false;
        if (highestNr != slot.highestNr) return false;
        if (!id.equals(slot.id)) return false;
        if (!name.equals(slot.name)) return false;
        if (!score.equals(slot.score)) return false;
        if (!created.equals(slot.created)) return false;
        if (!player.equals(slot.player)) return false;
        if (!reel.equals(slot.reel)) return false;
        return status == slot.status;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + score.hashCode();
        result = 31 * result + created.hashCode();
        result = 31 * result + player.hashCode();
        result = 31 * result + reel.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + lowestNr;
        result = 31 * result + highestNr;
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
                ", lowestNr=" + lowestNr +
                ", highestNr=" + highestNr +
                '}';
    }

    @FunctionalInterface
    public interface RandomNumberFetcher {
        Single<List<Integer>> fetch(Slot slot);
    }
}
