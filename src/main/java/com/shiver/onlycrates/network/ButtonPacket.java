package com.shiver.onlycrates.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

public class ButtonPacket implements IMessage {

    private BlockPos pos;
    private int dimension;
    private int buttonId;

    public ButtonPacket() {}

    public ButtonPacket(BlockPos pos, int dimension, int buttonId) {
        this.pos = pos;
        this.dimension = dimension;
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.dimension = buf.readInt();
        this.buttonId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
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
                WorldServer world = Objects.requireNonNull(ctx.getServerHandler().player.getServer()).getWorld(message.dimension);
                TileEntity tile = world.getTileEntity(message.pos);
                if (tile instanceof IButtonReactor) {
                    ((IButtonReactor) tile).onButtonPressed(message.buttonId, ctx.getServerHandler().player);
                }
            });
            return null;
        }
    }
}
