package com.TheRPGAdventurer.ROTD.client.items.gemset;

import java.util.Set;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemGemDragonAxe extends ItemAxe {
	
	public ItemGemDragonAxe(ToolMaterial material, String unlocalizedName, float damage, float speed) {
		super(material, damage, speed);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(new ResourceLocation(RealmOfTheDragons.MODID, unlocalizedName));
		this.setCreativeTab(RealmOfTheDragons.TAB);
	}
	
}
