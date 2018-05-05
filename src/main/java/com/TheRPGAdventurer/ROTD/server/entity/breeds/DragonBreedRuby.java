package com.TheRPGAdventurer.ROTD.server.entity.breeds;

import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;

import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;

public class DragonBreedRuby extends DragonBreed {

    DragonBreedRuby() {
        super("ruby", 0x960b0f);
        
        addHabitatBlock(Blocks.RED_GLAZED_TERRACOTTA);
        addHabitatBlock(Blocks.FIRE);
        addHabitatBlock(Blocks.LIT_FURNACE);
        addHabitatBlock(Blocks.REDSTONE_BLOCK);

        addImmunity(DamageSource.IN_FIRE);
        addImmunity(DamageSource.ON_FIRE);
        addImmunity(DamageSource.MAGIC);
        addImmunity(DamageSource.HOT_FLOOR);
        addImmunity(DamageSource.LIGHTNING_BOLT);
        addImmunity(DamageSource.WITHER);

    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {}

    @Override
    public void onDisable(EntityTameableDragon dragon) {}

    @Override
    public void onDeath(EntityTameableDragon dragon) {}
    
	@Override
	public boolean canChangeBreed() {
		return true;
	}
  
}
	
