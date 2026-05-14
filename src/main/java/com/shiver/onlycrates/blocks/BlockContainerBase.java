package com.shiver.onlycrates.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.shiver.onlycrates.tile.TileEntityBase;
import com.shiver.onlycrates.tile.TileEntityInventoryBase;
import com.shiver.onlycrates.util.ItemUtil;
import com.shiver.onlycrates.util.StackUtil;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockContainerBase extends BlockContainer implements ItemBlockBase.ICustomRarity {

    private final String name;

    public BlockContainerBase(Material material, String name) {
        super(material);
        this.name = name;
        this.register();
    }

    private void register() {
        ItemUtil.registerBlock(this, this.getItemBlock(), this.getBaseName(), this.shouldAddCreative());
    }

    protected String getBaseName() {
        return this.name;
    }

    protected ItemBlockBase getItemBlock() {
        return new ItemBlockBase(this);
    }

    public boolean shouldAddCreative() {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.COMMON;
    }

    private void dropInventory(World world, BlockPos position) {
        if (!world.isRemote) {
            TileEntity aTile = world.getTileEntity(position);
            if (aTile instanceof TileEntityInventoryBase) {
                TileEntityInventoryBase tile = (TileEntityInventoryBase) aTile;
                if (tile.inv.getSlots() > 0) {
                    for (int i = 0; i < tile.inv.getSlots(); i++) {
                        this.dropSlotFromInventory(i, tile, world, position);
                    }
                }
            }
        }
    }

    private void dropSlotFromInventory(int i, TileEntityInventoryBase tile, World world, BlockPos pos) {
        ItemStack stack = tile.inv.getStackInSlot(i);
        if (StackUtil.isValid(stack)) {
            float dX = world.rand.nextFloat() * 0.8F + 0.1F;
            float dY = world.rand.nextFloat() * 0.8F + 0.1F;
            float dZ = world.rand.nextFloat() * 0.8F + 0.1F;
            EntityItem entityItem = new EntityItem(world, pos.getX() + dX, pos.getY() + dY, pos.getZ() + dZ, stack.copy());
            float factor = 0.05F;
            entityItem.motionX = world.rand.nextGaussian() * factor;
            entityItem.motionY = world.rand.nextGaussian() * factor + 0.2F;
            entityItem.motionZ = world.rand.nextGaussian() * factor;
            world.spawnEntity(entityItem);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        if (stack.hasTagCompound()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityBase) {
                TileEntityBase base = (TileEntityBase) tile;
                NBTTagCompound compound = stack.getTagCompound().getCompoundTag("Data");
                if (compound != null) {
                    base.readSyncableNBT(compound, TileEntityBase.NBTType.SAVE_BLOCK);
                }
            }
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBase) { return ((TileEntityBase) tile).getComparatorStrength(); }
        return 0;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBase) {
            TileEntityBase base = (TileEntityBase) tile;
            NBTTagCompound data = new NBTTagCompound();
            base.writeSyncableNBT(data, TileEntityBase.NBTType.SAVE_BLOCK);

            List<String> keysToRemove = new ArrayList<>();
            for (String key : data.getKeySet()) {
                NBTBase tag = data.getTag(key);
                if (tag instanceof NBTTagInt) {
                    if (((NBTTagInt) tag).getInt() == 0) {
                        keysToRemove.add(key);
                    }
                }
            }
            for (String key : keysToRemove) {
                data.removeTag(key);
            }

            ItemStack stack = new ItemStack(this.getItemDropped(state, tile.getWorld().rand, fortune), 1, this.damageDropped(state));
            if (!data.isEmpty()) {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag("Data", data);
            }

            drops.add(stack);
        } else {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (this.shouldDropInventory(world, pos)) {
            this.dropInventory(world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    public boolean shouldDropInventory(World world, BlockPos pos) {
        return true;
    }
}
