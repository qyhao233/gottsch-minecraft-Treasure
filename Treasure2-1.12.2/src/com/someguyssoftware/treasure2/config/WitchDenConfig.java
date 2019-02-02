/**
 * 
 */
package com.someguyssoftware.treasure2.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.someguyssoftware.gottschcore.biome.BiomeHelper;
import com.someguyssoftware.gottschcore.biome.BiomeTypeHolder;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.treasure2.Treasure;

import net.minecraftforge.common.config.Configuration;

/**
 * @author Mark Gottschling on Jan 29, 2019
 *
 */
public class WitchDenConfig implements IWitchDenConfig {
//	private IMod mod;
//	private Configuration forgeConfiguration;
	
	private boolean denAllowed;
	private int chunksPerDen;
	private double genProbability;
	
	// biome type white/black lists
	private  String[] rawBiomeWhiteList;
	private  String[] rawBiomeBlackList;
	
	private List<BiomeTypeHolder> biomeWhiteList;
	private List<BiomeTypeHolder> biomeBlackList;
	
	/**
	 * 
	 */
	public WitchDenConfig() {
		biomeWhiteList = new ArrayList<>(5);
		biomeBlackList = new ArrayList<>(5);
	}
	
	/**
	 * @param defaultConfig 
	 * @param string 
	 * @param treasureConfigDir 
	 * @param configDir 
	 * @param mod 
	 * 
	 */
	public WitchDenConfig(IMod mod, File configDir, String modDir, String filename, IWitchDenConfig defaults) {
		this();
//		this.mod = mod;
		// build the path to the minecraft config directory
		String configPath = (new StringBuilder()).append(configDir).append("/").append(modDir).append("/").toString();
		// create the config file
		File configFile = new File((new StringBuilder()).append(configPath).append(filename).toString());
		// load the config file
		Configuration configuration = load(configFile, defaults);
//		this.forgeConfiguration = configuration;
	}

	/**
	 * 
	 * @param file
	 * @param defaults
	 * @return
	 */
	public Configuration load(File file, IWitchDenConfig defaults) {
		// load the config file
		Configuration config = IWitchDenConfig.super.load(file);
	
		config.setCategoryComment("01-enable", "Enablements.");
        denAllowed = config.getBoolean("denAllowed", "01-enable", defaults.isDenAllowed(), "");
 
        // gen props
    	chunksPerDen = config.getInt("chunksPerDen", "02-gen", defaults.getChunksPerDen(), 200, 32000, "");
    	genProbability = config.getFloat("genProbability", "02-gen", (float)defaults.getGenProbability(), 0.0F, 100.0F, "");
    	    	
        // white/black lists
		Treasure.logger.debug("config: {}", config);
		Treasure.logger.debug("defaults: {}", defaults);
        rawBiomeWhiteList = config.getStringList("biomeWhiteList", "02-gen", (String[]) defaults.getRawBiomeWhiteList(), "Allowable Biome Types for Wither Tree generation. Must match the Type identifer(s).");
        rawBiomeBlackList = config.getStringList("biomeBlackList", "02-gen", (String[]) defaults.getRawBiomeBlackList(), "Disallowable Biome Types for Wither Tree generation. Must match the Type identifer(s).");
              
        // update the config if it has changed.
       if(config.hasChanged()) {
    	   config.save();
       }

		BiomeHelper.loadBiomeList(rawBiomeWhiteList, biomeWhiteList);
		BiomeHelper.loadBiomeList(rawBiomeBlackList, biomeBlackList);
       
		return config;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IBiomeListConfig#getBiomeWhiteList()
	 */
	@Override
	public List<BiomeTypeHolder> getBiomeWhiteList() {
		return biomeWhiteList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IBiomeListConfig#setBiomeWhiteList(java.util.List)
	 */
	@Override
	public void setBiomeWhiteList(List<BiomeTypeHolder> biomeWhiteList) {
		this.biomeWhiteList = biomeWhiteList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IBiomeListConfig#getBiomeBlackList()
	 */
	@Override
	public List<BiomeTypeHolder> getBiomeBlackList() {
		return biomeBlackList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IBiomeListConfig#setBiomeBlackList(java.util.List)
	 */
	@Override
	public void setBiomeBlackList(List<BiomeTypeHolder> biomeBlackList) {
		this.biomeBlackList = biomeBlackList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#getRawBiomeBlackList()
	 */
	@Override
	public String[] getRawBiomeBlackList() {
		return this.rawBiomeBlackList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#setRawBiomeBlackList(java.lang.String[])
	 */
	@Override
	public IWitchDenConfig setRawBiomeBlackList(String[] rawBiomeBlackList) {
		this.rawBiomeBlackList = rawBiomeBlackList;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#getRawBiomeWhiteList()
	 */
	@Override
	public String[] getRawBiomeWhiteList() {
		return rawBiomeWhiteList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#setRawBiomeWhiteList(java.lang.String[])
	 */
	@Override
	public IWitchDenConfig setRawBiomeWhiteList(String[] rawBiomeWhiteList) {
		this.rawBiomeWhiteList = rawBiomeWhiteList;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#isDenAllowed()
	 */
	@Override
	public boolean isDenAllowed() {
		return denAllowed;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#getChunksPerDen()
	 */
	@Override
	public int getChunksPerDen() {
		return chunksPerDen;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#getGenProbability()
	 */
	@Override
	public double getGenProbability() {
		return genProbability;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#setDenAllowed(boolean)
	 */
	@Override
	public IWitchDenConfig setDenAllowed(boolean isAllowed) {
		this.denAllowed = isAllowed;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#setChunksPerDen(int)
	 */
	@Override
	public IWitchDenConfig setChunksPerDen(int chunksPerDen) {
		this.chunksPerDen = chunksPerDen;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchDenConfig#setGenProbability(double)
	 */
	@Override
	public IWitchDenConfig setGenProbability(double genProbability) {
		this.genProbability = genProbability; 
		return this;
	}
}
