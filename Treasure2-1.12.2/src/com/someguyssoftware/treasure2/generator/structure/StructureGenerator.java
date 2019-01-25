/**
 * 
 */
package com.someguyssoftware.treasure2.generator.structure;

import java.util.Random;

import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.enums.StructureMarkers;
import com.someguyssoftware.treasure2.generator.GenUtil;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.StructureInfo;
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
		
		// get the transformed size
		BlockPos transformedSize = template.transformedSize(placement.getRotation());
		// get the transformed entrance
		ICoords entranceCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.ENTRANCE));
		if (entranceCoords != null) {
			entranceCoords = new Coords(TreasureTemplate.transformedBlockPos(placement, entranceCoords.toPos()));
		}
		// get the transformed chest
		ICoords chestCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.CHEST));
		if (chestCoords != null) {
			chestCoords = new Coords(TreasureTemplate.transformedBlockPos(placement, chestCoords.toPos()));
		}
		
		// TODO make more generic of processing all specials and adding them to the StructureInfo
		
		// update StrucutreInfo
		IStructureInfo info = new StructureInfo();
		info.setCoords(spawnCoords);
		info.setSize(new Coords(transformedSize));		
		info.getMap().put(GenUtil.getMarkerBlock(StructureMarkers.ENTRANCE), entranceCoords);
		info.getMap().put(GenUtil.getMarkerBlock(StructureMarkers.CHEST), chestCoords);
		
		return info;
	}

}
