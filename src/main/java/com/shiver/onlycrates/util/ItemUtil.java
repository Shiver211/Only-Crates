package com.shiver.onlycrates.util;

import com.shiver.onlycrates.Tags;
import com.shiver.onlycrates.blocks.ItemBlockBase;
import com.shiver.onlycrates.creative.OnlyCratesTab;
import com.shiver.onlycrates.registry.RegistryHandler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class ItemUtil {

    public static void registerBlock(Block block, ItemBlockBase itemBlock, String name, boolean addTab) {
        block.setTranslationKey(Tags.MOD_ID + "." + name);
        block.setRegistryName(Tags.MOD_ID, name);
        RegistryHandler.BLOCKS_TO_REGISTER.add(block);

        itemBlock.setRegistryName(block.getRegistryName());
        RegistryHandler.ITEMS_TO_REGISTER.add(itemBlock);

        block.setCreativeTab(addTab ? OnlyCratesTab.INSTANCE : null);
    }

    public static void registerItem(Item item, String name, boolean addTab) {
        item.setTranslationKey(Tags.MOD_ID + "." + name);
        item.setRegistryName(Tags.MOD_ID, name);
        RegistryHandler.ITEMS_TO_REGISTER.add(item);
        item.setCreativeTab(addTab ? OnlyCratesTab.INSTANCE : null);
    }
}
