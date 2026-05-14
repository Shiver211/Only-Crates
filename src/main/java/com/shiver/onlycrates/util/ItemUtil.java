package com.shiver.onlycrates.util;

import java.util.Arrays;
import java.util.List;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.blocks.ItemBlockBase;
import com.shiver.onlycrates.creative.OnlyCratesTab;
import com.shiver.onlycrates.registry.RegistryHandler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemUtil {

    public static void registerBlock(Block block, ItemBlockBase itemBlock, String name, boolean addTab) {
        block.setTranslationKey(OnlyCrates.MODID + "." + name);
        block.setRegistryName(OnlyCrates.MODID, name);
        RegistryHandler.BLOCKS_TO_REGISTER.add(block);

        itemBlock.setRegistryName(block.getRegistryName());
        RegistryHandler.ITEMS_TO_REGISTER.add(itemBlock);

        block.setCreativeTab(addTab ? OnlyCratesTab.INSTANCE : null);
    }

    public static void registerItem(Item item, String name, boolean addTab) {
        item.setTranslationKey(OnlyCrates.MODID + "." + name);
        item.setRegistryName(OnlyCrates.MODID, name);
        RegistryHandler.ITEMS_TO_REGISTER.add(item);
        item.setCreativeTab(addTab ? OnlyCratesTab.INSTANCE : null);
    }

    public static boolean contains(ItemStack[] array, ItemStack stack, boolean checkWildcard) {
        return getPlaceAt(array, stack, checkWildcard) != -1;
    }

    public static int getPlaceAt(ItemStack[] array, ItemStack stack, boolean checkWildcard) {
        return getPlaceAt(Arrays.asList(array), stack, checkWildcard);
    }

    public static int getPlaceAt(List<ItemStack> list, ItemStack stack, boolean checkWildcard) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (!StackUtil.isValid(stack) && !StackUtil.isValid(list.get(i)) || areItemsEqual(stack, list.get(i), checkWildcard)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean contains(List<ItemStack> list, ItemStack stack, boolean checkWildcard) {
        return !(list == null || list.isEmpty()) && getPlaceAt(list, stack, checkWildcard) != -1;
    }

    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2, boolean checkWildcard) {
        return StackUtil.isValid(stack1) && StackUtil.isValid(stack2) && (stack1.isItemEqual(stack2) || checkWildcard && stack1.getItem() == stack2.getItem() && (stack1.getItemDamage() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE));
    }
}
