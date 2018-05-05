package com.TheRPGAdventurer.ROTD.client.items.dragonscales;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemRubyDragonScales extends Item {


	public ItemRubyDragonScales(String unlocalizedName, String registryName) {
	    this.setUnlocalizedName(unlocalizedName);
	    this.setRegistryName(registryName);
	    this.setCreativeTab(RealmOfTheDragons.TAB);
	}

}
