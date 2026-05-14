package com.shiver.onlycrates.tile;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.network.IButtonReactor;
import com.shiver.onlycrates.util.AwfulUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class TileEntityGiantChest extends TileEntityInventoryBase implements IButtonReactor, ILootContainer {

    private static final int SLOTS_PER_PAGE = 9 * 13;

    public ResourceLocation lootTable;
    private String customDisplayName;

    public TileEntityGiantChest(int slotAmount, String name) {
        this(slotAmount, name, null);
    }

    public TileEntityGiantChest(int slotAmount, String name, String customDisplayName) {
        super(slotAmount, name);
        this.customDisplayName = customDisplayName;
    }

    public TileEntityGiantChest() {
        this(9 * 13, "giantChest");
    }

    public int getPageCount() {
        int slots = this.inv != null ? this.inv.getSlots() : 0;
        return Math.max(1, (slots + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE);
    }

    protected void setCustomDisplayName(String customDisplayName) {
        this.customDisplayName = customDisplayName;
    }

    protected String getCustomDisplayName() {
        return this.customDisplayName;
    }

    @Override
    public ITextComponent getDisplayName() {
        if (this.customDisplayName != null && !this.customDisplayName.isEmpty()) {
            return new TextComponentString(this.customDisplayName);
        }
        return super.getDisplayName();
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.writeSyncableNBT(compound, type);
        if (this.lootTable != null) {
            compound.setString("LootTable", this.lootTable.toString());
        }
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.readSyncableNBT(compound, type);
        if (compound.hasKey("LootTable")) {
            this.lootTable = new ResourceLocation(compound.getString("LootTable"));
        }
    }

    @Override
    public void onButtonPressed(int buttonID, EntityPlayer player) {
        if (player != null && this.pos != null) {
            int pageCount = this.getPageCount();
            if (buttonID >= 0 && buttonID < pageCount) {
                player.openGui(OnlyCrates.INSTANCE, GuiHandler.GUI_GIANT_CHEST_BASE + buttonID, this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
            }
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public void fillWithLoot(EntityPlayer player) {
        if (this.lootTable != null && !this.world.isRemote && this.world instanceof WorldServer) {
            LootTable table = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable);
            this.lootTable = null;
            LootContext.Builder builder = new LootContext.Builder((WorldServer) this.world);
            if (player != null) {
                builder.withLuck(player.getLuck());
            }
            AwfulUtil.fillInventory(table, this.inv, this.world.rand, builder.build());
        }
    }
}
