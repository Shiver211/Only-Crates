package com.shiver.onlycrates.storage;

import java.util.WeakHashMap;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class CleanupHandler {

    private static final int SWEEP_INTERVAL = 6000; // 5 minutes
    private static final WeakHashMap<World, Integer> TICK_COUNTERS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        World world = event.world;
        if (world.isRemote) return;

        int ticks = TICK_COUNTERS.getOrDefault(world, 0) + 1;
        if (ticks >= SWEEP_INTERVAL) {
            ticks = 0;
            ChestDataStore.get(world).sweepDeletions(world);
        }
        TICK_COUNTERS.put(world, ticks);
    }
}
