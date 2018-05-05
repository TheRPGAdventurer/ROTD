package com.TheRPGAdventurer.ROTD.client.items.specialset.end;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.item.ItemSpade;
import net.minecraft.util.ResourceLocation;

public class ItemEnderDragonShovel extends ItemSpade {

	public ItemEnderDragonShovel(ToolMaterial material, String unlocalizedName) {
		super(material);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
		this.setCreativeTab(RealmOfTheDragons.TAB);
	}

}
