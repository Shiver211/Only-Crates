package com.shiver.onlycrates.inventory.gui;

import java.io.IOException;
import java.util.Objects;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.inventory.ContainerGiantChest;
import com.shiver.onlycrates.network.NetworkHandler;
import com.shiver.onlycrates.tile.TileEntityBase;
import com.shiver.onlycrates.tile.TileEntityGiantChest;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGiantChest extends GuiBase {

    private static final ResourceLocation RES_LOC = new ResourceLocation(OnlyCrates.MODID, "textures/gui/gui_giant_chest.png");
    private static final ResourceLocation INVENTORY_LOC = new ResourceLocation(OnlyCrates.MODID, "textures/gui/gui_inventory.png");

    private final TileEntityGiantChest chest;
    private final int page;

    public GuiGiantChest(InventoryPlayer inventory, TileEntityBase tile, int page) {
        super(new ContainerGiantChest(inventory, tile, page));
        this.chest = (TileEntityGiantChest) tile;
        this.page = page;
        this.xSize = 242;
        this.ySize = 172 + 86;
    }

    @Override
    public void initGui() {
        super.initGui();
        int pageCount = this.chest.getPageCount();
        if (this.page > 0) {
            this.buttonList.add(new GuiButton(this.page - 1, this.guiLeft + 13, this.guiTop + 172, 20, 20, "<"));
        }
        if (this.page < pageCount - 1) {
            this.buttonList.add(new GuiButton(this.page + 1, this.guiLeft + 209, this.guiTop + 172, 20, 20, ">"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 0 && button.id < this.chest.getPageCount()) {
            NetworkHandler.sendButtonPacket(this.chest, button.id);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String text = Objects.requireNonNull(this.chest.getDisplayName()).getFormattedText();
        this.fontRenderer.drawString(text, this.xSize / 2 - this.fontRenderer.getStringWidth(text) / 2, -10, 16777215);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(RES_LOC);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 242, 190);
        this.mc.getTextureManager().bindTexture(INVENTORY_LOC);
        this.drawTexturedModalRect(this.guiLeft + 33, this.guiTop + 172, 0, 0, 176, 86);
    }
}
