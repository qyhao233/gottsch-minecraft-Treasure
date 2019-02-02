/**
 * 
 */
package com.someguyssoftware.treasure2.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * 
 * @author Mark Gottschling on Jan 29, 2019
 *
 */
public interface IWitchDenConfig extends IBiomeListConfig {
	/**
	 * Loads the Forge mod Configuration file.
	 * @param file
	 * @return the loaded Forge mod Configuration;
	 */
	default public Configuration load(File file) {
        Configuration config = new Configuration(file);
        config.load();
		return config;
	}
	
	public boolean isDenAllowed();
	public int getChunksPerDen();
	public double getGenProbability();

	IWitchDenConfig setDenAllowed(boolean wellAllowed);

	IWitchDenConfig setChunksPerDen(int chunksPerWell);

	IWitchDenConfig setGenProbability(double genProbability);

	String[] getRawBiomeBlackList();

	IWitchDenConfig setRawBiomeBlackList(String[] rawBiomeBlackList);

	String[] getRawBiomeWhiteList();

	IWitchDenConfig setRawBiomeWhiteList(String[] rawBiomeWhiteList); 
}
