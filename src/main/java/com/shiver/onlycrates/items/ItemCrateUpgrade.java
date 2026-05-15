package com.shiver.onlycrates.items;

import java.util.List;

import javax.annotation.Nullable;

import com.shiver.onlycrates.tile.TileEntityGiantChest;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrateUpgrade extends Item {

    public enum UpgradeType {
        SHULKER("tooltip.onlycrates.shulker_upgrade"),
        BLAST_PROOF("tooltip.onlycrates.blast_proof_upgrade");

        private final String tooltipKey;

        UpgradeType(String tooltipKey) {
            this.tooltipKey = tooltipKey;
        }

        public String getTooltipKey() {
            return this.tooltipKey;
        }
    }

    private final UpgradeType type;

    public ItemCrateUpgrade(UpgradeType type) {
        this.type = type;
        this.setMaxStackSize(64);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking()) {
            return EnumActionResult.PASS;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityGiantChest)) {
            return EnumActionResult.PASS;
        }

        if (!world.isRemote) {
            TileEntityGiantChest chest = (TileEntityGiantChest) tile;
            boolean applied = false;
            switch (this.type) {
                case SHULKER:
                    applied = chest.applyShulkerUpgrade();
                    break;
                case BLAST_PROOF:
                    applied = chest.applyBlastProofUpgrade();
                    break;
                default:
                    break;
            }
            if (applied) {
                if (!player.capabilities.isCreativeMode) {
                    player.getHeldItem(hand).shrink(1);
                }
                chest.sendUpdate();
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(TextFormatting.GRAY + I18n.format(this.type.getTooltipKey()));
    }
}
