/**
 * 
 */
package com.someguyssoftware.treasure2.generator.special;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.block.TreasureBlocks;
import com.someguyssoftware.treasure2.config.Configs;
import com.someguyssoftware.treasure2.config.IWitchCloisterConfig;
import com.someguyssoftware.treasure2.enums.Rarity;
import com.someguyssoftware.treasure2.enums.StructureMarkers;
import com.someguyssoftware.treasure2.generator.GenUtil;
import com.someguyssoftware.treasure2.generator.chest.CauldronChestGenerator;
import com.someguyssoftware.treasure2.generator.structure.StructureGenerator;
import com.someguyssoftware.treasure2.tileentity.ProximitySpawnerTileEntity;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * @author Mark Gottschling on Jan 29, 2019
 *
 */
public class WitchCloisterGenerator {

	private static final String WITCH_CLOISTER_LOCATION = "treasure:special/witch-cloister";

	private static final String[] WITCH_CLOISTER_LOCATIONS = {
			"treasure:special/witch-cloister-base",
			"treasure:special/witch-cloister-mid",
			"treasure:special/witch-cloister-top"
	};
	
	/**
	 * 
	 */
	public WitchCloisterGenerator() {
		
	}

	/**
	 * 
	 * @param world
	 * @param random
	 * @param coords
	 * @param config
	 * @return
	 */
	public boolean generate(World world, Random random, ICoords coords, IWitchCloisterConfig config) {
		ICoords surfaceCoords = null;

		// 1. determine y-coord of land for markers
		surfaceCoords = WorldInfo.getSurfaceCoords(world, coords);
		Treasure.logger.debug("Surface Coords @ {}", surfaceCoords.toShortString());
		if (surfaceCoords == null || surfaceCoords == WorldInfo.EMPTY_COORDS) {
			Treasure.logger.debug("Returning due to surface coords == null or EMPTY_COORDS");
			return false;
		}
		
		// 2. get base template which is on the ground
//		TreasureTemplate template = (TreasureTemplate) Treasure.TEMPLATE_MANAGER.getTemplate(new ResourceLocation(WITCH_CLOISTER_LOCATION));
		TreasureTemplate template = (TreasureTemplate) Treasure.TEMPLATE_MANAGER.getTemplate(new ResourceLocation(WITCH_CLOISTER_LOCATIONS[0]));

		if (template == null) {
			Treasure.logger.debug("could not find random template");
			return false;
		}
		
		// 3. get the offset
		int offset = 0;
		ICoords offsetCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.OFFSET));
		if (offsetCoords != null) {
			offset = -offsetCoords.getY();
		}

		// 4. update the spawn coords with the offset
		ICoords spawnCoords = surfaceCoords.add(0, offset, 0);
		
		// 4.5 find the entrance block
		ICoords entranceCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.ENTRANCE));
		if (entranceCoords == null) {
			Treasure.logger.debug("Unable to locate entrance position.");
			return false;
		}
		
		// 5. select a rotation
		Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
		Treasure.logger.debug("witch cloister rotation used -> {}", rotation);
				
		// 6. setup placement
		PlacementSettings placement = new PlacementSettings();
		placement.setRotation(rotation).setRandom(random);
		
		// 6.5 update the entrance
		entranceCoords = new Coords(TreasureTemplate.transformedBlockPos(placement, entranceCoords.toPos()));
		
		// 7. get the transformed size
		BlockPos transformedSize = template.transformedSize(rotation);
		
		// 8. check if there is solid ground at spawn corods (not on surface since cloister should be set into the ground/swamp)
		if (!WorldInfo.isSolidBase(world, spawnCoords, transformedSize.getX(), transformedSize.getZ(), 75)) {
			Treasure.logger.debug("Coords -> [{}] does not meet {}% solid base requirements for size -> {} x {}", 75, spawnCoords.toShortString(), transformedSize.getX(), transformedSize.getY());
			return false;
		}
				
		// 9. generate the structure
		IStructureInfo info = new StructureGenerator().generate(world, random, template, placement, spawnCoords);
		if (info == null) {
			Treasure.logger.debug("Witch cloister did not return structure info -> {}", spawnCoords);
			return false;			
		}
		Treasure.logger.debug("returned info -> {}", info);
		
		/*
		 * 10. get special blocks
		 */
		// interrogate info for spawners and any other special block processing (except chests that are handler by caller
		List<ICoords> spawnerCoords = (List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.SPAWNER));
		List<ICoords> proximityCoords = (List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.PROXIMITY_SPAWNER));
		List<ICoords> chestCoordsList = (List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.CHEST));
		List<ICoords> bossChestCoordsList = (List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.BOSS_CHEST));

		// TODO load and place mid structure (repeating find specials and add to master)
		for (int i = 1; i < WITCH_CLOISTER_LOCATIONS.length; i++) {
			// get template
			template = (TreasureTemplate) Treasure.TEMPLATE_MANAGER.getTemplate(new ResourceLocation(WITCH_CLOISTER_LOCATIONS[i]));
			// get the entrance coords of the next piece of structure
			ICoords subStructEntranceCoords = template.findCoords(random, GenUtil.getMarkerBlock(StructureMarkers.ENTRANCE));
			if (entranceCoords == null) {
				Treasure.logger.debug("Unable to locate entrance position.");
				return false;
			}
			// update the rotate entrance coords
			subStructEntranceCoords = new Coords(TreasureTemplate.transformedBlockPos(placement, subStructEntranceCoords.toPos()));
			// determine spawn coords for structure
			ICoords subSpawnCoords = spawnCoords
					.add(entranceCoords.getX(), 0, entranceCoords.getZ())
					.add(-subStructEntranceCoords.getX(), 0, -subStructEntranceCoords.getZ());
			
			// build the struct
			IStructureInfo subInfo = new StructureGenerator().generate(world, random, template, placement, subSpawnCoords);
			if (subInfo == null) {
				Treasure.logger.debug("Witch cloister sub structure did not return structure info.");
			}
			else {
				Treasure.logger.debug("returned info -> {}", info);
			}
			
			/*
			 *  TODO all these coords are relative to the structure that they were a part of which isn't necessarily the same dimensions
			 *  of the base structure. so they are need to be coverted to be relative to the spawnCoords BEFORE being added to the master lists.
			 *  (function: (subSpawnCoords - spawnCoords) + markerCoords
			 */
			// find and add specials to master
			spawnerCoords.addAll((List<ICoords>) subInfo.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.SPAWNER)));
			proximityCoords.addAll((List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.PROXIMITY_SPAWNER)));
			chestCoordsList.addAll((List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.CHEST)));
			bossChestCoordsList.addAll((List<ICoords>) info.getMap().get(GenUtil.getMarkerBlock(StructureMarkers.BOSS_CHEST)));
		}

		/*
		 * to get the correct coords to an of the mapped specials, you must use info.getCoords() because the spawn coords may have shifted to align with the shaft
		 */
		ICoords bossChestCoords = spawnCoords.add(bossChestCoordsList.get(0));
		
		/*
		 * 11. Add mobs
		 */
		// populate vanilla spawners with zombies or witches
		for (ICoords c : spawnerCoords) {
			ICoords c2 = spawnCoords.add(c);			
			world.setBlockState(c2.toPos(), Blocks.MOB_SPAWNER.getDefaultState());
			TileEntityMobSpawner te = (TileEntityMobSpawner) world.getTileEntity(c2.toPos());
			ResourceLocation r = null;
			if (c2.delta(spawnCoords).getY() < 3)  {
				// spawn with Drowned
				r = new ResourceLocation("drowned");
			}
			else {
				r = new ResourceLocation("witch");
			}
			te.getSpawnerBaseLogic().setEntityId(r);			
		}
		
		// populate proximity spawners with zombies or witches
		for (ICoords c : proximityCoords) {
			ICoords c2 = spawnCoords.add(c);
	    	world.setBlockState(c2.toPos(), TreasureBlocks.PROXIMITY_SPAWNER.getDefaultState());
	    	ProximitySpawnerTileEntity te = (ProximitySpawnerTileEntity) world.getTileEntity(c2.toPos());
			ResourceLocation r = null;
			if (c2.delta(spawnCoords).getY() < 3)  {
				// spawn with Drowned
				r = new ResourceLocation("drowned");
				te.setMobNum(new Quantity(1, 3));
			}
			else {
				r = new ResourceLocation("witch");
		    	te.setMobNum(new Quantity(1, 2));
			}
	    	te.setMobName(r);
	    	te.setProximity(10D);
		}
		
		// TODO find the boss chest and regular chests
		
		// add chest
		CauldronChestGenerator chestGen = new CauldronChestGenerator();
		chestGen.generate(world, random, bossChestCoords, Rarity.EPIC, Configs.chestConfigs.get(Rarity.EPIC)); 
		
		return true;
	}
	
}
