package com.TheRPGAdventurer.ROTD.client.initialization;

import com.TheRPGAdventurer.ROTD.client.items.ItemFireBreath;
import com.TheRPGAdventurer.ROTD.client.items.ItemStructureSpawner;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemAmethystDragonScales;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemEnderDragonScales;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemGarnetDragonScales;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemJadeDragonScales;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemNetherDragonScales;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemRubyDragonScales;
import com.TheRPGAdventurer.ROTD.client.items.dragonscales.ItemSapphireDragonScales;

import net.minecraft.item.Item;

public class ModItems {
	
	public static final Item JadeDragonScales;
	public static final Item RubyDragonScales;
	public static final Item AmethystDragonScales;
	public static final Item SapphireDragonScales;
	public static final Item GarnetDragonScales;
	public static final Item NetherDragonScales;
	public static final Item EnderDragonScales;
	public static final Item structure_spawner;
	public static final Item ItemBreath;
	
	
	public static final Item[] ITEMS = {
		JadeDragonScales = new ItemJadeDragonScales("jade_dragonscales", "jade_dragonscales"),
		RubyDragonScales = new ItemRubyDragonScales("ruby_dragonscales", "ruby_dragonscales"),
		AmethystDragonScales = new ItemAmethystDragonScales("amethyst_dragonscales", "amethyst_dragonscales"),
		SapphireDragonScales = new ItemSapphireDragonScales("sapphire_dragonscales", "sapphire_dragonscales"),
		GarnetDragonScales = new ItemGarnetDragonScales("garnet_dragonscales", "garnet_dragonscales"),
		NetherDragonScales = new ItemNetherDragonScales("nether_dragonscales", "nether_dragonscales"),
		EnderDragonScales = new ItemEnderDragonScales("ender_dragonscales", "ender_dragonscales"),
		ItemBreath = new ItemFireBreath("itemfirebreath"),
		structure_spawner = new ItemStructureSpawner("structure_spawner", "structure_spawner")
		
	};
}
