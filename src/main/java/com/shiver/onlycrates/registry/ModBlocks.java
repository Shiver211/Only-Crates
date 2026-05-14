package com.shiver.onlycrates.registry;

import com.shiver.onlycrates.blocks.BlockGiantChest;

public class ModBlocks {

    public static BlockGiantChest blockGiantChest;
    public static BlockGiantChest blockGiantChestMedium;
    public static BlockGiantChest blockGiantChestLarge;

    public static void init() {
        blockGiantChest = new BlockGiantChest("block_giant_chest", 0);
        blockGiantChestMedium = new BlockGiantChest("block_giant_chest_medium", 1);
        blockGiantChestLarge = new BlockGiantChest("block_giant_chest_large", 2);
    }
}
