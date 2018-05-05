package com.TheRPGAdventurer.ROTD.client.items;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;

public class ItemFireBreath extends Item {
	
	public ItemFireBreath(String unlocalizedName) {
	    this.setUnlocalizedName(unlocalizedName);
	    this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
	    this.setMaxStackSize(1);
	    this.setCreativeTab(RealmOfTheDragons.TAB);
	}

}
