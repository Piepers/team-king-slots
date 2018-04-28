package me.piepers.king.domain;

public enum SlotStatus {
    IDLE, SPINNING;

    public static SlotStatus resolve(String from) {
        if (from.equalsIgnoreCase("Spinning")) {
            return SPINNING;
        }

        return IDLE;
    }
}
