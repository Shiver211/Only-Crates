package com.shiver.onlycrates.proxy;

import java.util.Objects;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.client.CustomCrateResourcePack;
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

    @Override
    public void preInit() {
        CustomCrateResourcePack.register();
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChest));
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChestMedium));
        registerItemModel(Item.getItemFromBlock(ModBlocks.blockGiantChestLarge));
        for (Block block : ModBlocks.getExtraCrates()) {
            registerExtraCrateModel(block);
        }

        registerItemModel(ModItems.itemChestToCrateUpgrade);
        registerItemModel(ModItems.itemSmallToMediumCrateUpgrade);
        registerItemModel(ModItems.itemMediumToLargeCrateUpgrade);
    }

    private static void registerItemModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
    }

    private static void registerExtraCrateModel(Block block) {
        ResourceLocation registryName = Objects.requireNonNull(block.getRegistryName());
        ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(registryName, "normal");
            }
        });
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(registryName, "inventory"));
    }
}
