package com.shiver.onlycrates;

import com.shiver.onlycrates.inventory.GuiHandler;
import com.shiver.onlycrates.network.NetworkHandler;
import com.shiver.onlycrates.proxy.CommonProxy;
import com.shiver.onlycrates.tile.TileEntityBase;

import com.shiver.onlycrates.Tags;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class OnlyCrates {

    public static final String MODID = Tags.MOD_ID;
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.Instance(MODID)
    public static OnlyCrates INSTANCE;

    @SidedProxy(clientSide = "com.shiver.onlycrates.proxy.ClientProxy", serverSide = "com.shiver.onlycrates.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkHandler.init();
        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        TileEntityBase.init();
        GuiHandler.init();
        PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();
    }
}
