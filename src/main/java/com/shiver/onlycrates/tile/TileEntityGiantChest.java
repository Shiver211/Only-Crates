package com.shiver.onlycrates.tile;

import java.util.UUID;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.items.ItemChestToCrateUpgrade;
import com.shiver.onlycrates.storage.ChestData;
import com.shiver.onlycrates.storage.ChestDataStore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityGiantChest extends TileEntityBase {

    private UUID crateUUID;
    private int cachedPageCount;
    private String cachedDisplayName;

    public TileEntityGiantChest(int pages) {
        super("giantChest");
        this.cachedPageCount = Math.max(1, pages);
    }

    public UUID getChestUUID() {
        return this.crateUUID;
    }

    public void setUUID(UUID uuid) {
        this.crateUUID = uuid;
    }

    public ChestData getChestData() {
        if (this.crateUUID == null || this.world == null) {
            return null;
        }
        return ChestDataStore.get(this.world).getData(this.crateUUID);
    }

    public void ensureDataExists() {
        if (this.crateUUID == null || this.world == null) {
            return;
        }
        ChestDataStore store = ChestDataStore.get(this.world);
        ChestData data = store.getData(this.crateUUID);
        if (data == null) {
            data = store.getOrCreateData(this.crateUUID, this.cachedPageCount);
        }
        if (data != null) {
            this.cachedPageCount = data.getPageCount();
            this.cachedDisplayName = data.getCustomDisplayName();
        }
    }

    public int getPageCount() {
        return this.cachedPageCount;
    }

    @Override
    public ITextComponent getDisplayName() {
        if (this.cachedDisplayName != null && !this.cachedDisplayName.isEmpty()) {
            return new TextComponentString(this.cachedDisplayName);
        }
        return new TextComponentTranslation(this.getNameForTranslation());
    }

    @Override
    public void validate() {
        super.validate();
        if (this.world != null && !this.world.isRemote) {
            if (this.crateUUID == null) {
                UUID pending = ItemChestToCrateUpgrade.pendingUpgradeUUID.get();
                if (pending != null) {
                    this.crateUUID = pending;
                } else {
                    this.crateUUID = UUID.randomUUID();
                }
                ensureDataExists();
            } else {
                ChestDataStore.get(this.world).cancelDeletion(this.crateUUID);
                ensureDataExists();
            }
            sendUpdate();
        }
    }

    @Override
    public IItemHandler getItemHandler(EnumFacing facing) {
        ChestData data = getChestData();
        return data != null ? data.getInventory() : null;
    }

    @Override
    public int getComparatorStrength() {
        ChestData data = getChestData();
        if (data != null) {
            return ItemHandlerHelper.calcRedstoneFromInventory(data.getInventory());
        }
        return 0;
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.writeSyncableNBT(compound, type);

        if (this.crateUUID != null) {
            compound.setString("uuid", this.crateUUID.toString());
        }

        compound.setInteger("PageCount", this.cachedPageCount);
        if (this.cachedDisplayName != null && !this.cachedDisplayName.isEmpty()) {
            compound.setString("DisplayName", this.cachedDisplayName);
        }
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.readSyncableNBT(compound, type);

        if (compound.hasKey("PageCount")) {
            this.cachedPageCount = Math.max(1, compound.getInteger("PageCount"));
        }

        if (compound.hasKey("uuid")) {
            this.crateUUID = UUID.fromString(compound.getString("uuid"));
        }

        if (compound.hasKey("DisplayName")) {
            this.cachedDisplayName = compound.getString("DisplayName");
        }
    }

    public void onButtonPressed(int buttonID, EntityPlayer player) {
        if (player != null && this.pos != null) {
            if (buttonID >= 0 && buttonID < this.cachedPageCount) {
                player.openGui(OnlyCrates.INSTANCE, GuiHandler.GUI_GIANT_CHEST_BASE + buttonID, this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
            }
        }
    }

    public void fillWithLoot(EntityPlayer player) {
        ChestData data = getChestData();
        if (data != null) {
            data.fillWithLoot(player, this.world);
        }
    }

    @Override
    public boolean canPlayerUse(EntityPlayer player) {
        if (this.crateUUID == null) {
            return false;
        }
        if (getChestData() == null) {
            return false;
        }
        return player.getDistanceSq(this.getPos().getX() + 0.5D, this.pos.getY() + 0.5D, this.getPos().getZ() + 0.5D) <= 64 && !this.isInvalid() && this.world.getTileEntity(this.pos) == this;
    }
}
