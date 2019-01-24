/**
 * 
 */
package com.someguyssoftware.treasure2.generator.structure;

import java.util.Random;

import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.generator.GenUtil;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * @author Mark Gottschling on Jan 24, 2019
 *
 */
public class StructureGenerator implements IStructureGenerator {

	@Override
	public IStructureInfo generate(World world, Random random, TreasureTemplate template, PlacementSettings placement,
			ICoords spawnCoords) {
		
		// generate the structure
		template.addBlocksToWorld(world, spawnCoords.toPos(), placement, 3);
		
		// remove any extra special blocks
		for (ICoords coords : template.getMapCoords()) {
			ICoords c = TreasureTemplate.transformedCoords(placement, coords);
			world.setBlockToAir(spawnCoords.toPos().add(c.toPos()));
			Treasure.logger.debug("removing mapped block -> {} : {}", c, spawnCoords.toPos().add(c.toPos()));
		}
		
		return null;
	}

}
