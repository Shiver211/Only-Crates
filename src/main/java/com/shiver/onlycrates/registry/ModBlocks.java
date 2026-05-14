package com.shiver.onlycrates.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.shiver.onlycrates.blocks.BlockGiantChest;
import com.shiver.onlycrates.config.ModConfig;

public class ModBlocks {

    public static BlockGiantChest blockGiantChest;
    public static BlockGiantChest blockGiantChestMedium;
    public static BlockGiantChest blockGiantChestLarge;
    private static final List<BlockGiantChest> EXTRA_CRATES = new ArrayList<>();

    public static void init() {
        blockGiantChest = new BlockGiantChest("block_giant_chest", 0);
        blockGiantChestMedium = new BlockGiantChest("block_giant_chest_medium", 1);
        blockGiantChestLarge = new BlockGiantChest("block_giant_chest_large", 2);

        EXTRA_CRATES.clear();
        for (ModConfig.CrateLevel level : ModConfig.getExtraCrates()) {
            String blockId = level.getBlockId();
            String path = blockId.contains(":") ? blockId.split(":", 2)[1] : blockId;
            BlockGiantChest block = new BlockGiantChest(path, level.getPages(), level.getDisplayName(), blockId);
            EXTRA_CRATES.add(block);
        }
    }

    public static List<BlockGiantChest> getExtraCrates() {
        return Collections.unmodifiableList(EXTRA_CRATES);
    }
}
