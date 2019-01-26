/**
 * 
 */
package com.someguyssoftware.treasure2.tileentity;

import java.util.Random;

import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.random.RandomHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Jan 17, 2019
 *
 */
public class ProximitySpawnerTileEntity extends AbstractProximityTileEntity {
	private ResourceLocation mobName;
	private Quantity mobNum;
	
	/**
	 * 
	 */
	public ProximitySpawnerTileEntity() {
	}

	/**
	 * @param proximity
	 */
	public ProximitySpawnerTileEntity(double proximity) {
		super(proximity);
	}

	/**
	 * 
	 */
	@Override
	public void execute(World world, Random random, Coords blockCoords, Coords playerCoords) {
    	int mobCount = RandomHelper.randomInt(getMobNum().getMinInt(), getMobNum().getMaxInt());
    	for (int i = 0; i < mobCount; i++) {

            Entity entity = EntityList.createEntityByIDFromName(getMobName(), world);

            // NOTE 4D = spawn range
            double x = (double)blockCoords.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * 4D + 0.5D;
            double y = (double)(blockCoords.getY() + world.rand.nextInt(3) - 1);
            double z = (double)blockCoords.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * 4D + 0.5D;

            entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);

            if (entity instanceof EntityLiving) {
            	EntityLiving entityLiving = (EntityLiving)entity;
            	if (entityLiving.getCanSpawnHere() && entityLiving.isNotColliding()) {                
	                entityLiving.rotationYawHead = entityLiving.rotationYaw;
	                entityLiving.renderYawOffset = entityLiving.rotationYaw;
	                entityLiving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityLiving)), (IEntityLivingData)null);
	                world.spawnEntity(entity);
	                entityLiving.playLivingSound();
            	}
            }
    	}    	
    	// self destruct
    	selfDestruct();
	}

	/**
	 * 
	 */
	private void selfDestruct() {
		this.setDead(true);
		this.getWorld().setBlockToAir(getPos());
		this.getWorld().removeTileEntity(getPos());
	}

	/**
	 * @return the mobName
	 */
	public ResourceLocation getMobName() {
		return mobName;
	}

	/**
	 * @param mobName the mobName to set
	 */
	public void setMobName(ResourceLocation mobName) {
		this.mobName = mobName;
	}

	/**
	 * @return the mobNum
	 */
	public Quantity getMobNum() {
		return mobNum;
	}

	/**
	 * @param mobNum the mobNum to set
	 */
	public void setMobNum(Quantity mobNum) {
		this.mobNum = mobNum;
	}

}
