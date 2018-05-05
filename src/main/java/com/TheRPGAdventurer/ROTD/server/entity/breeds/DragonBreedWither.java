package com.TheRPGAdventurer.ROTD.server.entity.breeds;

import com.TheRPGAdventurer.ROTD.client.render.BreathWeaponEmitter;
import com.TheRPGAdventurer.ROTD.client.sound.ModSounds;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathAffectedArea;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeapon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeaponWither;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class DragonBreedWither extends DragonBreed {
	
	public BreathAffectedArea breathAffectedAreaWither;

    DragonBreedWither() {
        super("wither", 0x50260a);
        
        addImmunity(DamageSource.IN_FIRE);
        addImmunity(DamageSource.ON_FIRE);
        addImmunity(DamageSource.MAGIC);
        addImmunity(DamageSource.HOT_FLOOR);
        addImmunity(DamageSource.LIGHTNING_BOLT);
        addImmunity(DamageSource.WITHER);
        
        breathAffectedAreaWither = new BreathAffectedArea(new BreathWeaponWither(dragon));
    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {}

    @Override
    public void onDisable(EntityTameableDragon dragon) {}

    @Override
    public void onDeath(EntityTameableDragon dragon) {}
    
    @Override
    public SoundEvent getLivingSound() {
        if (rand.nextInt(2) == 0) {
            return SoundEvents.ENTITY_SKELETON_AMBIENT;
        } else {
        	return ModSounds.ENTITY_SKELETON_DRAGON_GROWL;
        }
    }
    
	@Override
	public boolean canChangeBreed() {
		return false;
	}
	
	@Override
    public void continueAndUpdateBreathing(World world, Vec3d origin, Vec3d endOfLook, BreathNode.Power power) {
		dragon = new EntityTameableDragon(world);
        breathAffectedAreaWither = new BreathAffectedArea(new BreathWeaponWither(dragon));
        breathAffectedAreaWither.continueBreathing(world, origin, endOfLook, power);
        breathAffectedAreaWither.updateTick(world);
    }
    
	@Override
    public void spawnBreathParticles(World world, BreathNode.Power power, int tickCounter, Vec3d origin, Vec3d endOfLook) {
		dragon = new EntityTameableDragon(world);
        dragon.getBreathHelper().getEmitter().setBeamEndpoints(origin, endOfLook);
        dragon.getBreathHelper().getEmitter().spawnBreathParticlesforWitherDragon(world, power, tickCounter);
    }
    
}
	
