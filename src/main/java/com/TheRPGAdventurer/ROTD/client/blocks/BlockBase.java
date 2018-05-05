package com.TheRPGAdventurer.ROTD.client.blocks;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block {
	
	BlockBase(String name, Material material) {
		super(material);
		
		this.setRegistryName(RealmOfTheDragons.MODID, name);
		this.setUnlocalizedName(this.getRegistryName().toString());
	}
}