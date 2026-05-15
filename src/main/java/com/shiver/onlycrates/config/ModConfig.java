package com.shiver.onlycrates.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shiver.onlycrates.OnlyCrates;

import net.minecraftforge.common.config.Configuration;

public final class ModConfig {

    private static final String CATEGORY_CRATES = "crates";
    private static final String KEY_EXTRA_CRATES = "extra_crates";

    private static List<CrateLevel> extraCrates = new ArrayList<>();
    private static File customTexturesFolder;

    private ModConfig() {}

    public static void load(File suggestedConfigFile) {
        File configFolder = suggestedConfigFile.getParentFile();
        File onlyCratesFolder = new File(configFolder, "onlycrates");
        if (!onlyCratesFolder.exists()) {
            onlyCratesFolder.mkdirs();
        }

        customTexturesFolder = new File(onlyCratesFolder, "textures");
        if (!customTexturesFolder.exists()) {
            customTexturesFolder.mkdirs();
        }

        File configFile = new File(onlyCratesFolder, "onlycrates.cfg");
        Configuration config = new Configuration(configFile);
        try {
            config.load();
            String[] entries = config.getStringList(
                    KEY_EXTRA_CRATES,
                    CATEGORY_CRATES,
                    new String[0],
                    "Extra crate levels. Format: block_id/display_name/pages Example: example_crate/Example Carte/4\n" +
                    "block_id: unique identifier for the crate\n" +
                    "display_name: name shown in game\n" +
                    "pages: number of GUI pages (1 page = 117 slots)\n" +
                    "Place 'block_id.png' (16x16) in the 'textures' folder to use custom textures\n"
            );
            extraCrates = parseEntries(entries);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static List<CrateLevel> getExtraCrates() {
        return Collections.unmodifiableList(extraCrates);
    }

    public static File getCustomTexturesFolder() {
        return customTexturesFolder;
    }

    private static List<CrateLevel> parseEntries(String[] entries) {
        List<CrateLevel> levels = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();

        for (String entry : entries) {
            if (entry == null) continue;
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) continue;

            String[] parts = trimmed.split("/", 3);
            if (parts.length != 3) {
                OnlyCrates.LOGGER.warn("Invalid extra_crates entry (expected 3 parts): {}", entry);
                continue;
            }

            String rawId = parts[0].trim();
            String displayName = parts[1].trim();
            String pageText = parts[2].trim();

            if (rawId.isEmpty()) {
                OnlyCrates.LOGGER.warn("Invalid extra_crates entry (empty block_id): {}", entry);
                continue;
            }
            if (displayName.isEmpty()) {
                OnlyCrates.LOGGER.warn("Invalid extra_crates entry (empty display_name): {}", entry);
                continue;
            }

            int pages;
            try {
                pages = Integer.parseInt(pageText);
            } catch (NumberFormatException ex) {
                OnlyCrates.LOGGER.warn("Invalid extra_crates entry (bad pages): {}", entry);
                continue;
            }
            if (pages < 1) {
                OnlyCrates.LOGGER.warn("Invalid extra_crates entry (pages < 1): {}", entry);
                continue;
            }

            String blockId = rawId.contains(":") ? rawId : OnlyCrates.MODID + ":" + rawId;

            if (usedIds.contains(blockId)) {
                OnlyCrates.LOGGER.warn("Duplicate extra_crates block_id, skipping: {}", blockId);
                continue;
            }
            usedIds.add(blockId);

            levels.add(new CrateLevel(blockId, displayName, pages));
        }
        return levels;
    }

    public static final class CrateLevel {
        private final String blockId;
        private final String displayName;
        private final int pages;

        public CrateLevel(String blockId, String displayName, int pages) {
            this.blockId = blockId;
            this.displayName = displayName;
            this.pages = pages;
        }

        public String getBlockId() {
            return this.blockId;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public int getPages() {
            return this.pages;
        }
    }
}
