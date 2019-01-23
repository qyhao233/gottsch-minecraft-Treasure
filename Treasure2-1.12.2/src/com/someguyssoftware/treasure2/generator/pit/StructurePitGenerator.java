package com.someguyssoftware.treasure2.generator.pit;

import java.util.Random;

import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.block.TreasureBlocks;
import com.someguyssoftware.treasure2.generator.GenUtil;
import com.someguyssoftware.treasure2.tileentity.ProximitySpawnerTileEntity;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfoProvider;
import com.someguyssoftware.treasure2.world.gen.structure.StructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;


/**
 * TODO should a structure pit generator be of a different class than a regular pit generator?
 * -- The return value has to be different -> a class containing the StructureInfo and the result.
 * -- Looks like I will have to load the template, get the list of pos, rotate manually, and scan manually.
 * Generates lava blocks outside the main pit to prevent players from digging down on the edges
 * @author Mark Gottschling on Dec 9, 2018
 *
 */
public class StructurePitGenerator extends AbstractPitGenerator implements IStructureInfoProvider {
	
	IPitGenerator generator;
	IStructureInfo info;
	
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
	 * @param generator
	 */
	public StructurePitGenerator(IPitGenerator generator) {
		this();
		setGenerator(generator);
	}
	
	@Override
	public boolean generateEntrance(World world, Random random, ICoords surfaceCoords, ICoords spawnCoords) {
		return getGenerator().generateEntrance(world, random, surfaceCoords, spawnCoords);
	}
	
	@Override
	public boolean generatePit(World world, Random random, ICoords surfaceCoords, ICoords spawnCoords) {
		return getGenerator().generatePit(world, random, surfaceCoords, spawnCoords);
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
			Treasure.logger.debug("generating structure room at -> {}", spawnCoords.toShortString());
			
			// TODO will want the structures organized better to say grab RARE UNDERGROUND ROOMs
			TreasureTemplate template = (TreasureTemplate) Treasure.TEMPLATE_MANAGER.getTemplate(new ResourceLocation("treasure2:underground/basic1.nbt"));
			
			// check if the yDist is big enough to accodate a room
			BlockPos size = template.getSize();
			Treasure.logger.debug("template size -> {}, offset -> {}", size, template.getOffsetY());
			
			// if size of room is greater the distance to the surface minus 3, then fail 
			if (size.getY() + template.getOffsetY() + 3 < yDist) {
				Treasure.logger.debug("Structure is too large for available space.");
				return getGenerator().generate(world, random, surfaceCoords, spawnCoords);
			}
			
			// TODO all this stuff should move into a StructureGenerator
			
			// initialize StructureInfo
			IStructureInfo info = new StructureInfo();
		
			// find the entrance block
			ICoords entranceCoords = template.findCoords(random, Blocks.GOLD_BLOCK);
			if (entranceCoords == null) {
				Treasure.logger.debug("Unable to locate entrance position.");
				return false;
			}
			ICoords chestCoords = template.findCoords(random, Blocks.CHEST);
			if (chestCoords == null) {
				Treasure.logger.debug("Unable to locate chest position.");
				return false;
			}
			
			// select a random rotation
			Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
			rotation = Rotation.CLOCKWISE_90; // TEMP
			rotation = Rotation.NONE;
			Treasure.logger.debug("rotation used -> {}", rotation);
			
			// TODO setup placement
			PlacementSettings placement = new PlacementSettings();
			placement.setRotation(rotation).setRandom(random);
			
			// NOTE these values are still relative to origin (spawnCoords);
			// TODO perfrom rotation, mirror on specials map
			// TODO test is same as what Coords.rotate creates
			ICoords newEntrance = new Coords(TreasureTemplate.transformedBlockPos(placement, entranceCoords.toPos()));
			ICoords gottschCoreCoords = entranceCoords.rotate90(size.getX());
			Treasure.logger.debug("rotation of -> {}: template -> {}, gottschcore -> {}", entranceCoords.toShortString(), 
					newEntrance.toShortString(), gottschCoreCoords.toShortString());
			
			// TODO need to find the entire list in case there are more and they need to be erased after build
			// TODO this is why using only Structure Data blocks would be ideal - they are removed natively
			// find the new chest block
			chestCoords = new Coords(TreasureTemplate.transformedBlockPos(placement, chestCoords.toPos()));
			
			// TODO adjust spawn coords to line up room entrance with pit
			BlockPos transformedSize = template.transformedSize(rotation);
			Treasure.logger.debug("transformed size -> {}", transformedSize);
			
			ICoords roomCoords = alignToPit(spawnCoords, newEntrance, transformedSize, placement);
			Treasure.logger.debug("aligned room coords -> {}", roomCoords.toShortString());
			
			// generate the structure
			template.addBlocksToWorld(world, roomCoords.toPos(), placement, 3);
			
			// TODO go back into build and remove any extra special blocks
			
			// update StrucutreInfo
			info.setCoords(roomCoords);
			info.setSize(new Coords(transformedSize));		
			info.getMap().put(Blocks.GOLD_BLOCK, newEntrance);
			info.getMap().put(Blocks.CHEST, chestCoords);
			setInfo(info);
			
			Treasure.logger.debug("Generating shaft @ " + spawnCoords.toShortString());
			
			// shaft enterance
//			buildLogLayer(world, random, surfaceCoords.add(0, -3, 0), Blocks.LOG);
//			buildLayer(world, surfaceCoords.add(0, -4, 0), Blocks.SAND);
//			buildLogLayer(world, random, surfaceCoords.add(0, -5, 0), Blocks.LOG);
			generateEntrance(world, random, surfaceCoords, spawnCoords.add(0, size.getY()+1, 0));
			
			// build the pit
//			buildPit(world, random, spawnCoords.add(0, size.getY() + 1, 0), surfaceCoords, getBlockLayers());
			generatePit(world, random, surfaceCoords, spawnCoords.add(0, size.getY() + 1, 0));
		}			
		// shaft is only 2-6 blocks long - can only support small covering
		else if (yDist >= 2) {
			// simple short pit
			new SimpleShortPitGenerator().generate(world, random, surfaceCoords, spawnCoords);
		}		
		Treasure.logger.debug("Generated Structure Pit at " + spawnCoords.toShortString());
		return true;
	}	

	/**
	 * 
	 * @param spawnCoords
	 * @param newEntrance
	 * @param transformedSize
	 * @param placement
	 * @return
	 */
	private ICoords alignToPit(ICoords spawnCoords, ICoords newEntrance, BlockPos transformedSize, PlacementSettings placement) {
		ICoords startCoords = null;
		// TODO size is not needed
		// NOTE work with rotations only for now
		
		// first offset spawnCoords by newEntrance
		startCoords = spawnCoords.add(-newEntrance.getX(), 0, -newEntrance.getZ());
		
		// make adjustments for the rotation. REMEMBER that pits are 2x2
		switch (placement.getRotation()) {
		case CLOCKWISE_90:
			startCoords = startCoords.add(1, 0, 0);
			break;
		case CLOCKWISE_180:
			break;
		case COUNTERCLOCKWISE_90:
			break;
		default:
			break;
		}
		return startCoords;
	}

	private ICoords transform(final ICoords coordsIn, final PlacementSettings placement) {
		ICoords coords = null;
		switch(placement.getRotation()) {
		case CLOCKWISE_90:
			coords = coordsIn.rotate90(5);
			break;
		}
		return coords;
	}
	
	/**
	 * @return the generator
	 */
	public IPitGenerator getGenerator() {
		return generator;
	}

	/**
	 * @param generator the generator to set
	 */
	public void setGenerator(IPitGenerator generator) {
		this.generator = generator;
	}

	/**
	 * @return the info
	 */
	@Override
	public IStructureInfo getInfo() {
		return info;
	}

	/**
	 * @param info2 the info to set
	 */
	protected void setInfo(IStructureInfo info2) {
		this.info = info2;
	}
	
}
