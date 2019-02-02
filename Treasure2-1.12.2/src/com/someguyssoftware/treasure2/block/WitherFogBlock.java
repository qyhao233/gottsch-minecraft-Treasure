/**
 * 
 */
package com.someguyssoftware.treasure2.block;

import java.util.Map;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.enums.FogHeight;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Apr 18, 2018
 *
 */
public class WitherFogBlock extends FogBlock {
    
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param material
	 * @param map
	 */
	public WitherFogBlock(String modID, String name, Material material, Map<FogHeight, FogBlock> map) {
		super(modID, name, material, map);
	}
}
