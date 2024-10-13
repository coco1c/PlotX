package com.coco.plotX.Util;

import com.coco.plotX.PlotX;
import com.coco.plotX.plots.Plot;
import io.github.g00fy2.versioncompare.Version;
import org.bukkit.Bukkit;

public class VersionUtil {
    private static boolean hexSupport;
    private static boolean oldVersion;
    private static boolean legacyVersion;
    public static boolean latestVersion;

    static {
        final String serverVersion = Bukkit.getBukkitVersion();
        Version version = new Version(serverVersion);
        setHexSupport(version.isAtLeast("1.16"));
        setOldVersion(version.isLowerThan("1.9"));
        setLegacyVersion(version.isLowerThan("1.13"));
    }


    public static boolean isHexSupport() {
        return hexSupport;
    }

    public static void setHexSupport(boolean hexSupport) {
        VersionUtil.hexSupport = hexSupport;
    }

    public static boolean isOldVersion() {
        return oldVersion;
    }

    public static void setOldVersion(boolean oldVersion) {
        VersionUtil.oldVersion = oldVersion;
    }

    public static boolean isLegacyVersion() {
        return legacyVersion;
    }

    public static void setLegacyVersion(boolean legacyVersion) {
        VersionUtil.legacyVersion = legacyVersion;
    }

    public static void setLatestVersion(boolean latestVersion) {
        VersionUtil.latestVersion = latestVersion;
    }

    public static boolean isLatestVersion() {
        return latestVersion;
    }
}