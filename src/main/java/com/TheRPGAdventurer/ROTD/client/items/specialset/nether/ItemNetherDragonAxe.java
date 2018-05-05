package com.TheRPGAdventurer.ROTD.client.items.specialset.nether;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.item.ItemAxe;
import net.minecraft.util.ResourceLocation;

public class ItemNetherDragonAxe extends ItemAxe {

	public ItemNetherDragonAxe(ToolMaterial material, String unlocalizedName, float damage, float speed) {
		super(material, damage, speed);
        this.setUnlocalizedName(unlocalizedName);
        this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
        this.setCreativeTab(RealmOfTheDragons.TAB);
	}

}
