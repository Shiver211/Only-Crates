package com.shiver.onlycrates.registry;

import com.shiver.onlycrates.blocks.BlockGiantChest;
import com.shiver.onlycrates.items.ItemChestToCrateUpgrade;
import com.shiver.onlycrates.tile.TileEntityGiantChest;
import com.shiver.onlycrates.util.ItemUtil;

import net.minecraft.tileentity.TileEntityChest;

public class ModItems {

    public static ItemChestToCrateUpgrade itemChestToCrateUpgrade;
    public static ItemChestToCrateUpgrade itemSmallToMediumCrateUpgrade;
    public static ItemChestToCrateUpgrade itemMediumToLargeCrateUpgrade;

    public static void init() {

        itemChestToCrateUpgrade = new ItemChestToCrateUpgrade(
                "item_chest_to_crate_upgrade",
                TileEntityChest.class,
                null,
                ModBlocks.BLOCK_GIANT_CHEST.getDefaultState().withProperty(BlockGiantChest.LEVEL, 0)
        );
        ItemUtil.registerItem(itemChestToCrateUpgrade, "item_chest_to_crate_upgrade", true);

        itemSmallToMediumCrateUpgrade = new ItemChestToCrateUpgrade(
                "item_small_to_medium_crate_upgrade",
                TileEntityGiantChest.class,
                ModBlocks.BLOCK_GIANT_CHEST.getDefaultState().withProperty(BlockGiantChest.LEVEL, 0),
                ModBlocks.BLOCK_GIANT_CHEST.getDefaultState().withProperty(BlockGiantChest.LEVEL, 1)
        );
        ItemUtil.registerItem(itemSmallToMediumCrateUpgrade, "item_small_to_medium_crate_upgrade", true);

        itemMediumToLargeCrateUpgrade = new ItemChestToCrateUpgrade(
                "item_medium_to_large_crate_upgrade",
                TileEntityGiantChest.class,
                ModBlocks.BLOCK_GIANT_CHEST.getDefaultState().withProperty(BlockGiantChest.LEVEL, 1),
                ModBlocks.BLOCK_GIANT_CHEST.getDefaultState().withProperty(BlockGiantChest.LEVEL, 2)
        );
        ItemUtil.registerItem(itemMediumToLargeCrateUpgrade, "item_medium_to_large_crate_upgrade", true);
    }
}
