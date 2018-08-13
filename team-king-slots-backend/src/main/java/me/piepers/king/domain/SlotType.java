package me.piepers.king.domain;

/**
 * Convenience enumeration for creating a Slot of a particular type.
 * <p>
 * FIVE_BY_THREE: a slot with five columns and three rows. Can have paylines on each of the three rows in varying
 * orientation.
 * <p>
 * CLASSIC: has three columns with three rows and one payline in the middle of the slot.
 */
public enum SlotType {
    FIVE_BY_THREE, FIVE_BY_FOUR, CLASSIC;
}
