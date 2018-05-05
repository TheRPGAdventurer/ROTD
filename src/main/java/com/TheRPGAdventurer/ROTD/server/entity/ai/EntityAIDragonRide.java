/*
 ** 2012 March 18
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.server.entity.ai;

import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.network.BreathWeaponTarget;
import com.TheRPGAdventurer.ROTD.util.math.MathX;
import com.TheRPGAdventurer.ROTD.util.reflection.PrivateAccessor;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * Abstract "AI" for player-controlled movements.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonRide extends EntityAIDragonBase implements PrivateAccessor {

    protected EntityPlayer rider;
    private Vec3d inates;

    public EntityAIDragonRide(EntityTameableDragon dragon) {
        super(dragon);
        setMutexBits(0xffffffff);
    }
    
    @Override
    public boolean shouldExecute() {   
        rider = dragon.getControllingPlayer();
        return rider != null;
    }

    @Override
    public void startExecuting() {
        dragon.getNavigator().clearPathEntity();
    }
    
    @Override
    public void updateTask() {
        Vec3d wp = rider.getLookVec();
        double x = dragon.posX;
        double y = dragon.posY;
        double z = dragon.posZ;
        
        // if we're breathing at a target, look at it
        BreathWeaponTarget breathWeaponTarget = dragon.getBreathHelper().getPlayerSelectedTarget();
        if (breathWeaponTarget != null && dragon.getBreed().canBreathFire()) {
            Vec3d dragonEyePos = dragon.getPositionVector().addVector(0, dragon.getEyeHeight(), 0);
            breathWeaponTarget.setEntityLook(dragon.world, dragon.getLookHelper(), dragonEyePos,
                    dragon.getHeadYawSpeed(), dragon.getHeadPitchSpeed());
        }
                
        // control direction with movement keys
        if (rider.moveStrafing != 0 || rider.moveForward != 0) {
            if (rider.moveForward < 0) {
                wp = wp.rotateYaw(MathX.PI_F);
            } else if (rider.moveStrafing > 0) {
                wp = wp.rotateYaw(MathX.PI_F * 0.5f);
            } else if (rider.moveStrafing < 0) {
                wp = wp.rotateYaw(MathX.PI_F * -0.5f);
            } 
            
            x += wp.x * 10;
            y += wp.y * 10;
            z += wp.z * 10;
            
        }
      
        // lift off with a jump
        if (!dragon.isFlying()) {
            if (entityIsJumping(rider)) {
                dragon.liftOff();
            }
        } 
        
        dragon.getMoveHelper().setMoveTo(x,y,z,1.2);
    }
}
