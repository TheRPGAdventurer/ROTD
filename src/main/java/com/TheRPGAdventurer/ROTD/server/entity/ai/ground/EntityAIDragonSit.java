package com.TheRPGAdventurer.ROTD.server.entity.ai.ground;

import com.TheRPGAdventurer.ROTD.server.entity.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.server.entity.ai.EntityAIDragonBase;

import net.minecraft.entity.EntityLivingBase;

public class EntityAIDragonSit extends EntityAIDragonBase {
    private final EntityTameableDragon dragon;
    /** If the EntityTameable is sitting. */
    private boolean isSitting;

    public EntityAIDragonSit(EntityTameableDragon dragon) {
    	super(dragon);
        this.dragon = dragon;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.dragon.isTamed())
        {
            return false;
        }
        else if (this.dragon.isInWater())
        {
            return false;
        }
        else if (!this.dragon.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.dragon.getCommandingPlayer();

            if (entitylivingbase == null)
            {
                return true;
            }
            else
            {
                return this.dragon.getDistanceSqToEntity(entitylivingbase) < 30.0D && entitylivingbase.getRevengeTarget() != null ? false : this.isSitting;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()  {
        this.dragon.getNavigator().clearPathEntity();
        this.dragon.setDragonSitting(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.dragon.setDragonSitting(false);
    }

    /**
     * Sets the sitting flag.
     */
    public void setDragonSitting(boolean sitting) {
        this.isSitting = sitting;
    }
}