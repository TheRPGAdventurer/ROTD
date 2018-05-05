package com.TheRPGAdventurer.ROTD.client.items.specialset.end;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;

public class ItemEnderDragonSword extends ItemSword {

	public ItemEnderDragonSword(ToolMaterial material, String unlocalizedName) {
		super(material);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
		this.setCreativeTab(RealmOfTheDragons.TAB);

	}

}
