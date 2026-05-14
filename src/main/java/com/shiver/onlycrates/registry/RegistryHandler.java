package com.shiver.onlycrates.registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RegistryHandler {

    public static final List<Block> BLOCKS_TO_REGISTER = new ArrayList<>();
    public static final List<Item> ITEMS_TO_REGISTER = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        ModBlocks.init();
        for (Block block : BLOCKS_TO_REGISTER) {
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        ModItems.init();
        for (Item item : ITEMS_TO_REGISTER) {
            event.getRegistry().register(item);
        }
    }
}
