package com.shiver.onlycrates.inventory;

import com.shiver.onlycrates.inventory.gui.GuiGiantChest;
import com.shiver.onlycrates.tile.TileEntityBase;
import com.shiver.onlycrates.tile.TileEntityGiantChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.shiver.onlycrates.OnlyCrates;

public class GuiHandler implements IGuiHandler {

    public static final int GUI_GIANT_CHEST_BASE = 0;

    public static void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(OnlyCrates.INSTANCE, new GuiHandler());
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityGiantChest) {
            int page = id - GUI_GIANT_CHEST_BASE;
            int pageCount = ((TileEntityGiantChest) tile).getPageCount();
            if (page >= 0 && page < pageCount) {
                return new ContainerGiantChest(player.inventory, (TileEntityBase) tile, page);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityGiantChest) {
            int page = id - GUI_GIANT_CHEST_BASE;
            int pageCount = ((TileEntityGiantChest) tile).getPageCount();
            if (page >= 0 && page < pageCount) {
                return new GuiGiantChest(player.inventory, (TileEntityBase) tile, page);
            }
        }
        return null;
    }
}
