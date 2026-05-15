package com.shiver.onlycrates.config;

import com.shiver.onlycrates.OnlyCrates;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class ModConfig {

    private static final String CATEGORY_CRATES = "crates";
    private static final String KEY_EXTRA_COUNT = "extra_crate_count";
    private static final String KEY_EXTRA_PAGES = "extra_crate_pages";

    private static List<CrateLevel> extraCrates = new ArrayList<>();

    private ModConfig() {}

    public static void load(File suggestedConfigFile) {
        File configFolder = suggestedConfigFile.getParentFile();
        File onlyCratesFolder = new File(configFolder, "onlycrates");
        if (!onlyCratesFolder.exists()) {
            onlyCratesFolder.mkdirs();
        }

        File configFile = new File(onlyCratesFolder, "onlycrates.cfg");
        Configuration config = new Configuration(configFile);
        try {
            config.load();

            int count = config.getInt(
                    KEY_EXTRA_COUNT,
                    CATEGORY_CRATES,
                    0,
                    0,
                    13,
                    "Number of additional crate blocks to register (beyond the 3 default). Max 13 (meta 3-15)."
            );

            int[] pages = config.get(
                    KEY_EXTRA_PAGES,
                    CATEGORY_CRATES,
                    new int[0],
                    "Pages for each extra crate. Must have exactly 'extra_crate_count' entries. 1 page = 117 slots."
            ).getIntList();

            extraCrates = buildExtraCrates(count, pages);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static List<CrateLevel> getExtraCrates() {
        return Collections.unmodifiableList(extraCrates);
    }

    private static List<CrateLevel> buildExtraCrates(int count, int[] pages) {
        List<CrateLevel> levels = new ArrayList<>();
        if (count != pages.length) {
            OnlyCrates.LOGGER.error(
                    "extra_crate_count ({}) does not match extra_crate_pages length ({})",
                    count, pages.length
            );
            return levels;
        }

        for (int i = 0; i < count; i++) {
            if (pages[i] < 1) {
                OnlyCrates.LOGGER.warn(
                        "Invalid pages value at index {}: {}, must be >= 1. Skipping.",
                        i, pages[i]
                );
                continue;
            }

            int meta = 3 + i;
            levels.add(new CrateLevel(meta, pages[i]));
        }
        return levels;
    }

    @Nullable
    public static CrateLevel getCrateLevel(int meta) {
        if (meta == 0) return new CrateLevel(0, 1);
        if (meta == 1) return new CrateLevel(1, 2);
        if (meta == 2) return new CrateLevel(2, 3);
        for (CrateLevel level : extraCrates) {
            if (level.getMeta() == meta) return level;
        }
        return null;
    }

    public static final class CrateLevel {
        private final int meta;
        private final int pages;

        public CrateLevel(int meta, int pages) {
            this.meta = meta;
            this.pages = pages;
        }

        public int getMeta() {
            return meta;
        }

        public int getPages() {
            return pages;
        }
    }
}