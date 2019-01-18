/**
 * 
 */
package com.someguyssoftware.treasure2.structure;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;

/**
 * @author Mark Gottschling on Jan 18, 2019
 *
 */
public class StructureMetaData implements IStructureMetaData {
	private int width, depth, height;
	/*
	 * a list of all selected coords
	 */
	private List<ICoords> coords;
	
	// TODO can we make these more generic
	private List<ICoords> chests;
	
	private List<ICoords> spawners;
	
	/*
	 * map by blockState. this method assumes that a list of blockstates will be provided to scan for.
	 */
	private Multimap<IBlockState, ICoords> map;
	
	/*
	 * TODO should look for Data Structure blocks and record/map their values? test an nbt structure
	 */
	
	/**
	 * 
	 */
	public StructureMetaData() {
		map = ArrayListMultimap.create();
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the map
	 */
	public Multimap<IBlockState, ICoords> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Multimap<IBlockState, ICoords> map) {
		this.map = map;
	}

}
