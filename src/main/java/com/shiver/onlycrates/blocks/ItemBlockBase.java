package com.shiver.onlycrates.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBase extends ItemBlock {

    public ItemBlockBase(Block block) {
        super(block);
    }

    @Override
    public EnumRarity getForgeRarity(ItemStack stack) {
        if (this.block instanceof ICustomRarity) {
            return ((ICustomRarity) this.block).getRarity(stack);
        }
        return EnumRarity.COMMON;
    }

    public interface ICustomRarity {
        EnumRarity getRarity(ItemStack stack);
    }
}
