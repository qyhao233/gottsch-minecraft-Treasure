package com.someguyssoftware.treasure2.generator.structure;

import java.util.Random;

import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomWeightedCollection;
import com.someguyssoftware.treasure2.world.gen.structure.StructureInfo;
import com.someguyssoftware.treasure2.world.gen.structure.TreasureTemplate;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IStructureGenerator {

	/**
	 * 
	 * @param world
	 * @param random
	 * @param surfaceCoords
	 * @param spawnCoords
	 * @return
	 */
	public StructureInfo generate(World world, Random random, TreasureTemplate template, ICoords spawnCoords);

}