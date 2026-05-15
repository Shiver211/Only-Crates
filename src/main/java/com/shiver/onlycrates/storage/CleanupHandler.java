package com.shiver.onlycrates.storage;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class CleanupHandler {

    private static final long SWEEP_INTERVAL = 6000L; // 5 minutes

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        World world = event.world;
        if (world.isRemote) {
            return;
        }

        if (world.getTotalWorldTime() % SWEEP_INTERVAL == 0) {
            ChestDataStore.get(world).sweepDeletions(world);
        }
    }
}
