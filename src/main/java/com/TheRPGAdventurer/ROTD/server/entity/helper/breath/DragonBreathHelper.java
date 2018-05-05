package com.TheRPGAdventurer.ROTD.server.entity.helper.breath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.TheRPGAdventurer.ROTD.client.render.BreathWeaponEmitter;
import com.TheRPGAdventurer.ROTD.client.sound.SoundController;
import com.TheRPGAdventurer.ROTD.client.sound.SoundEffectBreathWeapon;
import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.breeds.EnumDragonBreed;
import com.TheRPGAdventurer.ROTD.server.entity.helper.DragonHelper;
import com.TheRPGAdventurer.ROTD.server.network.BreathWeaponTarget;
import com.TheRPGAdventurer.ROTD.server.network.DragonBreathControlTargets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Created by TGG on 8/07/2015.
 * Responsible for
 * - retrieving the player's selected target (based on player's input from Dragon Orb item)
 * - synchronising the player-selected target between server AI and client copy - using datawatcher
 * - rendering the breath weapon on the client
 * - performing the effects of the weapon on the server (eg burning blocks, causing damage)
 * The selection of an actual target (typically - based on the player desired target), navigation of dragon to the appropriate range,
 *   turning the dragon to face the target, is done by targeting AI.
 * DragonBreathHelper is also responsible for
 *  - tracking the current breath state (IDLE, STARTING, SUSTAINED BREATHING, STOPPING)
 *  - sound effects
 *  - adding delays for jaw open / breathing start
 *  - interrupting the beam when the dragon is facing the wrong way / the angle of the beam mismatches the head angle
 *  Usage:
 *  1) Create instance, providing the parent dragon entity and a datawatcher index to use for breathing
 *  2) call onLivingUpdate(), onDeath(), onDeathUpdate(), readFromNBT() and writeFromNBT() from the corresponding
 *     parent entity methods
 *  3a) The AI task responsible for targeting should call getPlayerSelectedTarget() to find out what the player wants
 *     the dragon to target.
 *  3b) Once the target is in range and the dragon is facing the correct direction, the AI should use setBreathingTarget()
 *      to commence breathing at the target
 *  4) getCurrentBreathState() and getBreathStateFractionComplete() should be called by animation routines for
 *     the dragon during breath weapon (eg jaw opening)
 */
public class DragonBreathHelper extends DragonHelper {
	
  private final DataParameter<String> dataParam;
  private final int BREATH_START_DURATION = 5; // ticks
  private final int BREATH_STOP_DURATION = 5; // ticks
  private BreathWeaponTarget targetBeingBreathedAt = null;  // server: the target currently being breathed at
  private BreathWeaponTarget lastBreathTargetSent = null;   // server: the last target sent to the client thru DataWatcher
  private BreathState currentBreathState = BreathState.IDLE;
  private int transitionStartTick;
  private int tickCounter = 0;
  private BreathWeaponTarget breathWeaponTarget;
  protected BreathWeaponEmitter breathWeaponEmitter = null;
	
  public DragonBreathHelper(EntityTameableDragon dragon, DataParameter<String> dataParam) {
    super(dragon);
    if (dragon.isClient()) {breathWeaponEmitter = new BreathWeaponEmitter();}
    this.dataParam = dataParam;
    dataWatcher.register(dataParam, "");
  }
  
  public enum  BreathState {
    IDLE, STARTING, SUSTAIN, STOPPING
  }

  public BreathState getCurrentBreathState() {return currentBreathState;}

  public float getBreathStateFractionComplete() {
    switch (currentBreathState) {
      case IDLE: {
        return 0.0F;
      }
      case STARTING: {
        int ticksSpentStarting = tickCounter - transitionStartTick;
        return MathHelper.clamp(ticksSpentStarting / (float)BREATH_START_DURATION, 0.0F, 1.0F);
      }
      case SUSTAIN: {
        return 0.0F;
      }
      case STOPPING: {
        int ticksSpentStopping = tickCounter - transitionStartTick;
        return MathHelper.clamp(ticksSpentStopping / (float)BREATH_STOP_DURATION, 0.0F, 1.0F);
      }
      default: {
        System.err.println("Unknown currentBreathState:" + currentBreathState);
        return 0.0F;
      }
    }
  }

  /** set the target currently being breathed at.
   * server only.
   * @param target the new target the dragon is breathing at, null = no target
    */
  public void setBreathingTarget(BreathWeaponTarget target) {
    if (dragon.isServer()) {
      targetBeingBreathedAt = target;
      boolean updateDataWatcher = false;
      if (lastBreathTargetSent == null) {
        updateDataWatcher = true;
      } else {
        updateDataWatcher = !lastBreathTargetSent.approximatelyMatches(target);
      }
      if (updateDataWatcher) {
        lastBreathTargetSent = target;
        if (target == null) {
          dataWatcher.set(dataParam, "");
        } else {
          dataWatcher.set(dataParam, target.toEncodedString());
        }
      }
    } else {
      L.warn("setBreathingTarget is only valid on server");
    }

    updateBreathState(target);
  }

  /** gets the target that the movement AI should move towards (or away from) to move to the optimal breathing distance
  */
   public BreathWeaponTarget getBreathTargetForMoving() {
    return breathWeaponTarget;
  }

  /** sets the target that the movement AI should move towards (or away from) to move to the optimal breathing distance
   * @param targetForMoving the new target - NULL for no target
   */
  public void setBreathTargetForMoving(BreathWeaponTarget targetForMoving) {
    breathWeaponTarget = targetForMoving;
  }

  /**
   * check if the dragon has a breath target that it should move towards (or away from)
   * @return true if the dragon has a movement target
   */
  public boolean hasBreathTargetForMoving() { return breathWeaponTarget != null;}

  /**
   * For tamed dragons, returns the target that their controlling player has selected using the DragonOrb.
   * @return the player's selected target, or null if no player target or dragon isn't tamed.
   */
  public BreathWeaponTarget getPlayerSelectedTarget() {
    Entity rider = dragon.getControllingPlayer();
    if (rider == null) {
      return null;
    }

    if (dragon.isClient()) {
      return getTarget();
    }

    EntityPlayerMP entityPlayerMP = (EntityPlayerMP)rider;
    BreathWeaponTarget breathWeaponTarget = DragonBreathControlTargets.getInstance().getPlayerTarget(entityPlayerMP);
    return breathWeaponTarget;
  }

  @Override
  public void onLivingUpdate() {
    ++tickCounter;
    if (dragon.isClient()) {
      onLivingUpdateClient();
    } else {
      onLivingUpdateServer();
    }
  }
  
  private void onLivingUpdateServer() {
    BreathWeaponTarget target = getTarget();
    updateBreathState(target);
    EnumDragonBreed breed = dragon.getBreedType();

    if (target != null) {
      Vec3d origin = dragon.getAnimator().getThroatPosition();
      Vec3d lookDirection = dragon.getLook(1.0f);
      Vec3d endOfLook = origin.addVector(lookDirection.x,
              lookDirection.y, 
              lookDirection.z);   
      BreathNode.Power power = dragon.getLifeStageHelper().getBreathPower();
      if (endOfLook != null && currentBreathState == BreathState.SUSTAIN) {
    	 dragon.getBreed().continueAndUpdateBreathing(dragon.getEntityWorld(), origin, endOfLook, power);
      }
    }
  }

  private void onLivingUpdateClient() {
    BreathWeaponTarget target = getTarget();
    updateBreathState(target);
    EnumDragonBreed breed = dragon.getBreedType();

    if (target != null) {
        Vec3d origin = dragon.getAnimator().getThroatPosition();
        Vec3d lookDirection = dragon.getLook(1.0f);
        Vec3d endOfLook = origin.addVector(lookDirection.x, lookDirection.y, lookDirection.z);
      if (endOfLook != null && currentBreathState == BreathState.SUSTAIN && dragon.getBreed().canBreathFire()) {       
        BreathNode.Power power = dragon.getLifeStageHelper().getBreathPower();
        dragon.getBreed().spawnBreathParticles(dragon.getEntityWorld(), power, tickCounter, origin, endOfLook);
      }
    }

    if (soundController == null) {
      soundController = new SoundController();
    }
    if (soundEffectBreathWeapon == null) {
      soundEffectBreathWeapon = new SoundEffectBreathWeapon(soundController, weaponInfoLink);
    }
    soundEffectBreathWeapon.performTick(Minecraft.getMinecraft().player);
  }

  private static final Logger L = LogManager.getLogger();

  private void updateBreathState(BreathWeaponTarget targetBeingBreathedAt) {
    switch (currentBreathState) {
      case IDLE: {
        if (targetBeingBreathedAt != null) {
          transitionStartTick = tickCounter;
          currentBreathState = BreathState.STARTING;
        }
        break;
      }
      case STARTING: {
        int ticksSpentStarting = tickCounter - transitionStartTick;
        if (ticksSpentStarting >= BREATH_START_DURATION) {
          transitionStartTick = tickCounter;
          currentBreathState = (targetBeingBreathedAt != null) ? BreathState.SUSTAIN : BreathState.STOPPING;
        }
        break;
      }
      case SUSTAIN: {
        if (targetBeingBreathedAt == null) {
          transitionStartTick = tickCounter;
          currentBreathState = BreathState.STOPPING;
        }
        break;
      }
      case STOPPING: {
        int ticksSpentStopping = tickCounter - transitionStartTick;
        if (ticksSpentStopping >= BREATH_STOP_DURATION) {
          currentBreathState = BreathState.IDLE;
        }
        break;
      }
      default: {
        System.err.println("Unknown currentBreathState:" + currentBreathState);
        return;
      }
    }
  }


  private SoundController soundController;
  private SoundEffectBreathWeapon soundEffectBreathWeapon;
  private WeaponInfoLink weaponInfoLink = new WeaponInfoLink();

  // Callback link to provide the Sound generator with state information
  public class WeaponInfoLink implements SoundEffectBreathWeapon.WeaponSoundUpdateLink {

    @Override
    public boolean refreshWeaponSoundInfo(SoundEffectBreathWeapon.WeaponSoundInfo infoToUpdate) {
      BreathWeaponTarget target = getTarget();
      EnumDragonBreed breed = dragon.getBreedType();
      Vec3d origin = dragon.getAnimator().getThroatPosition();
      infoToUpdate.dragonHeadLocation = origin;
      infoToUpdate.relativeVolume = dragon.getScale();
      infoToUpdate.lifeStage = dragon.getLifeStageHelper().getLifeStage();

      boolean isBreathing = false;
      if (target != null) {
          Vec3d lookDirection = dragon.getLook(1.0f);
          Vec3d endOfLook = origin.addVector(lookDirection.x,
                  lookDirection.y, 
                  lookDirection.z);
        if (endOfLook != null && currentBreathState == BreathState.SUSTAIN && dragon.getBreed().canBreathFire()) {
          isBreathing = true;
        }
      }
      
      infoToUpdate.breathingState = isBreathing ? SoundEffectBreathWeapon.WeaponSoundInfo.State.BREATHING
                                                : SoundEffectBreathWeapon.WeaponSoundInfo.State.IDLE;
      return true;
    }
  }

  /**
   * Get the target currently being breathed at, for this dragon:
   * 1) On the client, from the datawatcher
   * 2) On the server- previously set by AI
   * @return the target, or null for none
   */
  private BreathWeaponTarget getTarget() {
    if (dragon.isClient()) {
      String targetString = dataWatcher.get(dataParam);
      BreathWeaponTarget target = BreathWeaponTarget.fromEncodedString(targetString);
      return target;
    } else {
      return targetBeingBreathedAt;
    }
  }
  
  public BreathWeaponEmitter getEmitter() {
	  return breathWeaponEmitter;
  }
}
