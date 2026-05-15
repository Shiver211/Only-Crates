package com.shiver.onlycrates.proxy;

import com.shiver.onlycrates.Tags;
import com.shiver.onlycrates.config.ModConfig;
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
@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {

        for (int meta = 0; meta <= 2; meta++) {
            registerCrateItemModel(meta);
        }
        for (ModConfig.CrateLevel level : ModConfig.getExtraCrates()) {
            registerCrateItemModel(level.getMeta());
        }

        registerItemModel(ModItems.itemChestToCrateUpgrade, 0);
        registerItemModel(ModItems.itemSmallToMediumCrateUpgrade, 0);
        registerItemModel(ModItems.itemMediumToLargeCrateUpgrade, 0);
    }

    private static void registerCrateItemModel(int meta) {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.BLOCK_GIANT_CHEST),
                meta,
                new ModelResourceLocation(ModBlocks.BLOCK_GIANT_CHEST.getRegistryName(), "level=" + meta)
        );
    }

    private static void registerItemModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
