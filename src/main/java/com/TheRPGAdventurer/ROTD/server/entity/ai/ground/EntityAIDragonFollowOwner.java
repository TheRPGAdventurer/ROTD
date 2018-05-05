/*
** 2016 April 26
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD.server.entity.ai.ground;

import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.ai.EntityAIDragonBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class EntityAIDragonFollowOwner extends EntityAIDragonBase {
    
    protected EntityPlayer owner;
    private float oldWaterCost;
    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;

    public EntityAIDragonFollowOwner(EntityTameableDragon dragon) {
        super(dragon);
        this.petPathfinder = dragon.getNavigator();
    }

    @Override
    public boolean shouldExecute() {
    	
        // don't follow if sitting
        if (dragon.isDragonSitting()) {
        	return false;
        }
        
       owner = (EntityPlayer) dragon.getOwner();
//        owner = dragon.getCommandingPlayer();
        
        // don't follow if ownerless 
        if (owner == null) {
            return false;
        }
        
        // don't follow if already being ridden
        if (dragon.isPassenger(owner)) {
            return false;
        }
		
        return owner.onGround;

    }
    
    @Override
    public void updateTask() {
        dragon.getNavigator().tryMoveToEntityLiving(owner, 5);
    }
    
}
