/**
 * 
 */
package com.someguyssoftware.treasure2.world.gen.structure;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling on Jan 18, 2019
 *
 */
public class NBTStructureScanner {

	/**
	 * 
	 */
	public NBTStructureScanner() {	}
	
	/**
	 * TODO what should an input be to scan for? a list of strings to check against NBT values
	 * or a list of blockStates to test against, ie chest, spawner, gold block, door, etc.
	 * @param r
	 * @return
	 */
	public static IStructureInfo scan(ResourceLocation r, List<IBlockState> searchFor) {
		IStructureInfo data = new StructureInfo();
		return data;
	}

}
