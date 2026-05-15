package com.shiver.onlycrates.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityGiantChestConfigurable extends TileEntityGiantChest {

    private static final int SLOTS_PER_PAGE = 9 * 13;
    private static final String TAG_PAGE_COUNT = "PageCount";
    private static final String TAG_DISPLAY_NAME = "DisplayName";

    private int pageCount = 1;

    public TileEntityGiantChestConfigurable() {
        this(1, null);
    }

    public TileEntityGiantChestConfigurable(int pageCount, String displayName) {
        super(getSlotCount(pageCount), "giantChestConfigurable", displayName);
        this.pageCount = clampPages(pageCount);
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.writeSyncableNBT(compound, type);
        compound.setInteger(TAG_PAGE_COUNT, this.pageCount);
        String displayName = this.getCustomDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            compound.setString(TAG_DISPLAY_NAME, displayName);
        }
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        if (compound.hasKey(TAG_PAGE_COUNT)) {
            this.pageCount = clampPages(compound.getInteger(TAG_PAGE_COUNT));
        }
        this.setInventorySize(getSlotCount(this.pageCount));
        if (compound.hasKey(TAG_DISPLAY_NAME)) {
            this.setCustomDisplayName(compound.getString(TAG_DISPLAY_NAME));
        }
        super.readSyncableNBT(compound, type);
    }

    @Override
    public int getPageCount() {
        return this.pageCount;
    }

    private static int clampPages(int pages) {
        return Math.max(pages, 1);
    }

    private static int getSlotCount(int pages) {
        return clampPages(pages) * SLOTS_PER_PAGE;
    }
}
