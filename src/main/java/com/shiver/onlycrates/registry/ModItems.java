package com.shiver.onlycrates.registry;

import com.shiver.onlycrates.items.ItemChestToCrateUpgrade;
import com.shiver.onlycrates.items.ItemCrateUpgrade;
import com.shiver.onlycrates.tile.TileEntityGiantChest;
import com.shiver.onlycrates.tile.TileEntityGiantChestMedium;
import com.shiver.onlycrates.util.ItemUtil;

import net.minecraft.tileentity.TileEntityChest;

public class ModItems {

    public static ItemChestToCrateUpgrade itemChestToCrateUpgrade;
    public static ItemChestToCrateUpgrade itemSmallToMediumCrateUpgrade;
    public static ItemChestToCrateUpgrade itemMediumToLargeCrateUpgrade;
    public static ItemCrateUpgrade itemShulkerUpgrade;
    public static ItemCrateUpgrade itemBlastProofUpgrade;

    public static void init() {

        itemChestToCrateUpgrade = new ItemChestToCrateUpgrade("item_chest_to_crate_upgrade", TileEntityChest.class, ModBlocks.blockGiantChest.getDefaultState());
        ItemUtil.registerItem(itemChestToCrateUpgrade, "item_chest_to_crate_upgrade", true);

        itemSmallToMediumCrateUpgrade = new ItemChestToCrateUpgrade("item_small_to_medium_crate_upgrade", TileEntityGiantChest.class, ModBlocks.blockGiantChestMedium.getDefaultState());
        ItemUtil.registerItem(itemSmallToMediumCrateUpgrade, "item_small_to_medium_crate_upgrade", true);

        itemMediumToLargeCrateUpgrade = new ItemChestToCrateUpgrade("item_medium_to_large_crate_upgrade", TileEntityGiantChestMedium.class, ModBlocks.blockGiantChestLarge.getDefaultState());
        ItemUtil.registerItem(itemMediumToLargeCrateUpgrade, "item_medium_to_large_crate_upgrade", true);

        itemShulkerUpgrade = new ItemCrateUpgrade(ItemCrateUpgrade.UpgradeType.SHULKER);
        ItemUtil.registerItem(itemShulkerUpgrade, "item_shulker_upgrade", true);

        itemBlastProofUpgrade = new ItemCrateUpgrade(ItemCrateUpgrade.UpgradeType.BLAST_PROOF);
        ItemUtil.registerItem(itemBlastProofUpgrade, "item_blast_proof_upgrade", true);
    }
}
