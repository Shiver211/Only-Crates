package com.shiver.onlycrates.blocks;

import java.util.List;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.Tags;
import com.shiver.onlycrates.config.ModConfig;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.tile.TileEntityGiantChest;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BlockGiantChest extends BlockContainerBase {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

    public BlockGiantChest(String name) {
        super(Material.WOOD, name);
        this.setHarvestLevel("axe", 0);
        this.setHardness(0.5F);
        this.setResistance(15.0F);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, MathHelper.clamp(meta, 0, 15));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        ModConfig.CrateLevel level = ModConfig.getCrateLevel(meta);
        int pages = level != null ? level.getPages() : 1;
        return new TileEntityGiantChest(pages);
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
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 2; i++) {
            items.add(new ItemStack(this, 1, i));
        }
        for (ModConfig.CrateLevel level : ModConfig.getExtraCrates()) {
            items.add(new ItemStack(this, 1, level.getMeta()));
        }
    }

    @Override
    protected ItemBlockBase getItemBlock() {
        return new TheItemBlock(this);
    }

    public static class TheItemBlock extends ItemBlockBase {

        public TheItemBlock(net.minecraft.block.Block block) {
            super(block);
            this.setHasSubtypes(true);
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
            int meta = stack.getMetadata();
            if (meta == 2) {
                tooltip.add(TextFormatting.ITALIC + I18n.format("container." + Tags.MOD_ID + ".giantChestLarge.desc"));
            } else if (meta == 0) {
                tooltip.add(TextFormatting.ITALIC + I18n.format("container." + Tags.MOD_ID + ".giantChest.desc"));
            }
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            if (stack.hasTagCompound()) {
                NBTTagCompound data = stack.getTagCompound().getCompoundTag("Data");
                if (data.hasKey("DisplayName")) {
                    return data.getString("DisplayName");
                }
            }
            int meta = stack.getMetadata();
            return I18n.format("tile." + Tags.MOD_ID + ".block_giant_chest_" + meta + ".name");
        }

        @Override
        public NBTTagCompound getNBTShareTag(ItemStack stack) {
            return null;
        }
    }
}
