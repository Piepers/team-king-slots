package me.piepers.king.domain;

public enum SlotStatus {
    INITIALIZED, IDLE, SPINNING;

    public static SlotStatus resolve(String from) {
        String localFrom = from.toUpperCase();
        switch (localFrom) {
            case "SPINNING":
                return SPINNING;
            case "INITIALIZED":
                return INITIALIZED;
            default:
                return IDLE;
        }
    }
}
