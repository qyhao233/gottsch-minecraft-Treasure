package com.someguyssoftware.treasure2.generator.pit;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.cube.Cube;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.random.RandomWeightedCollection;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.block.TreasureBlocks;
import com.someguyssoftware.treasure2.generator.GenUtil;
import com.someguyssoftware.treasure2.tileentity.ProximitySpawnerTileEntity;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.TemplateManager;


/**
 * TODO should a structure pit generator be of a different class than a regular pit generator?
 * -- The return value has to be different -> a class containing the StructureMetaData and the result.
 * -- Looks like I will have to load the template, get the list of pos, rotate manually, and scan manually.
 * Generates lava blocks outside the main pit to prevent players from digging down on the edges
 * @author Mark Gottschling on Dec 9, 2018
 *
 */
public class StructurePitGenerator extends AbstractPitGenerator {
	
	/**
	 * 
	 */
	public StructurePitGenerator() {
		getBlockLayers().add(50, Blocks.AIR);
		getBlockLayers().add(25,  Blocks.SAND);
		getBlockLayers().add(15, Blocks.GRAVEL);
		getBlockLayers().add(10, Blocks.LOG);
	}
	
	/**
	 * 
	 * @param world
	 * @param random
	 * @param surfaceCoords
	 * @param spawnCoords
	 * @return
	 */
	@Override
	public boolean generate(World world, Random random, ICoords surfaceCoords, ICoords spawnCoords) {
		// is the chest placed in a cavern
		boolean inCavern = false;
		
		// check above if there is a free space - chest may have spawned in underground cavern, ravine, dungeon etc
		IBlockState blockState = world.getBlockState(spawnCoords.add(0, 1, 0).toPos());
		
		// if there is air above the origin, then in cavern. (pos in isAir() doesn't matter)
		if (blockState == null || blockState.getMaterial() == Material.AIR) {
			Treasure.logger.debug("Spawn coords is in cavern.");
			inCavern = true;
		}
		
		if (inCavern) {
			Treasure.logger.debug("Shaft is in cavern... finding ceiling.");
			spawnCoords = GenUtil.findUndergroundCeiling(world, spawnCoords.add(0, 1, 0));
			if (spawnCoords == null) {
				Treasure.logger.warn("Exiting: Unable to locate cavern ceiling.");
				return false;
			}
		}
	
		// generate shaft
		int yDist = (surfaceCoords.getY() - spawnCoords.getY()) - 2;
		Treasure.logger.debug("Distance to ySurface =" + yDist);
	
		ICoords nextCoords = null;
		if (yDist > 6) {
			// TODO will want the structures organized better to say grab RARE UNDERGROUND ROOMs
			TreasureTemplate template = (TreasureTemplate) Treasure.TEMPLATE_MANAGER.getTemplate(new ResourceLocation("treasure2:underground/basic1.nbt"));
			
			// TODO check if the yDist is big enough to accodate a room
			BlockPos size = template.getSize();
			
			// TODO select a rotation
			
			// TODO perfrom rotation, mirror on specials map
			
			// find the entrance block
			ICoords entranceCoords = template.findCoords(random, Blocks.GOLD_BLOCK);
			
			// find the chest block
			ICoords chestCoords = template.findCoords(random, Blocks.CHEST);

			// generate the structure
			template.addBlocksToWorld(world, spawnCoords.toPos(), null, 3);
			
			// TODO update the nextCoords add the entranceCoords
			
			Treasure.logger.debug("Generating shaft @ " + spawnCoords.toShortString());
			// at chest level
			nextCoords = build6WideLayer(world, random, spawnCoords, Blocks.AIR);
			
			// above the chest
			nextCoords = build6WideLayer(world, random, nextCoords, Blocks.AIR);
			nextCoords = build6WideLayer(world, random, nextCoords, Blocks.AIR);
			nextCoords = buildLogLayer(world, random, nextCoords, Blocks.LOG);
			nextCoords = buildLayer(world, nextCoords, Blocks.SAND);
			
			// shaft enterance
			buildLogLayer(world, random, surfaceCoords.add(0, -3, 0), Blocks.LOG);
			buildLayer(world, surfaceCoords.add(0, -4, 0), Blocks.SAND);
			buildLogLayer(world, random, surfaceCoords.add(0, -5, 0), Blocks.LOG);

			// build the trap
			buildTrapLayer(world, random, spawnCoords, null);
			
			// build the pit
			buildPit(world, random, nextCoords, surfaceCoords, getBlockLayers());
		}			
		// shaft is only 2-6 blocks long - can only support small covering
		else if (yDist >= 2) {
			// simple short pit
			new SimpleShortPitGenerator().generate(world, random, surfaceCoords, spawnCoords);
		}		
		Treasure.logger.debug("Generated Big Bottom Mob Trap Pit at " + spawnCoords.toShortString());
		return true;
	}	

	/**
	 * 
	 * @param world
	 * @param random
	 * @param coords
	 * @param block
	 * @return
	 */
	private ICoords build6WideLayer(World world, Random random, ICoords coords, Block block) {
		ICoords startCoords = coords.add(-2, 0, -2);
		for (int x = startCoords.getX(); x < startCoords.getX() + 6; x++) {
			for (int z = startCoords.getZ(); z < startCoords.getZ() + 6; z++) {
				GenUtil.replaceWithBlockState(world, new Coords(x, coords.getY(), z), block.getDefaultState());
			}
		}
		return coords.add(0, 1, 0);
	}
	
	/**
	 * 
	 * @param world
	 * @param random
	 * @param coords
	 * @param block
	 * @return
	 */
	public ICoords buildTrapLayer(final World world, final Random random, final ICoords coords, final Block block) {
		// spawn the mobs
    	spawnMob(world, coords.add(-2, 0, 0), "skeleton");
    	spawnMob(world, coords.add(0, 0, -2), "zombie");
    	spawnMob(world, coords.add(2, 0, 0), "zombie");
    	spawnMob(world, coords.add(0, 0, 2), "skeleton");
    	
    	// test
    	world.setBlockState(coords.add(-1, 0, 0).toPos(), TreasureBlocks.PROXIMITY_SPAWNER.getDefaultState());
    	ProximitySpawnerTileEntity te = (ProximitySpawnerTileEntity) world.getTileEntity(coords.add(-1, 0, 0).toPos());
    	te.setMobName(new ResourceLocation("minecraft:Spider"));
    	te.setMobNum(new Quantity(1, 2));
    	te.setProximity(5D);
    	
		return coords;
	}
	
}
