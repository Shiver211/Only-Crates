package com.shiver.onlycrates.blocks;

import java.util.List;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.tile.TileEntityGiantChest;
import com.shiver.onlycrates.tile.TileEntityGiantChestConfigurable;
import com.shiver.onlycrates.tile.TileEntityGiantChestLarge;
import com.shiver.onlycrates.tile.TileEntityGiantChestMedium;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BlockGiantChest extends BlockContainerBase {

    public final int type;
    private final int configPages;
    private final String customDisplayName;
    private final String blockId;

    public BlockGiantChest(String name, int type) {
        this(name, type, 0, null, null);
    }

    public BlockGiantChest(String name, int pages, String customDisplayName, String blockId) {
        this(name, -1, pages, customDisplayName, blockId);
    }

    private BlockGiantChest(String name, int type, int pages, String customDisplayName, String blockId) {
        super(Material.WOOD, name);
        this.type = type;
        this.configPages = pages;
        this.customDisplayName = customDisplayName;
        this.blockId = blockId;
        this.setHarvestLevel("axe", 0);
        this.setHardness(0.5F);
        this.setResistance(15.0F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        if (this.configPages > 0) {
            return new TileEntityGiantChestConfigurable(this.configPages, this.customDisplayName);
        }
        switch (this.type) {
            case 1:
                return new TileEntityGiantChestMedium();
            case 2:
                return new TileEntityGiantChestLarge();
            default:
                return new TileEntityGiantChest();
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing par6, float par7, float par8, float par9) {
        if (!world.isRemote) {
            TileEntityGiantChest chest = (TileEntityGiantChest) world.getTileEntity(pos);
            if (chest != null) {
                chest.fillWithLoot(player);
                player.openGui(OnlyCrates.INSTANCE, GuiHandler.GUI_GIANT_CHEST_BASE, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        if (stack.getTagCompound() != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityGiantChest) {
                NBTTagList list = stack.getTagCompound().getTagList("Items", 10);
                IItemHandlerModifiable inv = ((TileEntityGiantChest) tile).inv;
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound compound = list.getCompoundTagAt(i);
                    if (compound != null && compound.hasKey("id")) {
                        inv.setStackInSlot(i, new ItemStack(list.getCompoundTagAt(i)));
                    }
                }
            }
        }
        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }


    @Override
    protected ItemBlockBase getItemBlock() {
        return new TheItemBlock(this);
    }

    public String getCustomDisplayName() {
        return this.customDisplayName;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public static class TheItemBlock extends ItemBlockBase {

        public TheItemBlock(net.minecraft.block.Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
            int type = this.block instanceof BlockGiantChest ? ((BlockGiantChest) this.block).type : -1;
            if (type == 2) {
                tooltip.add(TextFormatting.ITALIC + I18n.format("container." + OnlyCrates.MODID + ".giantChestLarge.desc"));
            } else if (type == 0) {
                tooltip.add(TextFormatting.ITALIC + I18n.format("container." + OnlyCrates.MODID + ".giantChest.desc"));
            }
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            if (this.block instanceof BlockGiantChest) {
                String displayName = ((BlockGiantChest) this.block).getCustomDisplayName();
                if (displayName != null && !displayName.isEmpty()) {
                    return displayName;
                }
            }
            return super.getItemStackDisplayName(stack);
        }

        @Override
        public NBTTagCompound getNBTShareTag(ItemStack stack) {
            return null;
        }
    }
}
