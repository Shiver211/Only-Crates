package com.shiver.onlycrates.storage;

import java.util.UUID;

import com.shiver.onlycrates.util.AwfulUtil;
import com.shiver.onlycrates.util.ItemStackHandlerAA;
import com.shiver.onlycrates.util.StackUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class ChestData {

    public static final int SLOTS_PER_PAGE = 9 * 13;

    private final UUID uuid;
    private ItemStackHandlerAA inv;
    private int pageCount;
    private String customDisplayName;
    private ResourceLocation lootTable;
    private boolean dirty;

    public ChestData(UUID uuid, int pageCount) {
        this.uuid = uuid;
        this.pageCount = Math.max(1, pageCount);
        this.inv = new ChestStackHandler(this.pageCount * SLOTS_PER_PAGE);
    }

    public ChestData(NBTTagCompound tag) {
        this.uuid = UUID.fromString(tag.getString("uuid"));
        this.pageCount = Math.max(1, tag.getInteger("PageCount"));
        this.inv = new ChestStackHandler(this.pageCount * SLOTS_PER_PAGE);

        if (tag.hasKey("Items")) {
            NBTTagList list = tag.getTagList("Items", 10);
            for (int i = 0; i < Math.min(list.tagCount(), this.inv.getSlots()); i++) {
                NBTTagCompound compound = list.getCompoundTagAt(i);
                if (compound != null && compound.hasKey("id")) {
                    this.inv.setStackInSlot(i, new ItemStack(compound));
                }
            }
        }

        if (tag.hasKey("DisplayName")) {
            this.customDisplayName = tag.getString("DisplayName");
        }
        if (tag.hasKey("LootTable")) {
            this.lootTable = new ResourceLocation(tag.getString("LootTable"));
        }
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("uuid", this.uuid.toString());
        tag.setInteger("PageCount", this.pageCount);

        if (this.customDisplayName != null && !this.customDisplayName.isEmpty()) {
            tag.setString("DisplayName", this.customDisplayName);
        }
        if (this.lootTable != null) {
            tag.setString("LootTable", this.lootTable.toString());
        }

        if (this.inv.getSlots() > 0) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < this.inv.getSlots(); i++) {
                ItemStack stack = this.inv.getStackInSlot(i);
                NBTTagCompound slotTag = new NBTTagCompound();
                if (StackUtil.isValid(stack)) {
                    stack.writeToNBT(slotTag);
                }
                list.appendTag(slotTag);
            }
            tag.setTag("Items", list);
        }

        return tag;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public ItemStackHandlerAA getInventory() {
        return this.inv;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public String getCustomDisplayName() {
        return this.customDisplayName;
    }

    public void setCustomDisplayName(String name) {
        this.customDisplayName = name;
        this.dirty = true;
    }

    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(ResourceLocation lootTable) {
        this.lootTable = lootTable;
        this.dirty = true;
    }

    public void expandPages(int newPageCount) {
        if (newPageCount <= this.pageCount) {
            return;
        }
        ItemStackHandlerAA newInv = new ChestStackHandler(newPageCount * SLOTS_PER_PAGE);
        for (int i = 0; i < this.inv.getSlots(); i++) {
            newInv.setStackInSlot(i, this.inv.getStackInSlot(i));
        }
        this.inv = newInv;
        this.pageCount = newPageCount;
        this.dirty = true;
    }

    public void fillWithLoot(EntityPlayer player, World world) {
        if (this.lootTable != null && !world.isRemote && world instanceof WorldServer) {
            LootTable table = world.getLootTableManager().getLootTableFromLocation(this.lootTable);
            this.lootTable = null;
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            if (player != null) {
                builder.withLuck(player.getLuck());
            }
            AwfulUtil.fillInventory(table, this.inv, world.rand, builder.build());
            this.dirty = true;
        }
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    private class ChestStackHandler extends ItemStackHandlerAA {

        ChestStackHandler(int slots) {
            super(slots);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ChestData.this.dirty = true;
        }
    }
}
