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
import net.minecraft.item.Item;

/**
 * 
 * @author Mark Gottschling on Aug 15, 2018
 *
 */
public class WitherPlanksBlock extends ModBlock {
	// logger
	public static Logger logger = LogManager.getLogger(WitherPlanksBlock.class);
	
	/**
	 * 
	 * @param material
	 */
	public WitherPlanksBlock(String modID, String name, Material material) {
		super(modID, name, material	);
		setSoundType(SoundType.WOOD);
		setCreativeTab(Treasure.TREASURE_TAB);
		this.setHardness(2.5F);	
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 */
	public WitherPlanksBlock(String modID, String name) {
		this(modID, name, Material.WOOD);
	}

	/**
	 * 
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(TreasureBlocks.WITHER_PLANKS);
    }
}
