package com.shiver.onlycrates.util;

import net.minecraft.item.ItemStack;

public final class StackUtil {

    public static boolean isValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    public static ItemStack getEmpty() {
        return ItemStack.EMPTY;
    }
}
