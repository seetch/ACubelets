package me.davidml16.acubelets.utils;

import lombok.NonNull;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;

/**
 * Enum of supported NMS versions.
 */
@SuppressWarnings("java:S115") // SonarLint: Enum values naming convention
public enum Version {

    v1_16_R1(16, "1.16"),
    v1_16_R2(16, "1.16.2", "1.16.3"),
    v1_16_R3(16, "1.16.4", "1.16.5"),
    v1_17_R1(17, "1.17", "1.17.1"),
    v1_18_R1(18, "1.18", "1.18.1"),
    v1_18_R2(18, "1.18.2"),
    v1_19_R1(19, "1.19", "1.19.1", "1.19.2"),
    v1_19_R2(19, "1.19.3"),
    v1_19_R3(19, "1.19.4"),
    v1_20_R1(20, "1.20", "1.20.1"),
    v1_20_R2(20, "1.20.2"),
    v1_20_R3(20, "1.20.3", "1.20.4"),
    v1_20_R4(20, "1.20.5", "1.20.6"),
    v1_21_R1(21, "1.21", "1.21.1"),
    v1_21_R2(21, "1.21.2", "1.21.3");

    public static final Version CURRENT;
    public static final String CURRENT_MINECRAFT_VERSION;

    static {
        CURRENT_MINECRAFT_VERSION = getCurrentMinecraftVersion();
        CURRENT = getCurrentVersion();
    }

    private final int minor;
    private final String[] minecraftVersions;

    Version(int minor, String... minecraftVersions) {
        this.minor = minor;
        this.minecraftVersions = minecraftVersions;
    }

    private static Version getCurrentVersion() {
        return fromMinecraftVersion(CURRENT_MINECRAFT_VERSION);
    }

    private static String getCurrentMinecraftVersion() {
        // Bukkit version (e.g., 1.20.6-R0.1-SNAPSHOT)
        String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        // Minecraft version (e.g., 1.20.6)
        return bukkitVersion.split("-", 2)[0];
    }

    /**
     * Parse a Version from string.
     *
     * @param version The string.
     * @return The parsed Version or null.
     */
    @Nullable
    public static Version fromString(String version) {
        if (version == null) {
            return null;
        }

        for (Version value : Version.values()) {
            if (value.name().equalsIgnoreCase(version)) {
                return value;
            }
        }
        return null;
    }

    @Nullable
    public static Version fromMinecraftVersion(String minecraftVersion) {
        for (Version version : Version.values()) {
            for (String candidateMinecraftVersion : version.getMinecraftVersions()) {
                if (candidateMinecraftVersion.equals(minecraftVersion)) {
                    return version;
                }
            }
        }
        return null;
    }

    public static boolean is(int minor) {
        return CURRENT.getMinor() == minor;
    }

    public static boolean is(@NonNull Version version) {
        return CURRENT == version;
    }

    public static boolean after(int minor) {
        return CURRENT.getMinor() > minor;
    }

    public static boolean after(@NonNull Version version) {
        return CURRENT.ordinal() > version.ordinal();
    }

    public static boolean afterOrEqual(int minor) {
        return CURRENT.getMinor() >= minor;
    }

    public static boolean afterOrEqual(@NonNull Version version) {
        return CURRENT.ordinal() >= version.ordinal();
    }

    public static boolean before(int minor) {
        return CURRENT.getMinor() < minor;
    }

    public static boolean before(@NonNull Version version) {
        return CURRENT.ordinal() < version.ordinal();
    }

    /*
     *  Version
     */

    public static boolean beforeOrEqual(int minor) {
        return CURRENT.getMinor() <= minor;
    }

    public static boolean beforeOrEqual(@NonNull Version version) {
        return CURRENT.ordinal() <= version.ordinal();
    }

    public static boolean supportsHex() {
        return afterOrEqual(16);
    }

    public int getMinor() {
        return minor;
    }

    public String[] getMinecraftVersions() {
        return minecraftVersions;
    }
}
