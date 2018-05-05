/*
 ** 2012 August 13
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD;

import com.TheRPGAdventurer.ROTD.client.gui.GuiHandler;
import com.TheRPGAdventurer.ROTD.client.initialization.ROTDKeyBindings;
import com.TheRPGAdventurer.ROTD.client.inventory.CreativeTab;
import com.TheRPGAdventurer.ROTD.server.ServerProxy;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.network.MessageDragonArmor;
import com.TheRPGAdventurer.ROTD.server.world.RealmOfTheDragonsWorldGenerator;

import net.ilexiconn.llibrary.server.network.NetworkWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.BiomeHills;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Main control class for Forge.
 * 
 */
@Mod(
    dependencies = "required-after:llibrary@[" + RealmOfTheDragons.LLIBRARY_VERSION + ",)",
    modid = RealmOfTheDragons.MODID,
    name = RealmOfTheDragons.NAME,
    version = RealmOfTheDragons.VERSION,
    useMetadata = true,
    guiFactory = RealmOfTheDragons.GUI_FACTORY
)

public class RealmOfTheDragons {
    
	@NetworkWrapper({MessageDragonArmor.class})
	public static SimpleNetworkWrapper NETWORK_WRAPPER;
    public static final String NAME = "Realm Of The Dragons";
    public static final String MODID = "rotd";
    public static final String VERSION = "1.7";
    public static final String LLIBRARY_VERSION = "1.7.9";
    public static final String GUI_FACTORY = "com.TheRPGAdventurer.ROTD.RealmOfTheDragonsConfigGuiFactory";
    
    @SidedProxy(
        serverSide = "com.TheRPGAdventurer.ROTD.server.ServerProxy",
        clientSide = "com.TheRPGAdventurer.ROTD.client.ClientProxy"
    )
    public static ServerProxy proxy;
    
    @Instance(value = MODID)
    public static RealmOfTheDragons instance;
    
    private ModMetadata metadata;
    private RealmOfTheDragonsConfig config;
    public static CreativeTabs TAB;
    
    public RealmOfTheDragonsConfig getConfig() {
        return config;
    }
    
    public ModMetadata getMetadata() {
        return metadata;
    }
    
    @EventHandler
    public void PreInitialization(FMLPreInitializationEvent event) {
    	RealmOfTheDragonsLootTables.registerLootTables();
    	TAB = new CreativeTab(MODID);
        metadata = event.getModMetadata();
        proxy.PreInitialization(event);
//        EntityRegistry.addSpawn(EntityTameableDragon.class, 5, 1, 1, EnumCreatureType.CREATURE, Biomes.PLAINS);  
    }

    @EventHandler
    public void Initialization(FMLInitializationEvent event) {
        proxy.Initialization(event); proxy.render();
        GameRegistry.registerWorldGenerator(new RealmOfTheDragonsWorldGenerator(), 0);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
       
    }

    @EventHandler
    public void PostInitialization(FMLPostInitializationEvent event) {
        proxy.PostInitialization(event);
    }
    
    @EventHandler
    public void ServerStarting(FMLServerStartingEvent event) {
        proxy.ServerStarting(event);
    }
    
    @EventHandler
    public void ServerStopped(FMLServerStoppedEvent event) {
        proxy.ServerStopped(event);
    }
}
