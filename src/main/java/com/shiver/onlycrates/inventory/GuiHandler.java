package com.shiver.onlycrates.inventory;

import com.shiver.onlycrates.inventory.gui.GuiGiantChest;
import com.shiver.onlycrates.tile.TileEntityBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.shiver.onlycrates.OnlyCrates;

public class GuiHandler implements IGuiHandler {

    public static void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(OnlyCrates.INSTANCE, new GuiHandler());
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntityBase tile = null;
        if (GuiTypes.values()[id].checkTileEntity) {
            tile = (TileEntityBase) world.getTileEntity(new BlockPos(x, y, z));
        }
        switch (GuiTypes.values()[id]) {
            case GIANT_CHEST:
                return new ContainerGiantChest(player.inventory, tile, 0);
            case GIANT_CHEST_PAGE_2:
                return new ContainerGiantChest(player.inventory, tile, 1);
            case GIANT_CHEST_PAGE_3:
                return new ContainerGiantChest(player.inventory, tile, 2);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntityBase tile = null;
        if (GuiTypes.values()[id].checkTileEntity) {
            tile = (TileEntityBase) world.getTileEntity(new BlockPos(x, y, z));
        }
        switch (GuiTypes.values()[id]) {
            case GIANT_CHEST:
                return new GuiGiantChest(player.inventory, tile, 0);
            case GIANT_CHEST_PAGE_2:
                return new GuiGiantChest(player.inventory, tile, 1);
            case GIANT_CHEST_PAGE_3:
                return new GuiGiantChest(player.inventory, tile, 2);
            default:
                return null;
        }
    }

    public enum GuiTypes {
        GIANT_CHEST,
        GIANT_CHEST_PAGE_2,
        GIANT_CHEST_PAGE_3;

        public final boolean checkTileEntity;

        GuiTypes() {
            this(true);
        }

        GuiTypes(boolean checkTileEntity) {
            this.checkTileEntity = checkTileEntity;
        }
    }
}
