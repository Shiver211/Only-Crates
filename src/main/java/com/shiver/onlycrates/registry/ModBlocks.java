package com.shiver.onlycrates.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.shiver.onlycrates.blocks.BlockCrate;
import com.shiver.onlycrates.config.ModConfig;

public class ModBlocks {

    public static BlockCrate blockCrate;
    public static BlockCrate blockCrateMedium;
    public static BlockCrate blockCrateLarge;
    private static final List<BlockCrate> EXTRA_CRATES = new ArrayList<>();

    public static void init() {
        blockCrate = new BlockCrate("crate", 0);
        blockCrateMedium = new BlockCrate("crate_medium", 1);
        blockCrateLarge = new BlockCrate("crate_large", 2);

        EXTRA_CRATES.clear();
        for (ModConfig.CrateLevel level : ModConfig.getExtraCrates()) {
            String blockId = level.getBlockId();
            String path = blockId.contains(":") ? blockId.split(":", 2)[1] : blockId;
            BlockCrate block = new BlockCrate(path, level.getPages(), level.getDisplayName(), blockId);
            EXTRA_CRATES.add(block);
        }
    }

    public static List<BlockCrate> getExtraCrates() {
        return Collections.unmodifiableList(EXTRA_CRATES);
    }
}
