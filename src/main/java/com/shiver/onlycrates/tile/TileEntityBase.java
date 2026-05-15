package com.shiver.onlycrates.tile;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.Tags;
import com.shiver.onlycrates.util.VanillaPacketDispatcher;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityBase extends TileEntity implements ITickable {

    public final String name;
    protected int ticksElapsed;

    public TileEntityBase(String name) {
        this.name = name;
    }

    public static void init() {
        OnlyCrates.LOGGER.info("Registering TileEntities...");
        GameRegistry.registerTileEntity(TileEntityGiantChest.class, new net.minecraft.util.ResourceLocation(Tags.MOD_ID, "giantChest"));
    }

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.writeSyncableNBT(compound, NBTType.SAVE_TILE);
        return compound;
    }

    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        this.readSyncableNBT(compound, NBTType.SAVE_TILE);
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeSyncableNBT(compound, NBTType.SYNC);
        return new SPacketUpdateTileEntity(this.pos, -1, compound);
    }

    @Override
    public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readSyncableNBT(pkt.getNbtCompound(), NBTType.SYNC);
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeSyncableNBT(compound, NBTType.SYNC);
        return compound;
    }

    @Override
    public final void handleUpdateTag(NBTTagCompound compound) {
        this.readSyncableNBT(compound, NBTType.SYNC);
    }

    public final void sendUpdate() {
        if (this.world != null && !this.world.isRemote) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        }
    }

    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        if (type != NBTType.SAVE_BLOCK) {
            super.writeToNBT(compound);
        }

        if (type == NBTType.SAVE_TILE) {
            compound.setInteger("TicksElapsed", this.ticksElapsed);
        }
    }

    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        if (type != NBTType.SAVE_BLOCK) {
            super.readFromNBT(compound);
        }

        if (type == NBTType.SAVE_TILE) {
            this.ticksElapsed = compound.getInteger("TicksElapsed");
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return !oldState.getBlock().isAssociatedBlock(newState.getBlock());
    }

    public String getNameForTranslation() {
        return "container." + Tags.MOD_ID + "." + this.name + ".name";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getNameForTranslation());
    }

    @Override
    public final void update() {
        this.updateEntity();
    }

    public void updateEntity() {
        this.ticksElapsed++;
    }

    public int getComparatorStrength() {
        return 0;
    }

    public boolean canPlayerUse(EntityPlayer player) {
        return player.getDistanceSq(this.getPos().getX() + 0.5D, this.pos.getY() + 0.5D, this.getPos().getZ() + 0.5D) <= 64 && !this.isInvalid() && this.world.getTileEntity(this.pos) == this;
    }

    public IItemHandler getItemHandler(EnumFacing facing) {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return this.getCapability(capability, facing) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            IItemHandler handler = this.getItemHandler(facing);
            if (handler != null) { return (T) handler; }
        }
        return super.getCapability(capability, facing);
    }

    public enum NBTType {
        SAVE_TILE,
        SYNC,
        SAVE_BLOCK
    }
}
