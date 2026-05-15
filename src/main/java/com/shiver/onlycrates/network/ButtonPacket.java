package com.shiver.onlycrates.network;

import java.util.UUID;

import com.shiver.onlycrates.tile.TileEntityGiantChest;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ButtonPacket implements IMessage {

    private UUID chestUUID;
    private BlockPos pos;
    private int dimension;
    private int buttonId;

    public ButtonPacket() {}

    public ButtonPacket(UUID chestUUID, BlockPos pos, int dimension, int buttonId) {
        this.chestUUID = chestUUID;
        this.pos = pos;
        this.dimension = dimension;
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        this.chestUUID = new UUID(msb, lsb);
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.dimension = buf.readInt();
        this.buttonId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.chestUUID.getMostSignificantBits());
        buf.writeLong(this.chestUUID.getLeastSignificantBits());
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.dimension);
        buf.writeInt(this.buttonId);
    }

    public static class Handler implements IMessageHandler<ButtonPacket, IMessage> {
        @Override
        public IMessage onMessage(final ButtonPacket message, final MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                WorldServer world = ctx.getServerHandler().player.getServer().getWorld(message.dimension);
                if (world != null) {
                    TileEntity tile = world.getTileEntity(message.pos);
                    if (tile instanceof TileEntityGiantChest) {
                        TileEntityGiantChest chest = (TileEntityGiantChest) tile;
                        if (chest.getChestUUID() != null && chest.getChestUUID().equals(message.chestUUID)) {
                            chest.onButtonPressed(message.buttonId, ctx.getServerHandler().player);
                        }
                    }
                }
            });
            return null;
        }
    }
}
