package com.shiver.onlycrates.proxy;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.registry.ModBlocks;
import com.shiver.onlycrates.registry.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = OnlyCrates.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private static final ModelResourceLocation CRATE_BLOCK_MODEL = new ModelResourceLocation(new ResourceLocation(OnlyCrates.MODID, "block_giant_chest"), "normal");
    private static final ModelResourceLocation CRATE_ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(OnlyCrates.MODID, "block_giant_chest"), "inventory");
    private static final StateMapperBase CRATE_STATE_MAPPER = new StateMapperBase() {
        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return CRATE_BLOCK_MODEL;
        }
    };

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChest), 0);
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChestMedium), 0);
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChestLarge), 0);
        for (Block block : ModBlocks.getExtraCrates()) {
            registerExtraCrateModel(block);
        }

        registerItemModel(ModItems.itemChestToCrateUpgrade, 0);
        registerItemModel(ModItems.itemSmallToMediumCrateUpgrade, 0);
        registerItemModel(ModItems.itemMediumToLargeCrateUpgrade, 0);
    }

    private static void registerItemModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void registerExtraCrateModel(Block block) {
        ModelLoader.setCustomStateMapper(block, CRATE_STATE_MAPPER);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, CRATE_ITEM_MODEL);
    }
}
