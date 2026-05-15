package com.shiver.onlycrates.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.shiver.onlycrates.config.ModConfig;
import com.shiver.onlycrates.tile.TileEntityGiantChest;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ChestDataStore extends WorldSavedData {

    private static final String DATA_ID = "onlycrates_chests";

    private final Map<UUID, ChestData> dataMap = new HashMap<>();
    private final Map<UUID, Long> pendingDeletions = new HashMap<>();

    public ChestDataStore() {
        super(DATA_ID);
    }

    public ChestDataStore(String name) {
        super(name);
    }

    public static ChestDataStore get(World world) {
        MapStorage storage = world.getMapStorage();
        ChestDataStore instance = (ChestDataStore) storage.getOrLoadData(ChestDataStore.class, DATA_ID);
        if (instance == null) {
            instance = new ChestDataStore();
            storage.setData(DATA_ID, instance);
        }
        return instance;
    }

    public synchronized ChestData getData(UUID uuid) {
        if (uuid == null) return null;
        if (this.pendingDeletions.containsKey(uuid)) return null;
        return this.dataMap.get(uuid);
    }

    public synchronized ChestData getOrCreateData(UUID uuid, int pageCount) {
        if (uuid == null) return null;
        this.pendingDeletions.remove(uuid);
        ChestData data = this.dataMap.get(uuid);
        if (data == null) {
            data = new ChestData(uuid, pageCount);
            this.dataMap.put(uuid, data);
            this.markDirty();
        }
        return data;
    }

    public synchronized void storeData(ChestData data) {
        if (data == null || data.getUUID() == null) return;
        this.pendingDeletions.remove(data.getUUID());
        this.dataMap.put(data.getUUID(), data);
        this.markDirty();
    }

    public synchronized void removeData(UUID uuid) {
        if (uuid == null) return;
        this.dataMap.remove(uuid);
        this.pendingDeletions.remove(uuid);
        this.markDirty();
    }

    public synchronized void markForDeletion(UUID uuid) {
        if (uuid == null) return;
        this.pendingDeletions.put(uuid, System.currentTimeMillis());
        this.markDirty();
    }

    public synchronized void cancelDeletion(UUID uuid) {
        if (uuid == null) return;
        if (this.pendingDeletions.remove(uuid) != null) {
            this.markDirty();
        }
    }

    public synchronized boolean isPendingDeletion(UUID uuid) {
        return uuid != null && this.pendingDeletions.containsKey(uuid);
    }

    public synchronized void sweepDeletions(World world) {
        long now = System.currentTimeMillis();
        long gracePeriod = ModConfig.getCleanupGracePeriodMs();
        boolean changed = false;

        Iterator<Map.Entry<UUID, Long>> it = this.pendingDeletions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            UUID uuid = entry.getKey();
            long markedAt = entry.getValue();

            if (now - markedAt < gracePeriod) continue;

            boolean hasLoadedTE = false;
            for (TileEntity te : world.loadedTileEntityList) {
                if (te instanceof TileEntityGiantChest) {
                    TileEntityGiantChest chest = (TileEntityGiantChest) te;
                    if (uuid.equals(chest.getChestUUID())) {
                        hasLoadedTE = true;
                        break;
                    }
                }
            }

            if (!hasLoadedTE) {
                this.dataMap.remove(uuid);
                it.remove();
                changed = true;
            }
        }

        if (changed) {
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.dataMap.clear();
        this.pendingDeletions.clear();

        if (nbt.hasKey("Chests")) {
            NBTTagList list = nbt.getTagList("Chests", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ChestData data = new ChestData(tag);
                this.dataMap.put(data.getUUID(), data);
            }
        }

        if (nbt.hasKey("PendingDeletions")) {
            NBTTagList list = nbt.getTagList("PendingDeletions", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                UUID uuid = new UUID(tag.getLong("UUID_MSB"), tag.getLong("UUID_LSB"));
                long timestamp = tag.getLong("Timestamp");
                this.pendingDeletions.put(uuid, timestamp);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList chestList = new NBTTagList();
        for (ChestData data : this.dataMap.values()) {
            chestList.appendTag(data.serializeNBT());
        }
        nbt.setTag("Chests", chestList);

        NBTTagList pendingList = new NBTTagList();
        for (Map.Entry<UUID, Long> entry : this.pendingDeletions.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("UUID_MSB", entry.getKey().getMostSignificantBits());
            tag.setLong("UUID_LSB", entry.getKey().getLeastSignificantBits());
            tag.setLong("Timestamp", entry.getValue());
            pendingList.appendTag(tag);
        }
        nbt.setTag("PendingDeletions", pendingList);

        return nbt;
    }
}
