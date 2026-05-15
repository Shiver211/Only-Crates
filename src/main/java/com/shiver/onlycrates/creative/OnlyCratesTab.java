package com.shiver.onlycrates.creative;

import com.shiver.onlycrates.Tags;
import com.shiver.onlycrates.registry.ModBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class OnlyCratesTab extends CreativeTabs {

    public static final OnlyCratesTab INSTANCE = new OnlyCratesTab();

    public OnlyCratesTab() {
        super(Tags.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.BLOCK_GIANT_CHEST, 1, 0);
    }
}
