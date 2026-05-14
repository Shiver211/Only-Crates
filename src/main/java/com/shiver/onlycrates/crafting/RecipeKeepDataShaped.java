package com.shiver.onlycrates.crafting;

import com.shiver.onlycrates.registry.RegistryHandler;
import com.shiver.onlycrates.util.StackUtil;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeKeepDataShaped extends ShapedOreRecipe {

    private final ItemStack nbtCopyStack;

    public RecipeKeepDataShaped(ResourceLocation group, ItemStack result, ItemStack nbtCopyStack, Object... recipe) {
        super(group, result, recipe);
        this.nbtCopyStack = nbtCopyStack;
        this.setRegistryName(group);
        RegistryHandler.RECIPES_TO_REGISTER.add(this);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack stack = super.getCraftingResult(inventory);
        if (StackUtil.isValid(stack)) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack input = inventory.getStackInSlot(i);
                if (this.nbtCopyStack.getItem() == input.getItem() && this.nbtCopyStack.getMetadata() == input.getMetadata()) {
                    stack.setTagCompound(input.getTagCompound());
                    break;
                }
            }
        }
        return stack;
    }
}
