package com.TheRPGAdventurer.ROTD.client.initialization;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;
import com.TheRPGAdventurer.ROTD.client.items.ItemDiamondShears;
import com.TheRPGAdventurer.ROTD.client.items.gemset.ItemGemDragonAxe;
import com.TheRPGAdventurer.ROTD.client.items.gemset.ItemGemDragonPickAxe;
import com.TheRPGAdventurer.ROTD.client.items.gemset.ItemGemDragonShovel;
import com.TheRPGAdventurer.ROTD.client.items.gemset.ItemGemDragonSword;
import com.TheRPGAdventurer.ROTD.client.items.specialset.end.ItemEnderDragonAxe;
import com.TheRPGAdventurer.ROTD.client.items.specialset.end.ItemEnderDragonPickaxe;
import com.TheRPGAdventurer.ROTD.client.items.specialset.end.ItemEnderDragonShovel;
import com.TheRPGAdventurer.ROTD.client.items.specialset.end.ItemEnderDragonSword;
import com.TheRPGAdventurer.ROTD.client.items.specialset.nether.ItemNetherDragonAxe;
import com.TheRPGAdventurer.ROTD.client.items.specialset.nether.ItemNetherDragonPickAxe;
import com.TheRPGAdventurer.ROTD.client.items.specialset.nether.ItemNetherDragonShovel;
import com.TheRPGAdventurer.ROTD.client.items.specialset.nether.ItemNetherDragonSword;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ModTools {
	
	static float damage = 10.0F; static float speed = -2.9F; static float netherDamage = 12.0F; static float netherSpeed = -3.0F; static float enderDamage = 9.0F; static float enderSpeed = -3.3F;

	public static final ToolMaterial JadeDragonScaleMaterial     = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":jadedragonscales", 4, 1700, 8.0F, 5.0F, 11);
	public static final ToolMaterial RubyDragonScaleMaterial     = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":rubydragonscales", 4, 1700, 8.0F, 5.0F, 11);
	public static final ToolMaterial GarnetDragonScaleMaterial   = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":garnetdragonscales", 4, 1700, 8.0F, 5.0F, 11);
	public static final ToolMaterial AmethystDragonScaleMaterial = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":amethystdragonscales", 4, 1700, 8.0F, 5.0F, 11);
	public static final ToolMaterial SapphireDragonScaleMaterial = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":sapphiredragonscales", 4, 1700, 8.0F, 5.0F, 11);
	public static final ToolMaterial NetherDragonScaleMaterial   = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":netherdragonscales", 5, 1770, 8.0F, 7.0F, 11);
	public static final ToolMaterial EnderDragonScaleMaterial    = EnumHelper.addToolMaterial(RealmOfTheDragons.MODID + ":enderdragonscales", 5, 2000, 8.0F, 7.0F, 11);
	
	public static ItemGemDragonPickAxe jadeDragonPickaxe = new ItemGemDragonPickAxe(JadeDragonScaleMaterial, "jade_dragon_pickaxe");
	public static ItemGemDragonAxe  jadeDragonAxe = new ItemGemDragonAxe(JadeDragonScaleMaterial, "jade_dragon_axe", damage, speed);
	public static ItemGemDragonShovel jadeDragonShovel = new ItemGemDragonShovel(JadeDragonScaleMaterial, "jade_dragon_shovel");
	public static ItemGemDragonSword jadeDragonSword = new ItemGemDragonSword(JadeDragonScaleMaterial, "jade_dragon_sword");
	
	public static ItemGemDragonPickAxe garnetDragonPickaxe = new ItemGemDragonPickAxe(GarnetDragonScaleMaterial, "garnet_dragon_pickaxe");
	public static ItemGemDragonAxe garnetDragonAxe = new ItemGemDragonAxe(GarnetDragonScaleMaterial, "garnet_dragon_axe", damage, speed);
	public static ItemGemDragonShovel garnetDragonShovel = new ItemGemDragonShovel(GarnetDragonScaleMaterial, "garnet_dragon_shovel");
	public static ItemGemDragonSword garnetDragonSword = new ItemGemDragonSword(GarnetDragonScaleMaterial, "garnet_dragon_sword");
	
	public static ItemGemDragonPickAxe sapphireDragonPickaxe = new ItemGemDragonPickAxe(SapphireDragonScaleMaterial, "sapphire_dragon_pickaxe");
	public static ItemGemDragonAxe sapphireDragonAxe = new ItemGemDragonAxe(SapphireDragonScaleMaterial, "sapphire_dragon_axe", damage, damage);
	public static ItemGemDragonShovel sapphireDragonShovel = new ItemGemDragonShovel(SapphireDragonScaleMaterial, "sapphire_dragon_shovel");
	public static ItemGemDragonSword sapphireDragonSword = new ItemGemDragonSword(SapphireDragonScaleMaterial, "sapphire_dragon_sword");
    
	public static ItemGemDragonPickAxe amethystDragonPickaxe = new ItemGemDragonPickAxe(AmethystDragonScaleMaterial, "amethyst_dragon_pickaxe");
	public static ItemGemDragonAxe amethystDragonAxe = new ItemGemDragonAxe(AmethystDragonScaleMaterial, "amethyst_dragon_axe", damage, damage);
	public static ItemGemDragonShovel amethystDragonShovel = new ItemGemDragonShovel(AmethystDragonScaleMaterial, "amethyst_dragon_shovel");
	public static ItemGemDragonSword amethystDragonSword = new ItemGemDragonSword(AmethystDragonScaleMaterial, "amethyst_dragon_sword");
	
	public static ItemGemDragonPickAxe rubyDragonPickaxe = new ItemGemDragonPickAxe(RubyDragonScaleMaterial, "ruby_dragon_pickaxe");
	public static ItemGemDragonAxe rubyDragonAxe = new ItemGemDragonAxe(RubyDragonScaleMaterial, "ruby_dragon_axe", damage, damage);
	public static ItemGemDragonShovel rubyDragonShovel = new ItemGemDragonShovel(RubyDragonScaleMaterial, "ruby_dragon_shovel");
	public static ItemGemDragonSword rubyDragonSword = new ItemGemDragonSword(RubyDragonScaleMaterial, "ruby_dragon_sword");
	
	public static ItemNetherDragonPickAxe netherDragonPickaxe = new ItemNetherDragonPickAxe(NetherDragonScaleMaterial, "nether_dragon_pickaxe");
	public static ItemNetherDragonAxe netherDragonAxe = new ItemNetherDragonAxe(NetherDragonScaleMaterial, "nether_dragon_axe", netherDamage, netherSpeed);
	public static ItemNetherDragonShovel netherDragonShovel = new ItemNetherDragonShovel(NetherDragonScaleMaterial, "nether_dragon_shovel");
	public static ItemNetherDragonSword netherDragonSword = new ItemNetherDragonSword(NetherDragonScaleMaterial, "nether_dragon_sword");
	
	public static ItemEnderDragonPickaxe enderDragonPickaxe = new ItemEnderDragonPickaxe(EnderDragonScaleMaterial, "ender_dragon_pickaxe");
	public static ItemEnderDragonAxe enderDragonAxe = new ItemEnderDragonAxe(EnderDragonScaleMaterial, "ender_dragon_axe", enderDamage, enderSpeed);
	public static ItemEnderDragonShovel enderDragonShovel = new ItemEnderDragonShovel(EnderDragonScaleMaterial, "ender_dragon_shovel");
	public static ItemEnderDragonSword enderDragonSword = new ItemEnderDragonSword(EnderDragonScaleMaterial, "ender_dragon_sword");
	
	public static ItemDiamondShears diamond_shears = new ItemDiamondShears(ToolMaterial.DIAMOND, "diamond_shears");
	
	public static final Item[] TOOLS = {
		jadeDragonPickaxe, jadeDragonAxe, jadeDragonShovel, jadeDragonSword,
		rubyDragonPickaxe, rubyDragonAxe, rubyDragonShovel, rubyDragonSword,
	    amethystDragonAxe, amethystDragonPickaxe, amethystDragonShovel, amethystDragonSword,
		garnetDragonAxe, garnetDragonPickaxe, garnetDragonShovel,  garnetDragonSword,
	    sapphireDragonAxe, sapphireDragonPickaxe, sapphireDragonShovel, sapphireDragonSword,
	    netherDragonAxe, netherDragonPickaxe, netherDragonShovel, netherDragonSword,
	    enderDragonAxe, enderDragonPickaxe, enderDragonShovel, enderDragonSword,
	    diamond_shears
	};
	
	public static void initRepairs() {
		AmethystDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.AmethystDragonScales));
		GarnetDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.GarnetDragonScales));
		JadeDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.JadeDragonScales));
		RubyDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.RubyDragonScales));
		SapphireDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.SapphireDragonScales));
		NetherDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.NetherDragonScales));
		EnderDragonScaleMaterial.setRepairItem(new ItemStack(ModItems.EnderDragonScales));

	}
	
}