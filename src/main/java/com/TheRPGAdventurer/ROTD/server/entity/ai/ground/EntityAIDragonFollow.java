package com.TheRPGAdventurer.ROTD.server.entity.ai.ground;

import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.ai.EntityAIDragonBase;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIDragonFollow extends EntityAIDragonBase {
    private final EntityTameableDragon dragon;
    private EntityLivingBase owner;
    World world;
    private final double followSpeed;
    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;
    float maxDist;
    float minDist;
    private float oldWaterCost;

    public EntityAIDragonFollow(EntityTameableDragon dragon, double followSpeedIn, float minDistIn, float maxDistIn) {
    	super(dragon);
        this.dragon = dragon;
        this.world = dragon.world;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = dragon.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(dragon.getNavigator() instanceof PathNavigateGround) && !(dragon.getNavigator() instanceof PathNavigateFlying))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase owner = this.dragon.getOwner();

        if (owner == null) {
            return false;
        } else if (owner instanceof EntityPlayer && ((EntityPlayer)owner).isSpectator()) {
            return false;
        } else if (this.dragon.isDragonSitting()) {
            return false;
        } else if (this.dragon.getDistanceSqToEntity(owner) < (double)(this.minDist * this.minDist)) {
            return false;
        } else if (!owner.onGround) {
        	return false;
        }
            this.owner = owner;
            return true;      
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        return !this.petPathfinder.noPath() && this.dragon.getDistanceToEntity(owner) > (double)(this.maxDist * this.maxDist) && !this.dragon.isDragonSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.dragon.getPathPriority(PathNodeType.WATER);
        this.dragon.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.owner = null;
        this.petPathfinder.clearPathEntity();
        this.dragon.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

}