/**
 * 
 */
package com.someguyssoftware.treasure2.worldgen;

import java.util.List;

import com.someguyssoftware.dungeons2.model.DungeonInfo;
import com.someguyssoftware.dungeons2.registry.DungeonRegistry;
import com.someguyssoftware.gottschcore.positional.BBox;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.chest.ChestInfo;
import com.someguyssoftware.treasure2.config.TreasureConfig;
import com.someguyssoftware.treasure2.registry.ChestRegistry;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

/**
 * @author Mark Gottschling on Feb 3, 2019
 *
 */
public interface ITreasureWorldGenerator {
	
	/**
	 * 
	 * @param world
	 * @param coords
	 * @param minDistance
	 * @return
	 */
	default public boolean isValidChestLocation(World world, ICoords coords, int minDistance) {
		return isTooCloseToRegisteredChest(world, coords, minDistance) && isTooCloseToRegisterDungeon(world, coords);
	}
	
	/**
	 * 
	 * @param world
	 * @param pos
	 * @param minDistance
	 * @return
	 */
	default public boolean isTooCloseToRegisteredChest(World world, ICoords coords, int minDistance) {		
		double minDistanceSq = minDistance * minDistance;
		
		// get a list of chests
		List<ChestInfo> infos = ChestRegistry.getInstance().getEntries();

		if (infos == null || infos.size() == 0) {
			Treasure.logger.debug("Unable to locate the Chest Registry or the Registry doesn't contain any values");
			return false;
		}
		
		for (ChestInfo info : infos) {
			// calculate the distance to the other chest
			double distance = coords.getDistanceSq(info.getCoords());
			if (distance < minDistanceSq) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param world
	 * @param coords
	 * @return
	 */
	default public boolean isTooCloseToRegisterDungeon(World world, ICoords coords) { // TODO rename
		if (Loader.isModLoaded(TreasureConfig.DUNGEONS2_MODID)) {
			// get a list of dungeons
			List<DungeonInfo> infos = DungeonRegistry.getInstance().getEntries();
			for (DungeonInfo info : infos) {
				BBox bbox = new BBox(new Coords(info.getMinX(), info.getMinY(), info.getMinZ()),
						new Coords(info.getMaxX(), info.getMaxY(), info.getMaxZ()));
				
				if (bbox.intersects(new BBox(coords))) return true;
			}
		}
		return false;
	}
}
