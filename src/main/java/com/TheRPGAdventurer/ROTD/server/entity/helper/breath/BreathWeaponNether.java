package com.TheRPGAdventurer.ROTD.server.entity.helper.breath;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Random;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragonsConfig;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

/**
 * Created by TGG on 5/08/2015.
 *
 * Models the effects of a breathweapon on blocks and entities
 * Usage:
 * 1) Construct with a parent dragon
 * 2) affectBlock() to apply an area of effect to the given block (eg set fire to it)
 * 3) affectEntity() to apply an area of effect to the given entity (eg damage it)
 *
 * Currently does fire only.  Intended to be subclassed later on for different weapon types.
 */
public class BreathWeaponNether extends BreathWeapon {
	
  protected EntityTameableDragon dragon;
	
  public BreathWeaponNether(EntityTameableDragon i_dragon) {
    super(i_dragon);
  }

  /** if the hitDensity is high enough, manipulate the block (eg set fire to it)
   * @param world
   * @param blockPosition  the world [x,y,z] of the block
   * @param currentHitDensity
   * @return the updated block hit density
   */
  public BreathAffectedBlock affectBlock(World world, Vec3i blockPosition, BreathAffectedBlock currentHitDensity) {
    checkNotNull(world);
    checkNotNull(blockPosition);
    checkNotNull(currentHitDensity);

    BlockPos blockPos = new BlockPos(blockPosition);
    IBlockState iBlockState = world.getBlockState(blockPos);
    Block block = iBlockState.getBlock();

    Random rand = new Random();

    // Flammable blocks: set fire to them once they have been exposed enough.  After sufficient exposure, destroy the
    //   block (otherwise -if it's raining, the burning block will keep going out)
    // Non-flammable blocks:
    // 1) liquids (except lava) evaporate
    // 2) If the block can be smelted (eg sand), then convert the block to the smelted version
    // 3) If the block can't be smelted then convert to lava

    for (EnumFacing facing : EnumFacing.values()) {
      BlockPos sideToIgnite = blockPos.offset(facing);
      if (block.isFlammable(world, sideToIgnite, facing)) {
        int flammability = block.getFlammability(world, sideToIgnite, facing);
        float thresholdForIgnition = convertFlammabilityToHitDensityThreshold(flammability);
        float thresholdForDestruction = thresholdForIgnition * 30;
        float densityOfThisFace = currentHitDensity.getHitDensity(facing);
        if (densityOfThisFace >= thresholdForIgnition && world.isAirBlock(sideToIgnite) && RealmOfTheDragonsConfig.canFireSetFire) {
          final float MIN_PITCH = 0.8F;
          final float MAX_PITCH = 1.2F;
          final float VOLUME = 1.0F;
          world.playSound(null, new BlockPos(sideToIgnite.getX() + 0.5, sideToIgnite.getY() + 0.5, sideToIgnite.getZ() + 0.5),
                  SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.NEUTRAL, VOLUME, MIN_PITCH + rand.nextFloat() * (MAX_PITCH - MIN_PITCH));
          world.setBlockState(sideToIgnite, Blocks.FIRE.getDefaultState());
        }
        if (densityOfThisFace >= thresholdForDestruction) {
          world.setBlockToAir(blockPos);
        }
      }
    }
    
    if (!world.isRemote) {               
        int i = rand.nextInt(10000);
        if(i < 13 && RealmOfTheDragonsConfig.netherBreathExplosions) {
        world.createExplosion((Entity)null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 5, true);
        }
      }

    BlockBurnProperties burnProperties = getBurnProperties(iBlockState);
    if (burnProperties.burnResult == null
        || currentHitDensity.getMaxHitDensity() < burnProperties.threshold) {
      return currentHitDensity;
    }
    world.setBlockState(blockPos, burnProperties.burnResult);
    return new BreathAffectedBlock();  // reset to zero
  }

  private BlockBurnProperties getBurnProperties(IBlockState iBlockState) {
    Block block = iBlockState.getBlock();
    if (blockBurnPropertiesCache.containsKey(block)) {
      return  blockBurnPropertiesCache.get(block);
    }

    BlockBurnProperties blockBurnProperties = new BlockBurnProperties();
    IBlockState result = getSmeltingResult(iBlockState);
    blockBurnProperties.threshold = 20;
    if (result == null && RealmOfTheDragonsConfig.canFireAffectBlocks) {
      blockBurnProperties.threshold = 3;
      result = getScorchedResult(iBlockState);
    }
    if (result == null && RealmOfTheDragonsConfig.canFireAffectBlocks) {
      blockBurnProperties.threshold = 5;
      result = getVaporisedLiquidResult(iBlockState);
    }
    if (result == null && RealmOfTheDragonsConfig.canFireAffectBlocks) {
      blockBurnProperties.threshold = 100;
      result = getMoltenLavaResult(iBlockState);
    }
    blockBurnProperties.burnResult = result;
    blockBurnPropertiesCache.put(block, blockBurnProperties);
    return blockBurnProperties;
  }

  private static class BlockBurnProperties {
    public IBlockState burnResult = null;  // null if no effect
    public float threshold;
  }

  /** if sourceBlock can be smelted, return the smelting result as a block
   * @param sourceBlock
   * @return the smelting result, or null if none
   */
  private static IBlockState getSmeltingResult(IBlockState sourceBlock) {
    Block block = sourceBlock.getBlock();
    Item itemFromBlock = Item.getItemFromBlock(block);
    ItemStack itemStack;
    if (itemFromBlock != null && itemFromBlock.getHasSubtypes())     {
      int metadata = block.getMetaFromState(sourceBlock);
      itemStack = new ItemStack(itemFromBlock, 1, metadata);
    } else {
      itemStack = new ItemStack(itemFromBlock);
    }

    ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(itemStack);
    if (smeltingResult != null) {
      Block smeltedResultBlock = Block.getBlockFromItem(smeltingResult.getItem());
      if (smeltedResultBlock != null) {
        IBlockState iBlockStateSmelted = smeltedResultBlock.getStateFromMeta(smeltingResult.getMetadata());
        return iBlockStateSmelted;
      }
    } 
    if (block == Blocks.IRON_ORE) return Blocks.IRON_BLOCK.getDefaultState();
    if (block == Blocks.GOLD_ORE) return Blocks.GOLD_BLOCK.getDefaultState();
    if (block == Blocks.EMERALD_ORE) return Blocks.EMERALD_BLOCK.getDefaultState();
    if (block == Blocks.DIAMOND_ORE) return Blocks.DIAMOND_BLOCK.getDefaultState();
    if (block == Blocks.COAL_ORE) return Blocks.COAL_BLOCK.getDefaultState();
    if (block == Blocks.REDSTONE_ORE) return Blocks.REDSTONE_BLOCK.getDefaultState();
    if (block == Blocks.LAPIS_ORE) return Blocks.LAPIS_BLOCK.getDefaultState();
    if (block == Blocks.QUARTZ_ORE) return Blocks.QUARTZ_BLOCK.getDefaultState();
    return null;
  }

  /** if sourceBlock is a liquid or snow that can be molten or vaporised, return the result as a block
   *
   * @param sourceBlock
   * @return the vaporised result, or null if none
   */
  private static IBlockState getVaporisedLiquidResult(IBlockState sourceBlock) {
    Block block = sourceBlock.getBlock();
    Material material = block.getDefaultState().getMaterial();

    if (material == Material.WATER) {
      return Blocks.AIR.getDefaultState();
    } else if (material == Material.SNOW || material == Material.ICE) {
      final int SMALL_LIQUID_AMOUNT = 4;
      return Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, SMALL_LIQUID_AMOUNT);
    } else if (material == Material.PACKED_ICE || material == Material.CRAFTED_SNOW) {
      final int LARGE_LIQUID_AMOUNT = 1;
      return Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, LARGE_LIQUID_AMOUNT);
    }
    return null;
  }

  /** if sourceBlock is a block that can be melted to lava, return the result as a block
   * @param sourceBlock
   * @return the molten lava result, or null if none
   */
  private static IBlockState getMoltenLavaResult(IBlockState sourceBlock) {
    Block block = sourceBlock.getBlock();
    Material material = block.getDefaultState().getMaterial();

    if (material == Material.SAND || material == Material.CLAY
            || material == Material.GLASS || material == Material.IRON
            || material == Material.GROUND || material == Material.ROCK
            || block != Blocks.BEDROCK || block != Blocks.OBSIDIAN) {
      final int LARGE_LIQUID_AMOUNT = 1;
      return Blocks.LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, LARGE_LIQUID_AMOUNT);
    }
    return null;
  }

  /** if sourceBlock is a block that isn't flammable but can be scorched / changed, return the result as a block
   * @param sourceBlock
   * @return the scorched result, or null if none
   */
  private static IBlockState getScorchedResult(IBlockState sourceBlock) {
    Block block = sourceBlock.getBlock();
    Material material = block.getDefaultState().getMaterial();

    if (material == Material.GRASS) {
      return Blocks.DIRT.getDefaultState();
    }
    return null;
  }


  private HashMap<Block, BlockBurnProperties> blockBurnPropertiesCache = new HashMap<Block, BlockBurnProperties>();

  /** if the hitDensity is high enough, manipulate the entity (eg set fire to it, damage it)
   * A dragon can't be damaged by its own breathweapon;
   * @param world
   * @param entityID  the ID of the affected entity
   * @param currentHitDensity the hit density
   * @return the updated hit density; null if entity dead, doesn't exist, or otherwise not affected
   */
  public BreathAffectedEntity affectEntity(World world, Integer entityID, BreathAffectedEntity currentHitDensity) {
    checkNotNull(world);
    checkNotNull(entityID);
    checkNotNull(currentHitDensity);

//    if (entityID == dragon.getEntityId()) return null;

    Entity entity = world.getEntityByID(entityID);
    if (entity == null || !(entity instanceof EntityLivingBase) || entity.isDead) {
      return null;
    }

    final float CATCH_FIRE_THRESHOLD = 1.4F;
    final float BURN_SECONDS_PER_HIT_DENSITY = 1.0F;
    final float DAMAGE_PER_HIT_DENSITY = 0.5F;

    float hitDensity = currentHitDensity.getHitDensity();
    entity.setFire((int)(hitDensity * 5));
    if (currentHitDensity.applyDamageThisTick()) {
    	boolean immunetofire = entity.isImmuneToFire();
    	if(immunetofire) {
      entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), hitDensity * 0.3F);} else {
      entity.attackEntityFrom(DamageSource.IN_FIRE, hitDensity * DAMAGE_PER_HIT_DENSITY);}
    }

    return currentHitDensity;
  }

  /**
   * returns the hitDensity threshold for the given block flammability (0 - 300 as per Block.getFlammability)
   * @param flammability
   * @return the hit density threshold above which the block catches fire
   */
  private float convertFlammabilityToHitDensityThreshold(int flammability) {
    checkArgument(flammability >= 0 && flammability <= 300);
    if (flammability == 0) return Float.MAX_VALUE;
    // typical values for items are 5 (coal, logs), 20 (gates etc), 60 - 100 for leaves & flowers & grass
    // want: leaves & flowers to burn instantly; gates to take ~1 second at full power, coal / logs to take ~3 seconds
    // hitDensity of 1 is approximately 1-2 ticks of full exposure from a single beam, so 3 seconds is ~30

    float threshold = 50.0F / flammability;
    return threshold;
  }

}
