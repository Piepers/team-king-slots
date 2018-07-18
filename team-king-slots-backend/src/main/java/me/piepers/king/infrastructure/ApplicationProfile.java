package me.piepers.king.infrastructure;

/**
 * Convenience enumeration used thoughout the application to differentiate between environments
 * (production, local, test, acceptance).
 *
 * @author Bas Piepers
 */
public enum ApplicationProfile {

    LOCAL("Local"), PROD("Production");

    private final String name;

    ApplicationProfile(String name) {
        this.name = name;
    }

    /**
     * Falls back to Local if the given string is not matched.
     *
     * @param name, the string to match
     * @return either an instance of PROD or LOCAL (default).
     */
    public static ApplicationProfile resolve(String name) {
        if (name.equalsIgnoreCase("Production")) {
            return PROD;
        }
        return LOCAL;
    }

    public String getName() {
        return name;
    }
}
