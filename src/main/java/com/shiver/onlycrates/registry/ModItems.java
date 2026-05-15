package com.shiver.onlycrates.registry;

import com.shiver.onlycrates.items.ItemChestToCrateUpgrade;
import com.shiver.onlycrates.items.ItemCrateUpgrade;
import com.shiver.onlycrates.tile.TileEntityCrate;
import com.shiver.onlycrates.tile.TileEntityCrateMedium;
import com.shiver.onlycrates.util.ItemUtil;

import net.minecraft.tileentity.TileEntityChest;

public class ModItems {

    public static ItemChestToCrateUpgrade itemChestToCrateUpgrade;
    public static ItemChestToCrateUpgrade itemSmallToMediumCrateUpgrade;
    public static ItemChestToCrateUpgrade itemMediumToLargeCrateUpgrade;
    public static ItemCrateUpgrade itemShulkerUpgrade;
    public static ItemCrateUpgrade itemBlastProofUpgrade;

    public static void init() {

        itemChestToCrateUpgrade = new ItemChestToCrateUpgrade("chest_to_crate_upgrade", TileEntityChest.class, ModBlocks.blockCrate.getDefaultState());
        ItemUtil.registerItem(itemChestToCrateUpgrade, "chest_to_crate_upgrade", true);

        itemSmallToMediumCrateUpgrade = new ItemChestToCrateUpgrade("small_to_medium_crate_upgrade", TileEntityCrate.class, ModBlocks.blockCrateMedium.getDefaultState());
        ItemUtil.registerItem(itemSmallToMediumCrateUpgrade, "small_to_medium_crate_upgrade", true);

        itemMediumToLargeCrateUpgrade = new ItemChestToCrateUpgrade("medium_to_large_crate_upgrade", TileEntityCrateMedium.class, ModBlocks.blockCrateLarge.getDefaultState());
        ItemUtil.registerItem(itemMediumToLargeCrateUpgrade, "medium_to_large_crate_upgrade", true);

        itemShulkerUpgrade = new ItemCrateUpgrade(ItemCrateUpgrade.UpgradeType.SHULKER);
        ItemUtil.registerItem(itemShulkerUpgrade, "shulker_upgrade", true);

        itemBlastProofUpgrade = new ItemCrateUpgrade(ItemCrateUpgrade.UpgradeType.BLAST_PROOF);
        ItemUtil.registerItem(itemBlastProofUpgrade, "blast_proof_upgrade", true);
    }
}
