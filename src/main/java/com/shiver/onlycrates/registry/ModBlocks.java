package com.shiver.onlycrates.registry;

import java.util.HashMap;
import java.util.Map;

import com.shiver.onlycrates.blocks.BlockGiantChest;
import com.shiver.onlycrates.config.ModConfig;

public class ModBlocks {

    public static BlockGiantChest BLOCK_GIANT_CHEST;
    public static final Map<Integer, ModConfig.CrateLevel> LEVELS = new HashMap<>();

    public static void init() {
        BLOCK_GIANT_CHEST = new BlockGiantChest("block_giant_chest");

        LEVELS.clear();
        LEVELS.put(0, new ModConfig.CrateLevel(0, 1));
        LEVELS.put(1, new ModConfig.CrateLevel(1, 2));
        LEVELS.put(2, new ModConfig.CrateLevel(2, 3));
        for (ModConfig.CrateLevel level : ModConfig.getExtraCrates()) {
            LEVELS.put(level.getMeta(), level);
        }
    }
}
