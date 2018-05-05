package com.TheRPGAdventurer.ROTD.server.entity.breeds;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.TheRPGAdventurer.ROTD.client.render.BreathWeaponEmitter;
import com.TheRPGAdventurer.ROTD.client.sound.ModSounds;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathAffectedArea;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathNode;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeapon;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeaponEnder;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.BreathWeaponNether;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Base class for dragon breeds.
 * 
 */
public abstract class DragonBreed {
    
	public EntityTameableDragon dragon;
    private final String skin;
    private final int color;
    private final Set<String> immunities = new HashSet<>();
    private final Set<Block> breedBlocks = new HashSet<>();
    private final Set<Biome> biomes = new HashSet<>();
    protected final Random rand = new Random();
    public BreathAffectedArea breathAffectedArea;
    
    DragonBreed(String skin, int color) {
        this.skin = skin;
        this.color = color;
        
        // ignore suffocation damage
        addImmunity(DamageSource.DROWN);
        addImmunity(DamageSource.IN_WALL);
        
        // assume that cactus needles don't do much damage to animals with horned scales
        addImmunity(DamageSource.CACTUS);
        
        breathAffectedArea = new BreathAffectedArea(new BreathWeapon(dragon));
        
        // ignore damage from vanilla ender dragon
//        addImmunity(DamageSource.DRAGON_BREATH); // I kinda disabled this because it would'nt make any sense, feel free to re enable
    }
    
    public static double getBreedHealth() {
    	return 85;
    }
  
    public String getSkin() {
        return skin;
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }
    
    public int getColor() {
        return color;
    }
    
    public float getColorR() {
        return ((color >> 16) & 0xFF) / 255f;
    }
    
    public float getColorG() {
        return ((color >> 8) & 0xFF) / 255f;
    }
    
    public float getColorB() {
        return (color & 0xFF) / 255f;
    }
    
    protected final void addImmunity(DamageSource dmg) {
        immunities.add(dmg.damageType);
    }
    
    public boolean isImmuneToDamage(DamageSource dmg) {
        if (immunities.isEmpty()) {
            return false;
        }
        
        return immunities.contains(dmg.damageType);
    }
    
    protected final void addHabitatBlock(Block block) {
        breedBlocks.add(block);
    }
    
    public boolean isHabitatBlock(Block block) {
        return breedBlocks.contains(block);
    }
    
    protected final void addHabitatBiome(Biome biome) {
        biomes.add(biome);
    }
    
    public boolean isHabitatBiome(Biome biome) {
        return biomes.contains(biome);
    }
    
    public boolean isHabitatEnvironment(EntityTameableDragon dragon) {
        return false;
    }
   
    public Item[] getFoodItems() {
        return new Item[] { Items.PORKCHOP, Items.BEEF, Items.CHICKEN, Items.ROTTEN_FLESH, 
        		Items.WHEAT, Items.BEETROOT, Items.RABBIT, Items.CARROT, Items.COOKED_FISH,
        		Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP,
        		Items.COOKED_RABBIT, Items.FISH, Items.COOKED_FISH, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS,
        		Items.MELON_SEEDS};
    }
    
    public Item getBreedingItem() {
        return Items.FISH;
    }
    
    public void onUpdate(EntityTameableDragon dragon) {
        placeFootprintBlocks(dragon);
    }
    
    protected void placeFootprintBlocks(EntityTameableDragon dragon) {
        // only apply on server
        if (!dragon.isServer()) {
            return;
        }
        
        // only apply on adult dragons that don't fly
        if (!dragon.isAdult() || dragon.isFlying()) {
            return;
        }
        
        // only apply if footprints are enabled
        float footprintChance = getFootprintChance();
        if (footprintChance == 1) {
            return;
        }
        
        // footprint loop, from EntitySnowman.onLivingUpdate with slight tweaks
        World world = dragon.world;
        for (int i = 0; i < 4; i++) {
            // place only if randomly selected
            if (world.rand.nextFloat() > footprintChance) {
                continue;
            }

            // get footprint position
            double bx = dragon.posX + (i % 2 * 2 - 1) * 0.25;
            double by = dragon.posY + 0.5;
            double bz = dragon.posZ + (i / 2 % 2 * 2 - 1) * 0.25;
            BlockPos pos = new BlockPos(bx, by, bz);

            // footprints can only be placed on empty space
            if (world.isAirBlock(pos)) {
                continue;
            }

            placeFootprintBlock(dragon, pos);
        }
    }
    
    protected void placeFootprintBlock(EntityTameableDragon dragon, BlockPos blockPos) {
    
    }
    
    protected float getFootprintChance() {
        return 1;
    }
    
    public abstract void onEnable(EntityTameableDragon dragon);
    
    public abstract void onDisable(EntityTameableDragon dragon);
    
    public abstract void onDeath(EntityTameableDragon dragon);
    
    public SoundEvent getLivingSound() {
        if (rand.nextInt(2) == 0) {
            return ModSounds.ENTITY_DRAGON_GROWL;
        } else {
        	return ModSounds.ENTITY_DRAGON_BREATHE;
        }
    }
    
    public SoundEvent getHurtSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_HURT;
    }
    
    public SoundEvent getDeathSound() {
        return ModSounds.ENTITY_DRAGON_DEATH;
    }
    
    public SoundEvent getWingsSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_FLAP;
    }
    
    public SoundEvent getStepSound() {
        return ModSounds.ENTITY_DRAGON_STEP;
    }
    
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }
    
    public SoundEvent getAttackSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }

    public float getSoundPitch(SoundEvent sound) {
        return 1;
    }

    public float getSoundVolume(SoundEvent sound) {
        return 2;
    }
    
    public boolean canChangeBreed() {
    	return false;
    }
    
    public boolean canBreathFire() {
    	return true;
    }
    
    public void continueAndUpdateBreathing(World world, Vec3d origin, Vec3d endOfLook, BreathNode.Power power) {    
    	dragon = new EntityTameableDragon(world);
        breathAffectedArea.continueBreathing(world, origin, endOfLook, power);
        breathAffectedArea.updateTick(world);
    }
    
    public void spawnBreathParticles(World world, BreathNode.Power power, int tickCounter, Vec3d origin, Vec3d endOfLook) {
    	dragon = new EntityTameableDragon(world);
        dragon.getBreathHelper().getEmitter().setBeamEndpoints(origin, endOfLook);
        dragon.getBreathHelper().getEmitter().spawnBreathParticles(world, power, tickCounter);
    }

}

