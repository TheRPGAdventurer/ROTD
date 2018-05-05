package com.TheRPGAdventurer.ROTD.client.items.gemset;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemGemDragonShovel extends ItemSpade {

	public ItemGemDragonShovel(ToolMaterial material, String unlocalizedName) {
		super(material);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
		this.setCreativeTab(RealmOfTheDragons.TAB);
		
	}

}