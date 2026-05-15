package com.shiver.onlycrates.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public final class VanillaPacketDispatcher {

    public static void dispatchTEToNearbyPlayers(TileEntity tile) {
        WorldServer world = (WorldServer) tile.getWorld();
        PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(tile.getPos().getX() >> 4, tile.getPos().getZ() >> 4);

        if (entry == null) {
            return;
        }

        for (EntityPlayerMP player : entry.getWatchingPlayers()) {
            player.connection.sendPacket(tile.getUpdatePacket());
        }
    }
}
