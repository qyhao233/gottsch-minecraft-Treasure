/**
 * 
 */
package com.someguyssoftware.treasure2.generator.marker;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.block.TreasureBlocks;
import com.someguyssoftware.treasure2.enums.StructureMarkers;
import com.someguyssoftware.treasure2.generator.GenUtil;
import com.someguyssoftware.treasure2.generator.structure.IStructureGenerator;
import com.someguyssoftware.treasure2.generator.structure.StructureGenerator;
import com.someguyssoftware.treasure2.tileentity.ProximitySpawnerTileEntity;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplateManager.StructureType;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.DungeonHooks;

/**
 * @author Mark Gottschling on Jan 28, 2019
 *
 */
public class RandomStructureMarkerGenerator implements IMarkerGenerator {

	/**
	 * 
	 */
	public RandomStructureMarkerGenerator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.generator.marker.IMarkerGenerator#generate(net.minecraft.world.World, java.util.Random, com.someguyssoftware.gottschcore.positional.ICoords)
	 */
	@Override
	public boolean generate(World world, Random random, ICoords coords) {

		List<Template> templates = Treasure.TEMPLATE_MANAGER.getTemplatesByType(StructureType.ABOVEGROUND);
		TreasureTemplate template = (TreasureTemplate) templates.get(random.nextInt(templates.size()));
		if (template == null) {
			Treasure.logger.debug("could not find random template");
			return false;
		}

		// get the offset
		int offset = 0;
		ICoords offsetCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.OFFSET));
		if (offsetCoords != null) {
			offset = -offsetCoords.getY();
		}

		// update the spawn coords with the offset
		ICoords spawnCoords = coords.add(0, offset, 0);

		// find entrance
		ICoords entranceCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.ENTRANCE));
		if (entranceCoords == null) {
			Treasure.logger.debug("Unable to locate entrance position.");
			return false;
		}

		// select a rotation
		Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
		Treasure.logger.debug("above ground rotation used -> {}", rotation);
				
		// setup placement
		PlacementSettings placement = new PlacementSettings();
		placement.setRotation(rotation).setRandom(random);
		
		// NOTE these values are still relative to origin (spawnCoords);
		ICoords newEntrance = new Coords(TreasureTemplate.transformedBlockPos(placement, entranceCoords.toPos()));
		
		/*
		 *  adjust spawn coords to line up room entrance with pit
		 */
		BlockPos transformedSize = template.transformedSize(rotation);
		spawnCoords = IStructureGenerator.alignEntranceToCoords(spawnCoords, newEntrance, transformedSize, placement);
				
		// if offset is 2 or less, then determine if the solid ground percentage is validsd
		if (offset >= -2) {
			if (!WorldInfo.isSolidBase(world, spawnCoords, transformedSize.getX(), transformedSize.getZ(), 70)) {
				Treasure.logger.debug("Coords -> [{}] does not meet {}% solid base requirements for size -> {} x {}", 70, spawnCoords.toShortString(), transformedSize.getX(), transformedSize.getY());
				return new GravestoneMarkerGenerator().generate(world, random, coords);
			}
		}
		
		// generate the structure
		IStructureInfo info = new StructureGenerator().generate(world, random, template, placement, spawnCoords);
		if (info == null) return false;			
		Treasure.logger.debug("returned info -> {}", info);
		
		// interrogate info for spawners and any other special block processing (except chests that are handler by caller
		List<ICoords> spawnerCoords = (List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.SPAWNER));
		List<ICoords> proximityCoords = (List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.PROXIMITY_SPAWNER));

		// populate vanilla spawners
		for (ICoords c : spawnerCoords) {
			ICoords c2 = spawnCoords.add(c);
			world.setBlockState(c2.toPos(), Blocks.MOB_SPAWNER.getDefaultState());
			TileEntityMobSpawner te = (TileEntityMobSpawner) world.getTileEntity(c2.toPos());
			ResourceLocation r = DungeonHooks.getRandomDungeonMob(random);
			te.getSpawnerBaseLogic().setEntityId(r);
		}
		
		// populate proximity spawners
		for (ICoords c : proximityCoords) {
			ICoords c2 = spawnCoords.add(c);
	    	world.setBlockState(c2.toPos(), TreasureBlocks.PROXIMITY_SPAWNER.getDefaultState());
	    	ProximitySpawnerTileEntity te = (ProximitySpawnerTileEntity) world.getTileEntity(c2.toPos());
	    	ResourceLocation r = DungeonHooks.getRandomDungeonMob(random);
	    	te.setMobName(r);
	    	te.setMobNum(new Quantity(1, 2));
	    	te.setProximity(10D);
		}		
		return true;
	}

}
