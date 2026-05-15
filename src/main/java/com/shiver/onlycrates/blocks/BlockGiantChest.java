package com.shiver.onlycrates.blocks;

import java.util.List;
import java.util.UUID;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.Tags;
import com.shiver.onlycrates.config.ModConfig;
import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.storage.ChestDataStore;
import com.shiver.onlycrates.tile.TileEntityBase;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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
    public boolean shouldDropInventory(World world, BlockPos pos) {
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityGiantChest) {
                TileEntityGiantChest chest = (TileEntityGiantChest) te;
                UUID uuid = chest.getChestUUID();
                if (uuid != null && !isPistonMove(world, pos)) {
                    ChestDataStore.get(world).markForDeletion(uuid);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    private boolean isPistonMove(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (te instanceof net.minecraft.tileentity.TileEntityPiston) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGiantChest) {
            TileEntityGiantChest chest = (TileEntityGiantChest) te;
            NBTTagCompound data = new NBTTagCompound();
            chest.writeSyncableNBT(data, TileEntityBase.NBTType.SAVE_BLOCK);

            // Remove zero-valued int keys for compactness
            java.util.List<String> keysToRemove = new java.util.ArrayList<>();
            for (String key : data.getKeySet()) {
                net.minecraft.nbt.NBTBase tag = data.getTag(key);
                if (tag instanceof net.minecraft.nbt.NBTTagInt) {
                    if (((net.minecraft.nbt.NBTTagInt) tag).getInt() == 0) {
                        keysToRemove.add(key);
                    }
                }
            }
            for (String key : keysToRemove) {
                data.removeTag(key);
            }

            ItemStack stack = new ItemStack(this.getItemDropped(state, ((World) world).rand, fortune), 1, this.damageDropped(state));
            if (!data.isEmpty()) {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag("Data", data);
            }
            drops.add(stack);
        } else {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound data = stack.getTagCompound().getCompoundTag("Data");
            if (data.hasKey("CrateUUID_MSB")) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityGiantChest) {
                    TileEntityGiantChest chest = (TileEntityGiantChest) te;
                    UUID uuid = new UUID(data.getLong("CrateUUID_MSB"), data.getLong("CrateUUID_LSB"));
                    chest.setUUID(uuid);
                    chest.ensureDataExists();
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
