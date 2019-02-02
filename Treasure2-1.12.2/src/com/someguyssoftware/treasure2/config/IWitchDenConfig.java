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
public interface IWitchCloisterConfig extends IBiomeListConfig {
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
	
	public boolean isCloisterAllowed();
	public int getChunksPerCloister();
	public double getGenProbability();

	IWitchCloisterConfig setCloisterAllowed(boolean wellAllowed);

	IWitchCloisterConfig setChunksPerCloister(int chunksPerWell);

	IWitchCloisterConfig setGenProbability(double genProbability);

	String[] getRawBiomeBlackList();

	IWitchCloisterConfig setRawBiomeBlackList(String[] rawBiomeBlackList);

	String[] getRawBiomeWhiteList();

	IWitchCloisterConfig setRawBiomeWhiteList(String[] rawBiomeWhiteList); 
}
