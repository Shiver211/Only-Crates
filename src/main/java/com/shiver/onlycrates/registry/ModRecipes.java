package com.shiver.onlycrates.registry;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.crafting.RecipeKeepDataShaped;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModRecipes {

    public static void init() {
        // Small Crate: CWC / WDW / CWC  (C=chestWood, W=plankWood, D=iron_ingot)
        IRecipe recipeCrate = new ShapedOreRecipe(new ResourceLocation(OnlyCrates.MODID, "giant_chest"), new ItemStack(ModBlocks.blockGiantChest),
                "CWC", "WDW", "CWC", 'C', "chestWood", 'D', "ingotIron", 'W', "plankWood");
        recipeCrate.setRegistryName(new ResourceLocation(OnlyCrates.MODID, "giant_chest"));
        RegistryHandler.RECIPES_TO_REGISTER.add(recipeCrate);

        // Medium Crate: CWC / WDW / CWC  (C=coal_block, D=small_crate, W=plankWood) - keeps NBT
        new RecipeKeepDataShaped(new ResourceLocation(OnlyCrates.MODID, "giant_chest_medium"), new ItemStack(ModBlocks.blockGiantChestMedium), new ItemStack(ModBlocks.blockGiantChest),
                "CWC", "WDW", "CWC", 'C', new ItemStack(Blocks.COAL_BLOCK), 'D', new ItemStack(ModBlocks.blockGiantChest), 'W', "plankWood");

        // Large Crate: CWC / WDW / CWC  (C=diamond_block, D=medium_crate, W=plankWood) - keeps NBT
        new RecipeKeepDataShaped(new ResourceLocation(OnlyCrates.MODID, "giant_chest_large"), new ItemStack(ModBlocks.blockGiantChestLarge), new ItemStack(ModBlocks.blockGiantChestMedium),
                "CWC", "WDW", "CWC", 'C', new ItemStack(Blocks.DIAMOND_BLOCK), 'D', new ItemStack(ModBlocks.blockGiantChestMedium), 'W', "plankWood");

        // Chest To Crate Upgrade: " W " / WCW / " W "  (C=small_crate, W=plankWood)
        IRecipe recipeChestToCrateUpgrade = new ShapedOreRecipe(new ResourceLocation(OnlyCrates.MODID, "chest_to_crate_upgrade"), new ItemStack(ModItems.itemChestToCrateUpgrade),
                " W ", "WCW", " W ", 'C', new ItemStack(ModBlocks.blockGiantChest), 'W', "plankWood");
        recipeChestToCrateUpgrade.setRegistryName(new ResourceLocation(OnlyCrates.MODID, "chest_to_crate_upgrade"));
        RegistryHandler.RECIPES_TO_REGISTER.add(recipeChestToCrateUpgrade);

        // Small To Medium Crate Upgrade
        IRecipe recipeSmallToMediumCrateUpgrade = new ShapedOreRecipe(new ResourceLocation(OnlyCrates.MODID, "small_to_medium_crate_upgrade"), new ItemStack(ModItems.itemSmallToMediumCrateUpgrade),
                " W ", "WCW", " W ", 'C', new ItemStack(ModBlocks.blockGiantChestMedium), 'W', "plankWood");
        recipeSmallToMediumCrateUpgrade.setRegistryName(new ResourceLocation(OnlyCrates.MODID, "small_to_medium_crate_upgrade"));
        RegistryHandler.RECIPES_TO_REGISTER.add(recipeSmallToMediumCrateUpgrade);

        // Medium To Large Crate Upgrade
        IRecipe recipeMediumToLargeCrateUpgrade = new ShapedOreRecipe(new ResourceLocation(OnlyCrates.MODID, "medium_to_large_crate_upgrade"), new ItemStack(ModItems.itemMediumToLargeCrateUpgrade),
                " W ", "WCW", " W ", 'C', new ItemStack(ModBlocks.blockGiantChestLarge), 'W', "plankWood");
        recipeMediumToLargeCrateUpgrade.setRegistryName(new ResourceLocation(OnlyCrates.MODID, "medium_to_large_crate_upgrade"));
        RegistryHandler.RECIPES_TO_REGISTER.add(recipeMediumToLargeCrateUpgrade);

    }
}
