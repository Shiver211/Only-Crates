package com.shiver.onlycrates.proxy;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.registry.ModBlocks;
import com.shiver.onlycrates.registry.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = OnlyCrates.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        // Block item models
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChest), 0);
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChestMedium), 0);
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChestLarge), 0);

        // Item models
        registerItemModel(ModItems.itemChestToCrateUpgrade, 0);
        registerItemModel(ModItems.itemSmallToMediumCrateUpgrade, 0);
        registerItemModel(ModItems.itemMediumToLargeCrateUpgrade, 0);
    }

    private static void registerItemModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
