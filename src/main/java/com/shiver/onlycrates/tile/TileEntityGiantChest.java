package com.shiver.onlycrates.tile;

import java.util.UUID;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.blocks.BlockGiantChest;
import com.shiver.onlycrates.config.ModConfig;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.items.ItemChestToCrateUpgrade;
import com.shiver.onlycrates.network.IButtonReactor;
import com.shiver.onlycrates.storage.ChestData;
import com.shiver.onlycrates.storage.ChestDataStore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityGiantChest extends TileEntityBase implements IButtonReactor {

    private static final int SLOTS_PER_PAGE = 9 * 13;

    private UUID crateUUID;
    private int cachedPageCount;
    private String cachedDisplayName;

    public TileEntityGiantChest(int pages) {
        super("giantChest");
        this.cachedPageCount = Math.max(1, pages);
    }

    public TileEntityGiantChest() {
        this(1);
    }

    public UUID getChestUUID() {
        return this.crateUUID;
    }

    public UUID getOrCreateUUID() {
        if (this.crateUUID == null) {
            this.crateUUID = UUID.randomUUID();
        }
        return this.crateUUID;
    }

    public void setUUID(UUID uuid) {
        this.crateUUID = uuid;
    }

    public ChestData getChestData() {
        if (this.crateUUID == null || this.world == null) return null;
        return ChestDataStore.get(this.world).getData(this.crateUUID);
    }

    public void ensureDataExists() {
        if (this.crateUUID == null || this.world == null) return;
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

    private void syncCachedData() {
        ChestData data = getChestData();
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
                if (ItemChestToCrateUpgrade.pendingUpgradeUUID != null) {
                    this.crateUUID = ItemChestToCrateUpgrade.pendingUpgradeUUID;
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
            compound.setLong("CrateUUID_MSB", this.crateUUID.getMostSignificantBits());
            compound.setLong("CrateUUID_LSB", this.crateUUID.getLeastSignificantBits());
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
        } else if (type == NBTType.SAVE_TILE && this.hasWorld() && this.pos != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() instanceof BlockGiantChest) {
                int level = state.getValue(BlockGiantChest.LEVEL);
                ModConfig.CrateLevel crateLevel = ModConfig.getCrateLevel(level);
                if (crateLevel != null) {
                    this.cachedPageCount = crateLevel.getPages();
                }
            }
        }

        if (compound.hasKey("CrateUUID_MSB")) {
            this.crateUUID = new UUID(compound.getLong("CrateUUID_MSB"), compound.getLong("CrateUUID_LSB"));
        }

        if (compound.hasKey("DisplayName")) {
            this.cachedDisplayName = compound.getString("DisplayName");
        }
    }

    @Override
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
        if (this.crateUUID == null) return false;
        if (getChestData() == null) return false;
        return player.getDistanceSq(this.getPos().getX() + 0.5D, this.pos.getY() + 0.5D, this.getPos().getZ() + 0.5D) <= 64 && !this.isInvalid() && this.world.getTileEntity(this.pos) == this;
    }
}
