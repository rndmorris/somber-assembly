package dev.rndmorris.somberassembly;

import javax.annotation.Nonnull;

import dev.rndmorris.somberassembly.common.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = SomberAssembly.MODID,
    version = SomberAssembly.VERSION,
    name = SomberAssembly.NAME,
    dependencies = SomberAssembly.DEPS,
    acceptedMinecraftVersions = "[1.7.10]")
public class SomberAssembly {

    public static final String NAME = "Somber Assembly";
    public static final String DEPS = "after:Thaumcraft";
    public static final String MODID = "somberassembly";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final String VERSION = "1.7.10-A1";

    @SidedProxy(
        clientSide = "dev.rndmorris.somberassembly.client.ClientProxy",
        serverSide = "dev.rndmorris.somberassembly.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("Starting preInit :3");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        LOG.info("Starting init :3");
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        LOG.info("Starting postInit :3");
        proxy.postInit(event);
    }

    public static String prefixModid(@Nonnull String toPrefix) {
        return MODID + ":" + toPrefix;
    }
}
