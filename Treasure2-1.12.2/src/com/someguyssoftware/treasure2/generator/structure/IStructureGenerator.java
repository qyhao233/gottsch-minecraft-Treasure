package com.someguyssoftware.treasure2.generator.structure;

import java.util.Random;

import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;

public interface IStructureGenerator {

	/**
	 * 
	 * @param world
	 * @param random
	 * @param surfaceCoords
	 * @param spawnCoords
	 * @return
	 */
	public IStructureInfo generate(World world, Random random, TreasureTemplate template, PlacementSettings placement, ICoords spawnCoords);

}