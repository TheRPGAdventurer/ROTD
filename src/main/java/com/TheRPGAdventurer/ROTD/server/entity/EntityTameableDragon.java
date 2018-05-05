/*
 ** 2012 August 13
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.server.entity;

import static net.minecraft.entity.SharedMonsterAttributes.ARMOR_TOUGHNESS;
import static net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE;
import static net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE;
import static net.minecraft.entity.SharedMonsterAttributes.KNOCKBACK_RESISTANCE;
import static net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;
import com.TheRPGAdventurer.ROTD.RealmOfTheDragonsConfig;
import com.TheRPGAdventurer.ROTD.RealmOfTheDragonsLootTables;
import com.TheRPGAdventurer.ROTD.client.initialization.ModArmour;
import com.TheRPGAdventurer.ROTD.client.initialization.ModItems;
import com.TheRPGAdventurer.ROTD.client.initialization.ModTools;
import com.TheRPGAdventurer.ROTD.client.model.anim.DragonAnimator;
import com.TheRPGAdventurer.ROTD.client.sound.ModSounds;
import com.TheRPGAdventurer.ROTD.server.entity.ai.ground.EntityAIDragonSit;
import com.TheRPGAdventurer.ROTD.server.entity.ai.path.PathNavigateFlying;
import com.TheRPGAdventurer.ROTD.server.entity.breeds.DragonBreed;
import com.TheRPGAdventurer.ROTD.server.entity.breeds.EnumDragonBreed;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonBodyHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonBrain;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonBreedHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonInteractHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonLifeStageHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonLookHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonMoveHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonParticleHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonReproductionHelper;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonSoundManager;
import com.TheRPGAdventurer.ROTD.server.entity.helper.breath.DragonBreathHelper;
import com.TheRPGAdventurer.ROTD.server.network.MessageDragonArmor;
import com.TheRPGAdventurer.ROTD.server.util.ItemUtils;
import com.TheRPGAdventurer.ROTD.util.PrivateFields;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Here be dragons.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @Modifier James Miller <TheRPGAdventurer.>
 */
public class EntityTameableDragon extends EntityTameable implements IShearable, IEntityMultiPart {

	private static final Logger L = LogManager.getLogger();

	public static final IAttribute MOVEMENT_SPEED_AIR = new RangedAttribute(null, "generic.movementSpeedAir", 1.5, 0.0,
			Double.MAX_VALUE).setDescription("Movement Speed Air").setShouldWatch(true);

	// base attributes
	public static final double BASE_GROUND_SPEED = RealmOfTheDragonsConfig.DragonSpeedInGround;
	public static final double BASE_AIR_SPEED = RealmOfTheDragonsConfig.DragonSpeedInAir;
	public static final double BASE_DAMAGE = 10.0D;
	public static final double BASE_ARMOR = 20.0D;
	public static final double BASE_TOUGHNESS = 30.0D;
    public static final float BASE_WIDTH = 2.75f;
    public static final float BASE_HEIGHT = 2.4f;
	public static final float RESISTANCE = 20.0F;
	public static final double BASE_FOLLOW_RANGE = 70;
	public static final double BASE_FOLLOW_RANGE_FLYING = BASE_FOLLOW_RANGE * 2;
	public static final int HOME_RADIUS = 64;
	public static final double IN_AIR_THRESH = 10;

	protected int        ticksSinceLastAttack;
	public static int    ticksShear;

	// data value IDs
	private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager
			.<Boolean>createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DATA_SADDLED = EntityDataManager
			.<Boolean>createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> CHESTED = EntityDataManager
			.<Boolean>createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> ARMOR = EntityDataManager
			.<Integer>createKey(EntityTameableDragon.class, DataSerializers.VARINT);
	private static final DataParameter<Optional<UUID>> DATA_BREEDER = EntityDataManager
			.<Optional<UUID>>createKey(EntityTameableDragon.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<String> DATA_BREED = EntityDataManager
			.<String>createKey(EntityTameableDragon.class, DataSerializers.STRING);
	private static final DataParameter<Integer> DATA_REPRO_COUNT = EntityDataManager
			.<Integer>createKey(EntityTameableDragon.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DATA_TICKS_SINCE_CREATION = EntityDataManager
			.<Integer>createKey(EntityTameableDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> DRAGON_SCALES = EntityDataManager
    		.<Byte>createKey(EntityTameableDragon.class, DataSerializers.BYTE);
	private static final DataParameter<String> DATA_BREATH_WEAPON = EntityDataManager
			.<String>createKey(EntityTameableDragon.class, DataSerializers.STRING); 

	// data NBT IDs
	private static final String NBT_ARMOR     = "Armor";
	private static final String NBT_SADDLED   = "Saddle";
	private static final String NBT_SITTING   = "Sitting";
	private static final String NBT_SHEARED   = "Sheared";
	private static final String NBT_CHESTED   = "Chested";

	// server/client delegates
	private final Map<Class, DragonHelper> helpers = new HashMap<>();

	// client-only delegates
	private final DragonBodyHelper bodyHelper = new DragonBodyHelper(this);

	private EntityAIDragonSit AIDragonSit;
	public EntityEnderCrystal healingEnderCrystal;
	public DragonInventory dragonInv;
	private ItemStackHandler itemHandler = null;
	private boolean hasChestVarChanged = false;
    private boolean isBreathingFire;
//    public boolean onGround2;
    private int fireTicks; 
    private int inAirTicks;
    public final EntityAITasks attackTasks;
    public DragonAnimator animator;

	public EntityTameableDragon(World world) {
		super(world);
		List<Entity> parts = new ArrayList<>();
		parts.add(dragonPartBody);
		
        // override EntityBodyHelper field, which is private and has no setter
        // required to fixate body while sitting. also slows down rotation while standing.
        try {
            ReflectionHelper.setPrivateValue(EntityLiving.class, this, new DragonBodyHelper(this), PrivateFields.ENTITYLIVING_BODYHELPER);
        } catch (Exception ex) {
            L.warn("Can't override EntityBodyHelper", ex);
        }

        // override EntityLookHelper field, which is private and has no setter
        try {
            ReflectionHelper.setPrivateValue(EntityLiving.class, this, new DragonLookHelper(this), PrivateFields.ENTITYLIVING_LOOKHELPER);
        } catch (Exception ex) {
            L.warn("Can't override EntityLookHelper", ex);
        }

		partArray = parts.toArray(new Entity[0]);
		
		attackTasks = new EntityAITasks(world != null ? world.profiler : null);

		// set base size
		setSize(BASE_WIDTH, BASE_HEIGHT);

		// enables walking over blocks
		stepHeight = 1;

		// create entity delegates
		addHelper(new DragonBreedHelper(this, DATA_BREED));
		addHelper(new DragonLifeStageHelper(this, DATA_TICKS_SINCE_CREATION));
		addHelper(new DragonReproductionHelper(this, DATA_BREEDER, DATA_REPRO_COUNT));
		addHelper(new DragonBreathHelper(this, DATA_BREATH_WEAPON));
		addHelper(new DragonSoundManager(this));
		addHelper(new DragonInteractHelper(this));
		
		initDragonInv();

		if (isClient()) {
			addHelper(new DragonParticleHelper(this));
//			addHelper(new DragonAnimator(this));
		} else {
			addHelper(new DragonBrain(this));
		}

		moveHelper = new DragonMoveHelper(this);
		AIDragonSit = new EntityAIDragonSit(this);

		// init helpers
		helpers.values().forEach(DragonHelper::applyEntityAttributes);
		animator = new DragonAnimator(this);
	}

	@Override
	protected float updateDistance(float p_110146_1_, float p_110146_2_) {
		// required to fixate body while sitting. also slows down rotation while
		// standing.
		bodyHelper.updateRenderAngles();
		return p_110146_2_;
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(DATA_FLYING, false);
		dataManager.register(DATA_SADDLED, false);
		dataManager.register(CHESTED, false);
        dataManager.register(DRAGON_SCALES, Byte.valueOf((byte)0));		
		dataManager.register(ARMOR, 0);
	}
	
    @Override
    protected void updateAITasks() {
    	attackTasks.onUpdateTasks();      
    }

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		getAttributeMap().registerAttribute(MOVEMENT_SPEED_AIR);
		getAttributeMap().registerAttribute(ATTACK_DAMAGE);
		getEntityAttribute(MOVEMENT_SPEED).setBaseValue(BASE_GROUND_SPEED);
		getEntityAttribute(MOVEMENT_SPEED_AIR).setBaseValue(BASE_AIR_SPEED);
		getEntityAttribute(ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
		getEntityAttribute(FOLLOW_RANGE).setBaseValue(BASE_FOLLOW_RANGE);
		getEntityAttribute(KNOCKBACK_RESISTANCE).setBaseValue(RESISTANCE);
		getEntityAttribute(ARMOR_TOUGHNESS).setBaseValue(BASE_TOUGHNESS);
	}

	/**
	 * Returns true if the dragon is saddled.
	 */
	public boolean isSaddled() {
		return dataManager.get(DATA_SADDLED);
	}

	/**
	 * Set or remove the saddle of the dragon.
	 */
	public void setSaddled(boolean saddled) {
		L.trace("setSaddled({})", saddled);
		dataManager.set(DATA_SADDLED, saddled);
	}
	
	// used to be called isChestedLeft
	public boolean isChested() {
		return this.dataManager.get(CHESTED);
	}

	public void setChested(boolean chested) {
		this.dataManager.set(CHESTED, chested);
		this.hasChestVarChanged = true;
	}
	
	/**
	 * 1 equals iron
	 * 2 equals gold 
	 * 3 equals diamond
	 * @return 0 no armor
	 */
	public int getArmor() {
		return this.dataManager.get(ARMOR);
	}

	public void setArmor(int armorType) {
		this.dataManager.set(ARMOR, armorType);
	}


	public boolean canFly() {
		// eggs and hatchlings can't fly
		return !isEgg() && !isHatchling();
	}

	/**
	 * Returns true if the entity is flying.
	 */
	public boolean isFlying() {
		return dataManager.get(DATA_FLYING);
	}

	/**f
	 * Set the flying flag of the entity.
	 */
	public void setFlying(boolean flying) {
		L.trace("setFlying({})", flying);
		dataManager.set(DATA_FLYING, flying);
	}

	/**
	 * Causes this entity to lift off if it can fly.
	 */
	public void liftOff() {
		L.trace("liftOff");
		if (canFly()) {
            // stronger jump for an easier lift-off
            motionY += 0.5;
            inAirTicks += 20;
			jump();
		}
	}

	@Override
	protected float getJumpUpwardsMotion() {
		// stronger jumps for easier lift-offs
		return canFly() ? 1 : super.getJumpUpwardsMotion();
	}

	/**
	 * Called when the mob is falling. Calculates and applies fall damage.
	 */
	@Override
	public void fall(float distance, float damageMultiplier) {
		// ignore fall damage if the entity can fly
		if (!canFly()) {
			super.fall(distance, damageMultiplier);
		}
	}

	public int getTicksSinceLastAttack() {
		return ticksSinceLastAttack;
	}

	/**
	 * Returns the AITask responsible of the sit logic
	 */
	public EntityAIDragonSit getAIDragonSit() {
		return this.AIDragonSit;
	}

	public boolean isDragonSitting() {
		return (((Byte) this.dataManager.get(TAMED)).byteValue() & 1) != 0;
	}

	public void setDragonSitting(boolean sitting) {
		byte b0 = ((Byte) this.dataManager.get(TAMED)).byteValue();

		if (sitting) {
			this.dataManager.set(TAMED, Byte.valueOf((byte) (b0 | 1)));
		} else {
			this.dataManager.set(TAMED, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean(NBT_SADDLED, isSaddled());
		nbt.setBoolean(NBT_SITTING, this.isSitting());
		nbt.setInteger(NBT_ARMOR, this.getArmor());
		nbt.setBoolean(NBT_CHESTED, this.isChested());
        nbt.setBoolean(NBT_SHEARED, this.getSheared());
//        nbt.setBoolean("OnGround2", this.onGround2);
//		nbt.setBoolean(NBT_CHESTED_RIGHT, this.isChestedInRight());
		if (dragonInv != null) {
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < this.dragonInv.getSizeInventory(); ++i) {
				ItemStack itemstack = this.dragonInv.getStackInSlot(i);
				if (!itemstack.isEmpty()) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("Slot", (byte) i);
					itemstack.writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}
			nbt.setTag("Items", nbttaglist);
		}
		if (this.getCustomNameTag() != null && !this.getCustomNameTag().isEmpty()) {
			nbt.setString("CustomName", this.getCustomNameTag());
		}

		helpers.values().forEach(helper -> helper.writeToNBT(nbt));
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSaddled(nbt.getBoolean(NBT_SADDLED));
		this.setChested(nbt.getBoolean(NBT_CHESTED));
        this.setSheared(nbt.getBoolean(NBT_SHEARED));
 //       this.onGround2 = nbt.getBoolean("OnGround2");
		if (this.AIDragonSit != null) {this.AIDragonSit.setDragonSitting(nbt.getBoolean(NBT_SITTING));}this.setDragonSitting(nbt.getBoolean(NBT_SITTING));	
		helpers.values().forEach(helper -> helper.readFromNBT(nbt));
		this.setArmor(nbt.getInteger(NBT_ARMOR));
		if (dragonInv != null) {
			NBTTagList nbttaglist = nbt.getTagList("Items", 10);
			initDragonInv();
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot") & 255;
				this.dragonInv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
			}
		} else {
			NBTTagList nbttaglist = nbt.getTagList("Items", 10);
			initDragonInv();
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot") & 255;
				this.initDragonInv();
				this.dragonInv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
				//this.setArmorInSlot(j, this.getIntFromArmor(ItemStack.loadItemStackFromNBT(nbttagcompound)));
				ItemStack saddle = dragonInv.getStackInSlot(0);
				ItemStack chest = dragonInv.getStackInSlot(1);
				if (world.isRemote) {
					RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
					RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
					RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, this.getIntFromArmor(dragonInv.getStackInSlot(2))));
				}
			}
		}
	}

	/**
	 * returns the pitch of the dragon's body
	 *
	 * @return
	 */
	public float getBodyPitch() {
		return getAnimator().getBodyPitch();
	}
	
    /**
     * Returns the distance to the ground while the entity is flying.
     */
    public double getAltitude() {
      BlockPos groundPos = world.getHeight(getPosition());
      double altitude = posY - groundPos.getY();
      return altitude;
    }

	@Override
	public void onLivingUpdate() {
		helpers.values().forEach(DragonHelper::onLivingUpdate);                                                           //		if(!isFlying() && getControllingPlayer() == null &&getPassenger().size>=2) {//		removePassengers();//	}
		
		if (isServer()) {
			final float DUMMY_MOVETIME = 0; 
            final float DUMMY_MOVESPEED = 0;
            animator.setMovement(DUMMY_MOVETIME, DUMMY_MOVESPEED);
            float netYawHead = getRotationYawHead() - renderYawOffset;
            animator.setLook(netYawHead, rotationPitch);
            animator.tickingUpdate();
			animator.animate();
			// set home position near owner when tamed
			if (isTamed()) {
				Entity owner = getOwner();
				if (owner != null) {
					setHomePosAndDistance(owner.getPosition(), HOME_RADIUS);
				}
			}
			
			// delay flying state for 10 ticks (0.5s)
            if (!onGround) {
                inAirTicks++;
            } else {
                inAirTicks = 0;
            }

			// update flying state based on the distance to the ground 
			boolean flying = canFly() && inAirTicks > IN_AIR_THRESH && !isInWater() && !isInLava();
			if (flying != isFlying()) {

				// notify client
				setFlying(flying);

				// clear tasks (needs to be done before switching the navigator!)
				getBrain().clearTasks();

				// update AI follow range (needs to be updated before creating
				// new PathNavigate!)
				getEntityAttribute(FOLLOW_RANGE).setBaseValue(flying ? BASE_FOLLOW_RANGE_FLYING : BASE_FOLLOW_RANGE);

				// update pathfinding method
				if (flying) {
					navigator = new PathNavigateFlying(this, world);
				} else {
					navigator = new PathNavigateGround(this, world);
				}

				// tasks need to be updated after switching modes
				getBrain().updateAITasks();
			}
	    } else {
	    	animator.tickingUpdate();
	    }
	

		if (ticksSinceLastAttack >= 0) { // used for jaw animation
			++ticksSinceLastAttack;
			if (ticksSinceLastAttack > 1000) {
				ticksSinceLastAttack = -1; // reset at arbitrary large value
			}
		}
		
		if (hasChestVarChanged && dragonInv != null && !this.isChested()) {
			for (int i = 3; i < 30; i++) {
				if (!dragonInv.getStackInSlot(i).isEmpty()) {
					if (!world.isRemote) {
						this.entityDropItem(dragonInv.getStackInSlot(i), 1);
					}
					dragonInv.removeStackFromSlot(i);
				}
			}
			hasChestVarChanged = false;
		}
		
		updateShearing();
//		updateForRiding();
		updateRandomParticles();
		updateDragonEnderCrystal();
		regenerateHealth();
		updateDragonHitBox();
		
		super.onLivingUpdate();
	}

	@Override
	public void travel(float strafe, float forward, float vertical) {
		// disable method while flying, the movement is done entirely by
		// moveEntity() and this one just makes the dragon to fall slowly when
		// hovering
		if (!isFlying()) {
			super.travel(strafe, forward, vertical);
		}
	}
	
    /**
     * Called when the mob's health reaches 0.
     */
	public void onDeath(DamageSource src) {
		super.onDeath(src);
		if (dragonInv != null && !this.world.isRemote) {
			for (int i = 0; i < dragonInv.getSizeInventory(); ++i) {
				ItemStack itemstack = dragonInv.getStackInSlot(i);
				if (!itemstack.isEmpty()) {
					this.entityDropItem(itemstack, 0.0F);
				}
			}
		}
	}

	/**
	 * Handles entity death timer, experience orb and particle creation
	 */
	@Override
	protected void onDeathUpdate() {
		helpers.values().forEach(DragonHelper::onDeathUpdate);

		// unmount any riding entities
		removePassengers();

		// freeze at place
		motionX = motionY = motionZ = 0;
		rotationYaw = prevRotationYaw;
		rotationYawHead = prevRotationYawHead;

		if (isEgg()) {
			setDead();
		} else {
			// actually delete entity after the time is up
			if (deathTime >= getMaxDeathTime()) {
				setDead();
			}
		}

		deathTime++;

	}

	@Override
	public void setDead() {
		helpers.values().forEach(DragonHelper::onDeath);
		super.setDead();
	}

	@Override
	public String getName() {
		// return custom name if set
		if (hasCustomName()) {
			return getCustomNameTag();
		}

		// return default breed name otherwise
		String entName = EntityList.getEntityString(this);
		String breedName = getBreedType().getName().toLowerCase();
		return I18n.translateToLocal("entity." + entName + "." + breedName + ".name");
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	@Override
	protected SoundEvent getAmbientSound() {
		return getSoundManager().getLivingSound();
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound(DamageSource src) {
		return getSoundManager().getHurtSound();
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return getSoundManager().getDeathSound();
	}

	/**
	 * Plays living's sound at its position
	 */
	@Override
	public void playLivingSound() {
		getSoundManager().playLivingSound();
	}

	@Override
	public void playSound(SoundEvent soundIn, float volume, float pitch) {
		getSoundManager().playSound(soundIn, volume, pitch);
	}

	/**
	 * Plays step sound at given x, y, z for the entity
	 */
	@Override
	protected void playStepSound(BlockPos entityPos, Block block) {
		getSoundManager().playStepSound(entityPos, block);
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	@Override
	protected float getSoundVolume() {
		// note: unused, managed in playSound()
		return 1;
	}

	/**
	 * Gets the pitch of living sounds in living entities.
	 */
	@Override
	protected float getSoundPitch() {
		// note: unused, managed in playSound()
		return 1;
	}

	/**
	 * Get number of ticks, at least during which the living entity will be silent.
	 */
	@Override
	public int getTalkInterval() {
		return getSoundManager().getTalkInterval();
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return getBreed().getCreatureAttribute();
	}

	/**
	 * I made this method so any player can make the dragon sit, possibly be used
	 * for other interactions,
	 * 
	 * @author TheRPGAdventurer
	 */
	@Nullable
	public EntityPlayer getCommandingPlayer() {
		for (int i = 0; i < world.playerEntities.size();) {
			EntityPlayer entityplayer = world.playerEntities.get(i);
			return entityplayer;
		}   return null;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets
	 * into the saddle on a pig.
	 */
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		// don't interact with eggs!
		if (isEgg()) {
			return !this.canBeLeashedTo(player);
		}

		// baby dragons are tameable now! :D
		if (this.isChild() && !isTamed() && this.isBreedingItem(item)) {
			ItemUtils.consumeEquipped(player, this.getBreed().getBreedingItem());
			tamedFor(player, getRNG().nextInt(5) == 0);
			return true;
		}

		// inherited interaction
		if (super.processInteract(player, hand)) {
			return true;
		}

		if (player.isSneaking() && isTamed()) {
			this.openGUI(player);
			return true;
		}

		return getInteractHelper().interact(player, item);
	}

	public void tamedFor(EntityPlayer player, boolean successful) {
		if (successful) {
			setTamed(true);
			navigator.clearPathEntity(); // replacement for setPathToEntity(null);
			setAttackTarget(null);
			setOwnerId(player.getUniqueID());
			playTameEffect(true);
			world.setEntityState(this, (byte) 7);
		} else {
			playTameEffect(false);
			world.setEntityState(this, (byte) 6);
		}
	}

	public boolean isTamedFor(EntityPlayer player) {
		return isTamed() && isOwner(player);
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it
	 * (wheat, carrots or seeds depending on the animal type)
	 */
	@Override
	public boolean isBreedingItem(ItemStack item) {
		return getBreed().getBreedingItem() == item.getItem();
	}

	/**
	 * Returns the height of the eyes. Used for looking at other entities.
	 */
	@Override
	public float getEyeHeight() {
		float eyeHeight = super.getEyeHeight();
		if (isEgg()) {
			eyeHeight = 0.2F;
		}
		if (isHatchling()) {
			eyeHeight = 1.8F;
		}
		if (isJuvenile()) {
			eyeHeight = 2.30F;
		}
		if (isSitting()) {
			eyeHeight *= 0.8f;
		}
		return eyeHeight;
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding this
	 * one.
	 */
	@Override
	public double getMountedYOffset() {
		return (isSitting() ? 1.7f : 2.0f) * getScale();
	}

	/**
	 * Returns render size modifier
	 */
	@Override
	public float getRenderSizeModifier() {
		return getScale();
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when
	 * colliding.
	 */
	@Override
	public boolean canBePushed() {
		return super.canBePushed() && isEgg();
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	@Override
	protected boolean canDespawn() {
		if (isTamed() || isEgg() || isHatchling()) {
			return false;
		} else {
			return RealmOfTheDragonsConfig.canDragonDespawn;
		}
	}

	/**
	 * returns true if this entity is by a ladder, false otherwise
	 */
	@Override
	public boolean isOnLadder() {
		// this better doesn't happen...
		return false;
	}

	/**
	 * Drop 0-2 items of this living's type.
	 * 
	 * @param par1
	 *            - Whether this entity has recently been hit by a player.
	 * @param par2
	 *            - Level of Looting used to kill this mob.
	 */
	@Override
	protected void dropFewItems(boolean par1, int par2) {
		super.dropFewItems(par1, par2);

		// drop saddle if equipped
		if (isSaddled()) {
			dropItem(Items.SADDLE, 1);
		}
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		boolean attacked = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
				(float) getEntityAttribute(ATTACK_DAMAGE).getAttributeValue());

		if (attacked) {
			applyEnchantments(this, entityIn);
		}

		if (getBreedType() == EnumDragonBreed.WITHER) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
		}

		return attacked;
	}

	@Override
	public void swingArm(EnumHand hand) {
		// play eating sound
		playSound(getSoundManager().getAttackSound(), 1, 0.7f);

		// play attack animation
		if (world instanceof WorldServer) {
			((WorldServer) world).getEntityTracker().sendToTracking(this, new SPacketAnimation(this, 0));
		}

		ticksSinceLastAttack = 0;
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isBeingRidden() && source.getTrueSource() != null && this.getControllingPassenger() != null
				&& source.getTrueSource() == this.getControllingPassenger()) {
			return false;
		}

		if (isInvulnerableTo(source)) {
			return false;
		}
		
		if(amount > 50) {
			return false;
		}
		
        if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
            this.attackEntityFromPart(this.dragonPartBody, source, amount);
        }

		// don't just sit there!
		AIDragonSit.setDragonSitting(false);
		
		float damageReduction = getArmorResistance() + 3.0F;
		amount -= damageReduction;

		return super.attackEntityFrom(source, amount);
	}

	
	/**
	 * 1 equals iron
	 * 2 equals gold 
	 * 3 equals diamond
	 * @return 0 no armor
	 */
	public int getArmorResistance() {
		if(getArmor() == 1) {return 2;}
		if(getArmor() == 2) {return 1;}
		if(getArmor() == 3) {return 3;}
		return 0;
	}

	/**
	 * Return whether this entity should be rendered as on fire.
	 */
	@Override
	public boolean canRenderOnFire() {
		return super.canRenderOnFire() && !getBreed().isImmuneToDamage(DamageSource.IN_FIRE);
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	@Override
	public boolean canMateWith(EntityAnimal mate) {
		return getReproductionHelper().canMateWith(mate);
	}

	/**
	 * This function is used when two same-species animals in 'love mode' breed to
	 * generate the new baby animal.
	 */
	@Override
	public EntityAgeable createChild(EntityAgeable mate) {
		return getReproductionHelper().createChild(mate);
	}

	private void addHelper(DragonHelper helper) {
		L.trace("addHelper({})", helper.getClass().getName());
		helpers.put(helper.getClass(), helper);
	}

	private <T extends DragonHelper> T getHelper(Class<T> clazz) {
		return (T) helpers.get(clazz);
	}

	public DragonBreedHelper getBreedHelper() {
		return getHelper(DragonBreedHelper.class);
	}

	public DragonLifeStageHelper getLifeStageHelper() {
		return getHelper(DragonLifeStageHelper.class);
	}

	public DragonReproductionHelper getReproductionHelper() {
		return getHelper(DragonReproductionHelper.class);
	}

	public DragonParticleHelper getParticleHelper() {
		return getHelper(DragonParticleHelper.class);
	}

	public DragonBreathHelper getBreathHelper() {
		return getHelper(DragonBreathHelper.class); 
	}

	public DragonAnimator getAnimator() {
		return animator;
	}

	public DragonSoundManager getSoundManager() {
		return getHelper(DragonSoundManager.class);
	}

	public DragonBrain getBrain() {
		return getHelper(DragonBrain.class);
	}

	public DragonInteractHelper getInteractHelper() {
		return getHelper(DragonInteractHelper.class);
	}

	/**
	 * Returns the breed for this dragon.
	 * 
	 * @return breed
	 */
	public EnumDragonBreed getBreedType() {
		return getBreedHelper().getBreedType();
	}

	/**
	 * Sets the new breed for this dragon.
	 * 
	 * @param type
	 *            new breed
	 */
	public void setBreedType(EnumDragonBreed type) {
		getBreedHelper().setBreedType(type);
	}

	public DragonBreed getBreed() {
		return getBreedType().getBreed();
	}

	/**
	 * For vehicles, the first passenger is generally considered the controller and
	 * "drives" the vehicle. For example, Pigs, Horses, and Boats are generally
	 * "steered" by the controlling passenger.
	 */
	@Override
	public Entity getControllingPassenger() {
		List<Entity> list = getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		// must always return false or the vanilla movement code interferes
		// with DragonMoveHelper
		return false;
	}
	
	public boolean canPassengerSteer2() {
		Entity entity = this.getControllingPassenger();

        if (entity instanceof EntityPlayer) {
            return ((EntityPlayer)entity).isUser();
        } else {
            return !this.world.isRemote;
        }
	}

	public EntityPlayer getControllingPlayer() {
		Entity entity = getControllingPassenger();
		if (entity instanceof EntityPlayer && !(entity instanceof EntityAnimal)) {
			return (EntityPlayer) entity;
		} else {
			return null;
		}
	}

	public void setRidingPlayer(EntityPlayer player) {
		L.trace("setRidingPlayer({})", player.getName());
		player.rotationYaw = rotationYaw;
		player.rotationPitch = rotationPitch;
		player.startRiding(this);
	}

	// server-only flags
	private BitSet controlFlags;

	public void setControlFlags(BitSet flags) {
		controlFlags = flags;
	}

	public BitSet getControlFlags() {
		return controlFlags;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			double px = posX;
			double py = posY + getMountedYOffset() + passenger.getYOffset();
			double pz = posZ;
			
			// dragon position is the middle of the model and the saddle is on
			// the shoulders, so move player forwards on Z axis relative to the
			// dragon's rotation to fix that
			Vec3d pos = new Vec3d(0, 0, 0.8 * getScale());
			pos = pos.rotateYaw((float) Math.toRadians(-renderYawOffset)); // oops
			px += pos.x;
			py += pos.y;
			pz += pos.z;

			passenger.setPosition(px, py, pz);

			// fix rider rotation
			if (passenger instanceof EntityLiving) {
				EntityLiving rider = ((EntityLiving) passenger);
				rider.prevRotationPitch = rider.rotationPitch;
				rider.prevRotationYaw = rider.rotationYaw;
				rider.renderYawOffset = renderYawOffset;
			}
		}
	}

	public boolean isInvulnerableTo(DamageSource src) {
		Entity srcEnt = src.getImmediateSource();
		if (srcEnt != null) {
			// ignore own damage
			if (srcEnt == this) {
				return true;
			}

			// ignore damage from riders
			if (isPassenger(srcEnt)) {
				return true;
			}
		}

		// don't drown as egg
		if (src.damageType.equals("drown") && isEgg()) {
			return true;
		}

		return getBreed().isImmuneToDamage(src);
	}

	/**
	 * Returns the entity's health relative to the maximum health.
	 * 
	 * @return health normalized between 0 and 1
	 */
	public double getHealthRelative() {
		return getHealth() / (double) getMaxHealth();
	}

	public int getDeathTime() {
		return deathTime;
	}

	public int getMaxDeathTime() {
		return 120;
	}

	public boolean canBeLeashedTo(EntityPlayer player) {
		return this.isEgg() || this.isAdult() || this.isChild();
	}

	public void setImmuneToFire(boolean isImmuneToFire) {
		L.trace("setImmuneToFire({})", isImmuneToFire);
		this.isImmuneToFire = isImmuneToFire;
	}

	public void setAttackDamage(double damage) {
		L.trace("setAttackDamage({})", damage);
		getEntityAttribute(ATTACK_DAMAGE).setBaseValue(damage);
	}

	/**
	 * Public wrapper for protected final setScale(), used by DragonLifeStageHelper.
	 * 
	 * @param scale
	 */
	public void setScalePublic(float scale) {
		double posXTmp = posX;
		double posYTmp = posY;
		double posZTmp = posZ;
		boolean onGroundTmp = onGround;

		setScale(scale);

		// workaround for a vanilla bug; the position is apparently not set correcty
		// after changing the entity size, causing asynchronous server/client
		// positioning
		setPosition(posXTmp, posYTmp, posZTmp);

		// otherwise, setScale stops the dragon from landing while it is growing
		onGround = onGroundTmp;
	}

	/**
	 * The age value may be negative or positive or zero. If it's negative, it get's
	 * incremented on each tick, if it's positive, it get's decremented each tick.
	 * Don't confuse this with EntityLiving.getAge. With a negative value the Entity
	 * is considered a child.
	 */
	@Override
	public int getGrowingAge() {
		// adapter for vanilla code to enable breeding interaction
		return isAdult() ? 0 : -1;
	}

	/**
	 * The age value may be negative or positive or zero. If it's negative, it get's
	 * incremented on each tick, if it's positive, it get's decremented each tick.
	 * With a negative value the Entity is considered a child.
	 */
	@Override
	public void setGrowingAge(int age) {
		// managed by DragonLifeStageHelper, so this is a no-op
	}

	/**
	 * Sets the scale for an ageable entity according to the boolean parameter,
	 * which says if it's a child.
	 */
	@Override
	public void setScaleForAge(boolean p_98054_1_) {
		// managed by DragonLifeStageHelper, so this is a no-op
	}
	
    /**
     * Get's the appopriate age via ticksSinceCreation for interactions like
     * appropriate age for riding the dragon appropriate age for opening gui etc.
     */
//    public boolean isInAppropriateAgeForInteraction() {
 //   	return this.getLifeStageHelper().getTicksSinceCreation() >= 45000;	
 //   }

	/**
	 * Returns the size multiplier for the current age.
	 * 
	 * @return scale
	 */
	public float getScale() {
		return getLifeStageHelper().getScale();
	}

	public boolean isEgg() {
		return getLifeStageHelper().isEgg();
	}

	public boolean isHatchling() {
		return getLifeStageHelper().isHatchling();
	}

	public boolean isJuvenile() {
		return getLifeStageHelper().isJuvenile();
	}

	public boolean isAdult() {
		return getLifeStageHelper().isAdult();
	}

	@Override
	public boolean isChild() {
		return !isAdult();
	}
	
	/**
	 * Checks if this entity is running on a client.
	 * 
	 * Required since MCP's isClientWorld returns the exact opposite...
	 * 
	 * @return true if the entity runs on a client or false if it runs on a server
	 */
	public final boolean isClient() {
		return world.isRemote;
	}

	/**
	 * Checks if this entity is running on a server.
	 * 
	 * @return true if the entity runs on a server or false if it runs on a client
	 */
	public final boolean isServer() {
		return !world.isRemote;
	}

	protected ResourceLocation getLootTable() {
		switch (this.getBreedType()) {
		case END:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_END;
		case NETHER:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_NETHER;
		case JADE:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_JADE;
		case GARNET:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_GARNET;
		case RUBY:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_RUBY;
		case SAPPHIRE:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_SAPPHIRE;
		case AMETHYST:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_AMETHYST;
		case SKELETON:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_SKELETON;
		case WITHER:
			return RealmOfTheDragonsLootTables.ENTITIES_DRAGON_SKELETON;
		default:
			return null;

		}

	}

	protected Item getShearDropItem() {

		switch (this.getBreedType()) {
		case END:
			return ModItems.EnderDragonScales;
		case NETHER:
			return ModItems.NetherDragonScales;
		case JADE:
			return ModItems.JadeDragonScales;
		case GARNET:
			return ModItems.GarnetDragonScales;
		case RUBY:
			return ModItems.RubyDragonScales;
		case SAPPHIRE:
			return ModItems.SapphireDragonScales;
		case AMETHYST:
			return ModItems.AmethystDragonScales;
		default:
			return null;

		}
	}
	
	public boolean getSheared() {
        return (((Byte)this.dataManager.get(DRAGON_SCALES)).byteValue() & 16) != 0;
    }

    /**
     * make a dragon sheared if set to true
     */
    public void setSheared(boolean sheared){
        byte b0 = ((Byte)this.dataManager.get(DRAGON_SCALES)).byteValue();

        if (sheared) {
               dataManager.set(DRAGON_SCALES, Byte.valueOf((byte)(b0 | 16)));
        } else {
               dataManager.set(DRAGON_SCALES, Byte.valueOf((byte)(b0 & -17)));
        }
    }

    @Override 
    public boolean isShearable(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos) {
    	return item != null && item.getItem() == ModTools.diamond_shears && !this.isChild() && !this.getSheared() && ticksShear <= 0;
         
    }
    
    /**
     * when the dragon rotates its head left-right (yaw), how fast does it move?
     *
     * @return max yaw speed in degrees per tick
     */
    public float getHeadYawSpeed() {
        return 10.0F;
    }

    /**
     * when the dragon rotates its head up-down (pitch), how fast does it move?
     *
     * @return max pitch speed in degrees per tick
     */
    public float getHeadPitchSpeed() {
        return 40;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
    	this.setSheared(true);
        int i = 1 + this.rand.nextInt(2);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int j = 0; j < i; ++j)
            ret.add(new ItemStack(this.getShearDropItem()));
        
        ticksShear = 2500;
        playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        playSound(ModSounds.ENTITY_DRAGON_GROWL, 1.0F, 1.0F);
        
        
        return ret;
    }

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	@Override
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		EnumDragonBreed currentType = getBreedType();
		super.onStruckByLightning(lightningBolt);
		Random random = new Random();
		if (currentType == EnumDragonBreed.SKELETON) {
			this.setBreedType(EnumDragonBreed.WITHER);

			if (world.getWorldInfo().isThundering() && currentType == EnumDragonBreed.SKELETON && isSitting()
					|| isEgg()) {
				world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX, this.posY, this.posZ, true));
			} this.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 2, 1); this.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 2, 1);
		}
		
		addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 35 * 20));
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		Biome biome = this.world.getBiome(new BlockPos(this));

		 if (world.provider.getDimension() == 0) {
			 this.setBreedType(EnumDragonBreed.AMETHYST);
		 }
		
		return null;
	}

	/**
	 * updates particles (Spawns smoke particles when a nether dragon hits water)
	 */
	private void updateRandomParticles() {
		if (world instanceof WorldServer && isWet() && getBreedType() == EnumDragonBreed.NETHER && !isEgg()) {
			((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double) posX + 0.5D,
					(double) posY + getEyeHeight(), (double) posZ + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
		}
		
	}
	
	private void regenerateHealth() {
		if (!isEgg() && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 13 == 0) {
			Random rand = new Random();
			this.heal(rand.nextInt(5));
		}
	}
	
	private void updateShearing() {
		if (ticksShear <= 0) {setSheared(false);}
		if (ticksShear >= 0) {ticksShear--;}
	}
	
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 3;
    }
    
    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    public void applyEntityCollision(Entity entityIn) {
    	
        if (entityIn instanceof EntityBoat) {
            if (entityIn.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY)
            {
                super.applyEntityCollision(entityIn);
            }
        }
        else if (entityIn.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY)
        {
            super.applyEntityCollision(entityIn);
        }
    }
	
	private void updateForRiding() {
		this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.getTeamCollisionPredicate(this));

        if (!list.isEmpty())
        {
            boolean flag = !this.world.isRemote; //&& !(this.getControllingPassenger() instanceof EntityPlayer)

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity = list.get(j);

                if (!entity.isPassenger(this))
                {
                    if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer))
                    {
                        entity.startRiding(this);
                    }
                    else
                    {
                        this.applyEntityCollision(entity);
                    }
                }
            }
        }
    }
	
    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
    private void updateDragonEnderCrystal() {
      if (getBreedType() == EnumDragonBreed.END) {
        if (this.healingEnderCrystal != null) {
            if (this.healingEnderCrystal.isDead) {
            	this.healingEnderCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            	this.setHealth(this.getHealth() + 1.0F);
        		addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 15 * 20));
            }
        }

        if (this.rand.nextInt(10) == 0) {
            List<EntityEnderCrystal> list = this.world.<EntityEnderCrystal>getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().grow(32.0D));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;

            for (EntityEnderCrystal entityendercrystal1 : list) {
                double d1 = entityendercrystal1.getDistanceSqToEntity(this);

                if (d1 < d0)
                {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.healingEnderCrystal = entityendercrystal;
           }
        }
    }

	private void initDragonInv() {
		int numberOfInventoryforChest = 27;
		DragonInventory animalchest = this.dragonInv;
		this.dragonInv = new DragonInventory("dragonInv", 3 + numberOfInventoryforChest, this);
		this.dragonInv.setCustomName(this.getName());
		if (animalchest != null) {
			int i = Math.min(animalchest.getSizeInventory(), this.dragonInv.getSizeInventory());
			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = animalchest.getStackInSlot(j);
				if (!itemstack.isEmpty()) {
					this.dragonInv.setInventorySlotContents(j, itemstack.copy());
				}
			}

			if (world.isRemote) {
				ItemStack saddle = animalchest.getStackInSlot(0);
				ItemStack chest_left = animalchest.getStackInSlot(1);
				ItemStack chest_right = animalchest.getStackInSlot(3);
				RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
				RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, chest_left != null && chest_left.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest_left.isEmpty() ? 1 : 0));
				RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, chest_right != null && chest_right.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest_right.isEmpty() ? 1 : 0));
				RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, this.getIntFromArmor(animalchest.getStackInSlot(2))));
			}
		}
	}
	
	public int getIntFromArmor(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModArmour.dragonarmor_iron) {
			return 1;
		}
		if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModArmour.dragonarmor_gold) {
			return 2;
		}
		if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModArmour.dragonarmor_diamond) {
			return 3;
		}
		
		return 0;
	}

	public void openGUI(EntityPlayer playerEntity) {
		if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity)) && getLifeStageHelper().getTicksSinceCreation() >= 45000) {
			playerEntity.openGui(RealmOfTheDragons.instance, 0, this.world, this.getEntityId(), 0, 0);
		} else if (!this.world.isRemote && getLifeStageHelper().getTicksSinceCreation() >= 45000) {
			playerEntity.sendStatusMessage(new TextComponentTranslation("entity.dragon.tooYoung", new Object[0]), true);
		}
	}

	public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
		int j = inventorySlot - 500 + 2;
		if (j >= 0 && j < this.dragonInv.getSizeInventory()) {
			this.dragonInv.setInventorySlotContents(j, itemStackIn);
			return true;
		} else {
			return false;
		}
	}
	
    
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability,
			net.minecraft.util.EnumFacing facing) {
		if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) itemHandler;
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability,
			net.minecraft.util.EnumFacing facing) {
		return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}
	
	public void refreshInventory() {
		ItemStack saddle = this.dragonInv.getStackInSlot(0);
		ItemStack leftChestforInv = this.dragonInv.getStackInSlot(1);
		this.setSaddled(saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty());
		this.setChested(leftChestforInv != null && leftChestforInv.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !leftChestforInv.isEmpty());
		this.setArmor(getIntFromArmor(this.dragonInv.getStackInSlot(2)));
		if (this.world.isRemote) {
			RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
			RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, leftChestforInv != null && leftChestforInv.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !leftChestforInv.isEmpty() ? 1 : 0));
			RealmOfTheDragons.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, this.getIntFromArmor(this.dragonInv.getStackInSlot(2))));
		}
	}
	
	public class DragonInventory extends ContainerHorseChest {

		public DragonInventory(String inventoryTitle, int slotCount, EntityTameableDragon hippogryph) {
			super(inventoryTitle, slotCount);
			this.addInventoryChangeListener(new DragonInventoryListener(hippogryph));
		}
	}

	class DragonInventoryListener implements IInventoryChangedListener {

		EntityTameableDragon dragon;

		public DragonInventoryListener(EntityTameableDragon dragon) {
			this.dragon = dragon;
		}

		@Override
		public void onInventoryChanged(IInventory invBasic) {
			dragon.refreshInventory();
		}
	}
	
	private final Entity partArray[];
    public MultiPartEntityPart[] dragonPartArray;
    /** The head bounding box of a dragon */
    public MultiPartEntityPart dragonPartHead = new MultiPartEntityPart(this, "head", 6.0F, 6.0F);
    public MultiPartEntityPart dragonPartNeck = new MultiPartEntityPart(this, "neck", 6.0F, 6.0F);
    /** The body bounding box of a dragon */
    public MultiPartEntityPart dragonPartBody = new MultiPartEntityPart(this, "body", 8.0F, 8.0F);
    public MultiPartEntityPart dragonPartTail1 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);
    public MultiPartEntityPart dragonPartTail2 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);
    public MultiPartEntityPart dragonPartTail3 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);
    public MultiPartEntityPart dragonPartWing1 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F);
    public MultiPartEntityPart dragonPartWing2 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F);
    /** Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer. */
    public int ringBufferIndex = -1;
    public double[][] ringBuffer = new double[64][3];

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart dragonPart, DamageSource source, float damage) {
        if (dragonPart != this.dragonPartHead) {
            damage = damage / 4.0F + Math.min(damage, 1.0F);
        }

        if (damage < 0.01F) {
            return false;
        }
		return true;
	}
	
	public void updateDragonHitBox() {
		float angle;
		double dx, dy, dz;
		angle = (((renderYawOffset + 180) * 3.141593F) / 180F);

		dx = posX - MathHelper.sin(angle) * 3.0;
		dy = posY + 0.1;
		dz = posZ + MathHelper.cos(angle) * 3.0;
		dragonPartBody.setPosition(dx, dy, dz);
		dragonPartBody.width = dragonPartBody.height = 6.0F;
		
		dragonPartBody.onUpdate();
	}
	
}