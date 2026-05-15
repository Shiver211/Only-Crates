package com.shiver.onlycrates.items;

import com.shiver.onlycrates.tile.TileEntityInventoryBase;
import com.shiver.onlycrates.util.StackUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class ItemChestToCrateUpgrade extends Item {

    private final Class<? extends TileEntity> start;
    @Nullable
    private final IBlockState startState;
    private final IBlockState end;

    public ItemChestToCrateUpgrade(String name, Class<? extends TileEntity> start, @Nullable IBlockState startState, IBlockState end) {
        this.start = start;
        this.startState = startState;
        this.end = end;
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            TileEntity tileHit = world.getTileEntity(pos);
            boolean matches = false;
            if (this.startState != null) {
                IBlockState currentState = world.getBlockState(pos);
                matches = currentState.equals(this.startState);
            } else {
                matches = tileHit != null && this.start.isInstance(tileHit);
            }
            if (tileHit != null && matches) {
                if (!world.isRemote) {
                    IItemHandlerModifiable chest = null;
                    if (tileHit instanceof IInventory) {
                        chest = new InvWrapper((IInventory) tileHit);
                    } else if (tileHit instanceof TileEntityInventoryBase) {
                        chest = ((TileEntityInventoryBase) tileHit).inv;
                    }

                    if (chest != null) {
                        ItemStack[] stacks = new ItemStack[chest.getSlots()];
                        for (int i = 0; i < stacks.length; i++) {
                            ItemStack aStack = chest.getStackInSlot(i);
                            stacks[i] = aStack.copy();
                        }

                        world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
                        world.removeTileEntity(pos);
                        world.setBlockState(pos, this.end, 2);
                        if (!player.capabilities.isCreativeMode) heldStack.shrink(1);

                        TileEntity newTileHit = world.getTileEntity(pos);
                        if (newTileHit instanceof TileEntityInventoryBase) {
                            IItemHandlerModifiable newChest = ((TileEntityInventoryBase) newTileHit).inv;
                            for (int i = 0; i < stacks.length; i++) {
                                if (StackUtil.isValid(stacks[i])) {
                                    if (newChest.getSlots() > i) {
                                        newChest.setStackInSlot(i, stacks[i].copy());
                                    }
                                }
                            }
                        }
                    }
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
