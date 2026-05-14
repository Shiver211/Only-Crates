package com.shiver.onlycrates.util;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public final class StackUtil {

    public static boolean isValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    public static ItemStack getEmpty() {
        return ItemStack.EMPTY;
    }

    public static NonNullList<ItemStack> makeList(int size) {
        return NonNullList.withSize(size, getEmpty());
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
                if (!isValid(stack) && !isValid(list.get(i)) || areItemsEqual(stack, list.get(i), checkWildcard)) {
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
        return isValid(stack1) && isValid(stack2) && (stack1.isItemEqual(stack2) || checkWildcard && stack1.getItem() == stack2.getItem() && (stack1.getItemDamage() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE));
    }
}
