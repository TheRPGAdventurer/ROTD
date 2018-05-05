package com.TheRPGAdventurer.ROTD.server.entity.breeds;

import com.TheRPGAdventurer.ROTD.client.render.BreathWeaponEmitter;
import com.TheRPGAdventurer.ROTD.client.sound.ModSounds;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathAffectedArea;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeapon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeaponEnder;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathNode.Power;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class DragonBreedEnd extends DragonBreed {
	
	public BreathAffectedArea breathAffectedAreaEnder;

    DragonBreedEnd() {
        super("ender", 0xab39be);
        
        addImmunity(DamageSource.IN_FIRE);
        addImmunity(DamageSource.ON_FIRE);
        addImmunity(DamageSource.MAGIC);
        addImmunity(DamageSource.HOT_FLOOR);
        addImmunity(DamageSource.LIGHTNING_BOLT);
        addImmunity(DamageSource.WITHER);
        
        breathAffectedAreaEnder = new BreathAffectedArea(new BreathWeaponEnder(dragon));
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {}

    @Override
    public void onDisable(EntityTameableDragon dragon) {}

    @Override
    public void onDeath(EntityTameableDragon dragon) {}
    
    @Override
    public SoundEvent getLivingSound() {
        if (rand.nextInt(3) == 0) {
            return SoundEvents.ENTITY_ENDERDRAGON_GROWL;
        } else {
        	return ModSounds.ENTITY_DRAGON_BREATHE;
        }
    }
    
	@Override
	public boolean canChangeBreed() {
		return false;
	}
	
	@Override
    public void continueAndUpdateBreathing(World world, Vec3d origin, Vec3d endOfLook, BreathNode.Power power) {
		breathAffectedAreaEnder.continueBreathing(world, origin, endOfLook, power);
		breathAffectedAreaEnder.updateTick(world);
    }
    
	@Override
    public void spawnBreathParticles(World world, BreathNode.Power power, int tickCounter, Vec3d origin, Vec3d endOfLook) {
		dragon = new EntityTameableDragon(world);
		dragon.getBreathHelper().getEmitter().setBeamEndpoints(origin, endOfLook);
		dragon.getBreathHelper().getEmitter().spawnBreathParticlesforEnderDragon(world, power, tickCounter);
    }
    
}
	
