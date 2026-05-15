package com.shiver.onlycrates.creative;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.registry.ModBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class OnlyCratesTab extends CreativeTabs {

    public static final OnlyCratesTab INSTANCE = new OnlyCratesTab();

    public OnlyCratesTab() {
        super(OnlyCrates.MODID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.blockCrate);
    }
}
