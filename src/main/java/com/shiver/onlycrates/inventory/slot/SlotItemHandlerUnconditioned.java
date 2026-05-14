package com.shiver.onlycrates.inventory.slot;

import com.shiver.onlycrates.util.ItemStackHandlerAA;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotItemHandlerUnconditioned extends SlotItemHandler {

    private final ItemStackHandlerAA inv;

    public SlotItemHandlerUnconditioned(ItemStackHandlerAA inv, int index, int xPosition, int yPosition) {
        super(inv, index, xPosition, yPosition);
        this.inv = inv;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (!this.inv.canAccept(this.getSlotIndex(), stack, false)) return false;
        ItemStack remainder = this.inv.insertItem(this.getSlotIndex(), stack, true, false);
        return remainder.isEmpty() || remainder.getCount() < stack.getCount();
    }

    @Override
    public ItemStack getStack() {
        return this.inv.getStackInSlot(this.getSlotIndex());
    }

    @Override
    public void putStack(ItemStack stack) {
        this.inv.setStackInSlot(this.getSlotIndex(), stack);
        this.onSlotChanged();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        ItemStack remainder = this.inv.insertItem(this.getSlotIndex(), stack, true, false);
        return stack.getCount() - remainder.getCount();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return this.inv.extractItem(this.getSlotIndex(), 1, true, false).isEmpty() == false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return this.inv.extractItem(this.getSlotIndex(), amount, false, false);
    }
}
