package com.shiver.onlycrates.items;

import java.util.UUID;

import com.shiver.onlycrates.config.ModConfig;
import com.shiver.onlycrates.storage.ChestData;
import com.shiver.onlycrates.storage.ChestDataStore;
import com.shiver.onlycrates.tile.TileEntityGiantChest;
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

    public static UUID pendingUpgradeUUID = null;

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
                    IItemHandlerModifiable sourceInventory = null;
                    if (tileHit instanceof IInventory) {
                        sourceInventory = new InvWrapper((IInventory) tileHit);
                    } else if (tileHit instanceof TileEntityInventoryBase) {
                        sourceInventory = ((TileEntityInventoryBase) tileHit).inv;
                    }

                    // Preserve UUID for crate-to-crate upgrades
                    UUID preservedUUID = null;
                    if (tileHit instanceof TileEntityGiantChest) {
                        preservedUUID = ((TileEntityGiantChest) tileHit).getChestUUID();
                    }

                    // Copy source items before block replacement
                    ItemStack[] copiedItems = null;
                    if (sourceInventory != null) {
                        copiedItems = new ItemStack[sourceInventory.getSlots()];
                        for (int i = 0; i < copiedItems.length; i++) {
                            ItemStack aStack = sourceInventory.getStackInSlot(i);
                            copiedItems[i] = StackUtil.isValid(aStack) ? aStack.copy() : ItemStack.EMPTY;
                        }
                    }

                    // For crate-to-crate upgrade: expand ChestData page count before replacing block
                    if (preservedUUID != null) {
                        ChestData data = ChestDataStore.get(world).getData(preservedUUID);
                        if (data != null) {
                            int newMeta = this.end.getBlock().getMetaFromState(this.end);
                            ModConfig.CrateLevel newLevel = ModConfig.getCrateLevel(newMeta);
                            if (newLevel != null) {
                                data.expandPages(newLevel.getPages());
                            }
                        }
                    }

                    // Set pending UUID for new TE to pick up
                    pendingUpgradeUUID = preservedUUID;

                    world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
                    world.removeTileEntity(pos);
                    world.setBlockState(pos, this.end, 2);

                    pendingUpgradeUUID = null;

                    if (!player.capabilities.isCreativeMode) heldStack.shrink(1);

                    // For vanilla chest -> crate: copy items into ChestDataStore
                    if (preservedUUID == null && copiedItems != null) {
                        TileEntity newTile = world.getTileEntity(pos);
                        if (newTile instanceof TileEntityGiantChest) {
                            TileEntityGiantChest newChest = (TileEntityGiantChest) newTile;
                            UUID newUUID = newChest.getChestUUID();
                            if (newUUID != null) {
                                ChestData data = ChestDataStore.get(world).getData(newUUID);
                                if (data != null) {
                                    IItemHandlerModifiable targetInv = data.getInventory();
                                    for (int i = 0; i < copiedItems.length && i < targetInv.getSlots(); i++) {
                                        if (StackUtil.isValid(copiedItems[i])) {
                                            targetInv.setStackInSlot(i, copiedItems[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // For crate-to-crate upgrade: UUID preserved, data stays in ChestDataStore
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
