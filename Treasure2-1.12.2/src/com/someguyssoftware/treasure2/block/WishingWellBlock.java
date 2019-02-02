/**
 * 
 */
package com.someguyssoftware.treasure2.block;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.block.ModBlock;
import com.someguyssoftware.treasure2.Treasure;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

/**
 * @author Mark Gottschling on Sep 19, 2014
 *
 */
public class WishingWellBlock extends ModBlock {
	// logger
	public static Logger logger = LogManager.getLogger(WishingWellBlock.class);
	
	/**
	 * 
	 * @param material
	 */
	public WishingWellBlock(String modID, String name, Material material) {
		super(modID, name, material	);
		setSoundType(SoundType.STONE);
		setCreativeTab(Treasure.TREASURE_TAB);
		this.setHardness(2.0F);	
	}

	/**
	 * Drops vanilla mossy cobblestone instead of wishing well block i.e. the well loses it's magic on break.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.MOSSY_COBBLESTONE);
    }
}
