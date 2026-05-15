package com.shiver.onlycrates.inventory;

import com.shiver.onlycrates.inventory.slot.SlotItemHandlerUnconditioned;
import com.shiver.onlycrates.tile.TileEntityBase;
import com.shiver.onlycrates.tile.TileEntityCrate;
import com.shiver.onlycrates.util.StackUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCrate extends Container {

    public final TileEntityCrate tileChest;

    public ContainerCrate(InventoryPlayer inventory, TileEntityBase tile, int page) {
        this.tileChest = (TileEntityCrate) tile;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 13; j++) {
                this.addSlotToContainer(new SlotItemHandlerUnconditioned(this.tileChest.inv, 9 * 13 * page + j + i * 13, 5 + j * 18, 5 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 33 + 8 + j * 18, 172 + 4 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 33 + 8 + i * 18, 172 + 62));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        int inventoryStart = 117;
        int inventoryEnd = inventoryStart + 26;
        int hotbarStart = inventoryEnd + 1;
        int hotbarEnd = hotbarStart + 8;

        Slot theSlot = this.inventorySlots.get(slot);

        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack newStack = theSlot.getStack();
            ItemStack currentStack = newStack.copy();

            if (slot >= inventoryStart) {
                if (!this.mergeItemStack(newStack, 0, 117, false)) {
                    if (slot >= inventoryStart && slot <= inventoryEnd) {
                        if (!this.mergeItemStack(newStack, hotbarStart, hotbarEnd + 1, false)) { return StackUtil.getEmpty(); }
                    } else if (slot >= inventoryEnd + 1 && slot < hotbarEnd + 1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd + 1, false)) { return StackUtil.getEmpty(); }
                }
            } else if (!this.mergeItemStack(newStack, inventoryStart, hotbarEnd + 1, true)) { return StackUtil.getEmpty(); }

            if (!StackUtil.isValid(newStack)) {
                theSlot.putStack(StackUtil.getEmpty());
            } else {
                theSlot.onSlotChanged();
            }

            if (newStack.getCount() == currentStack.getCount()) { return StackUtil.getEmpty(); }
            theSlot.onTake(player, newStack);

            return currentStack;
        }
        return StackUtil.getEmpty();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tileChest.canPlayerUse(player);
    }
}
