package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.reactivex.Single;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Slot domain model object that stores the state of a slot
 *
 * @author Bas Piepers
 */
@DataObject
public class Slot implements JsonDomainObject {
    private final static Logger LOGGER = LoggerFactory.getLogger(Slot.class);

    @JsonUnwrapped
    private final SlotId id;
    private final String name;
    private final Long score;
    private final Instant created;
    private final String player;
    private final Reel reel;
    private SlotStatus status;
    // TODO: might not be necessary
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

    public Slot(SlotId id, String name, Long score, Instant created, String player, Reel reel, int lowestNr, int highestNr) {
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
     * Constructs default slot implementations with one payline.
     */
    public static Slot of(SlotType type, String player) {
        switch (type) {
            case CLASSIC:
                Reel classicReel = Reel.of(3, 3);
                classicReel.addPayline(1, new Integer[]{2, 2, 2});
                Slot slot = new Slot(SlotId.create(), "Classic", 0L, Instant.now(), player, classicReel, 1, 100);
                ReelConfig classicReelConfig = generateClassicDefaultReelConfig(slot);
                classicReel.setReelConfig(classicReelConfig);
                return slot;
            case FIVE_BY_THREE:
                Reel fiveByThree = Reel.of(3, 5);
                fiveByThree.addPayline(1, new Integer[]{2, 2, 2, 2, 2});
                return new Slot(SlotId.create(), "FiveByThree", 0L, Instant.now(), player, fiveByThree, 1, 100);
            case FIVE_BY_FOUR:
                Reel fiveByFour = Reel.of(4, 5);
                fiveByFour.addPayline(1, new Integer[]{2, 2, 2, 2, 2});
                return new Slot(SlotId.create(), "FiveByFour", 0L, Instant.now(), player, fiveByFour, 1, 100);
            default:
                throw new UnsupportedOperationException("Unsupported slot type.");
        }
    }

    private static ReelConfig generateClassicDefaultReelConfig(Slot slot) {
        return ReelConfig.of(slot.lowestNr, slot.highestNr)
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.SEVEN, 1, 13, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{50, 100})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.TWO_SEVENS, 13, 23, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{80, 160})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.THREE_SEVENS, 23, 26, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{100, 200})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.CHERRY, 26, 40, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{40, 80})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.TWO_CHERRIES, 40, 49, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{90, 180})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.THREE_CHERRIES, 49, 51, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{120, 240})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.BELL, 51, 64, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{45, 90})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.TWO_BELLS, 64, 75, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{95, 190})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.THREE_BELLS, 75, 76, CellSymbolConfig.symbolScores(new SubsequentSymbols[]{SubsequentSymbols.TWO, SubsequentSymbols.THREE}, new Integer[]{130, 260})))
                .addCellConfig(CellSymbolConfig.of(CellSymbolConfig.Symbol.EMPTY, 76, 101, null));

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////BUSINESS LOGIC///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        LOGGER.debug("Stopping slot {}. Assigning numbers, mapping symbols and calculating results.", this.id.getId());
        // Check if the status is appropriate to stop the reels on this instance.
        if (this.status == SlotStatus.SPINNING) {

            this.status = SlotStatus.IDLE;

            return randomNumberFetcher
                    .fetch(this)
                    .flatMap(numbers -> Single
                            .just(this.reel
                                    .assignNumbersAndMapSymbols(numbers)))
                    .flatMap(reel -> Single.just(SpinResult.create(this)));
        } else {
            return Single.error(new IllegalStateException("This slot machine is not spinning and can therefore not be stopped."));
        }
    }

    /**
     * Activate a payline by its reference numbers. If the payline doesn't exist, an exception is thrown. Otherwise the payline is
     * set to active (even if it was active already).
     *
     * @param reference, the reference of the payline (the number of the payline).
     * @return the {@link Payline} for convenience.
     */
    public Payline activatePaylineByReference(Integer reference) {
        Payline p = findOrThrowException(reference);

        return p.activate();
    }

    /**
     * De-activate a payline by its reference numbers. If the payline doesn't exist, an exception is thrown. Otherwise the payline is
     * set to in-active (even if it was in-active already).
     *
     * @param reference, the reference of the payline (the number of the payline).
     * @return the {@link Payline} for convenience.
     */
    public Payline deActivatePaylineByReference(Integer reference) {
        Payline p = findOrThrowException(reference);

        return p.deActivate();
    }

    private Payline findOrThrowException(Integer reference) {
        if (Objects.isNull(this.getReel()) || Objects.isNull(this.getReel().getPayLines())) {
            throw new IllegalStateException("The current slot does not (yet) contain paylines.");
        }
        Payline p = this.getPaylineByReference(reference);
        if (Objects.isNull(p)) {
            throw new IllegalStateException("No payline exists with reference " + reference);
        }
        return p;
    }

    /**
     * Get a payline by reference or null if it doesn't exist.
     *
     * @param reference, the reference of the payline.
     * @return the {@link Payline} of the {@link Reel} by its reference.
     */
    public Payline getPaylineByReference(Integer reference) {
        if (Objects.isNull(this.getReel()) || Objects.isNull(this.getReel().getPayLines())) {
            return null;
        }

        return this.getReel().getPayLines().stream().filter(payline -> payline.getReference() == reference).findFirst().orElse(null);
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

    public int getLowestNr() {
        return lowestNr;
    }

    public int getHighestNr() {
        return highestNr;
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

    @FunctionalInterface
    public interface SymbolAssigner {
        CellSymbolConfig.Symbol assignForNumber(int number, CellSymbolConfig config);
    }
}
