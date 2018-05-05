package com.TheRPGAdventurer.ROTD.client.items.gemset;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemGemDragonPickAxe extends ItemPickaxe {

	public ItemGemDragonPickAxe(ToolMaterial material, String unlocalizedName) {
		super(material);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
		this.setCreativeTab(RealmOfTheDragons.TAB);
		
	}
	
}
