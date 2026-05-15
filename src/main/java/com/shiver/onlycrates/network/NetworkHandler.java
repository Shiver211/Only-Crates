package com.shiver.onlycrates.network;

import java.util.UUID;

import com.shiver.onlycrates.Tags;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static SimpleNetworkWrapper CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);
        CHANNEL.registerMessage(ButtonPacket.Handler.class, ButtonPacket.class, 0, Side.SERVER);
    }

    public static void sendButtonPacket(UUID chestUUID, BlockPos pos, int dimension, int buttonId) {
        CHANNEL.sendToServer(new ButtonPacket(chestUUID, pos, dimension, buttonId));
    }
}
