package com.shiver.onlycrates.tile;

import com.shiver.onlycrates.blocks.BlockGiantChest;
import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.config.ModConfig;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.network.IButtonReactor;
import com.shiver.onlycrates.util.AwfulUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class TileEntityGiantChest extends TileEntityInventoryBase implements IButtonReactor, ILootContainer {

    private static final int SLOTS_PER_PAGE = 9 * 13;
    private static final String TAG_PAGE_COUNT = "PageCount";
    private static final String TAG_DISPLAY_NAME = "DisplayName";

    public ResourceLocation lootTable;
    private int pageCount;
    private String customDisplayName;

    public TileEntityGiantChest(int slotAmount, String name, String customDisplayName) {
        super(slotAmount, name);
        this.pageCount = Math.max(1, (slotAmount + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE);
        this.customDisplayName = customDisplayName;
    }

    public TileEntityGiantChest(int slotAmount, String name) {
        this(slotAmount, name, null);
    }

    public TileEntityGiantChest(int pages) {
        this(pages * SLOTS_PER_PAGE, "giantChest", null);
    }

    public TileEntityGiantChest() {
        this(9 * 13, "giantChest", null);
    }

    public int getPageCount() {
        return this.pageCount;
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
        return new TextComponentTranslation(this.getNameForTranslation());
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, NBTType type) {
        super.writeSyncableNBT(compound, type);
        if (this.lootTable != null) {
            compound.setString("LootTable", this.lootTable.toString());
        }
        if (type == NBTType.SAVE_BLOCK || type == NBTType.SAVE_TILE) {
            compound.setInteger(TAG_PAGE_COUNT, this.pageCount);
            if (this.customDisplayName != null && !this.customDisplayName.isEmpty()) {
                compound.setString(TAG_DISPLAY_NAME, this.customDisplayName);
            }
        }
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, NBTType type) {
        // Read page count and resize inventory BEFORE loading items
        if (type == NBTType.SAVE_TILE && compound.hasKey(TAG_PAGE_COUNT)) {
            this.pageCount = Math.max(1, compound.getInteger(TAG_PAGE_COUNT));
            this.setInventorySize(this.pageCount * SLOTS_PER_PAGE);
        } else if (type == NBTType.SAVE_TILE && this.hasWorld() && this.pos != null) {
            // Defensive: resize from block state if PageCount not in NBT
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() instanceof BlockGiantChest) {
                int level = state.getValue(BlockGiantChest.LEVEL);
                ModConfig.CrateLevel crateLevel = ModConfig.getCrateLevel(level);
                if (crateLevel != null) {
                    this.pageCount = crateLevel.getPages();
                    this.setInventorySize(this.pageCount * SLOTS_PER_PAGE);
                }
            }
        }
        if (compound.hasKey(TAG_DISPLAY_NAME)) {
            this.customDisplayName = compound.getString(TAG_DISPLAY_NAME);
        }
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
