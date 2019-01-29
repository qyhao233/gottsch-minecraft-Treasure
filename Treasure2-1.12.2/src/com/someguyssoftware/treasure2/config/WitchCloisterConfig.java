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
import net.minecraftforge.fml.common.Mod;

/**
 * @author Mark Gottschling on Jan 29, 2019
 *
 */
public class WitchCloisterConfig implements IWitchCloisterConfig {
	private IMod mod;
	private Configuration forgeConfiguration;
	
	private boolean cloisterAllowed;
	private int chunksPerCloister;
	private double genProbability;
	
	// biome type white/black lists
	private  String[] rawBiomeWhiteList;
	private  String[] rawBiomeBlackList;
	
	private List<BiomeTypeHolder> biomeWhiteList;
	private List<BiomeTypeHolder> biomeBlackList;
	
	/**
	 * 
	 */
	public WitchCloisterConfig() {
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
	public WitchCloisterConfig(IMod mod, File configDir, String modDir, String filename, IWitchCloisterConfig defaults) {
		this();
		this.mod = mod;
		// build the path to the minecraft config directory
		String configPath = (new StringBuilder()).append(configDir).append("/").append(modDir).append("/").toString();
		// create the config file
		File configFile = new File((new StringBuilder()).append(configPath).append(filename).toString());
		// load the config file
		Configuration configuration = load(configFile, defaults);
		this.forgeConfiguration = configuration;
	}

	/**
	 * 
	 * @param file
	 * @param defaults
	 * @return
	 */
	public Configuration load(File file, IWitchCloisterConfig defaults) {
		// load the config file
		Configuration config = IWitchCloisterConfig.super.load(file);
		// ge the modid
		String modid = mod.getClass().getAnnotation(Mod.class).modid();
		
		config.setCategoryComment("01-enable", "Enablements.");
        cloisterAllowed = config.getBoolean("cloisterAllowed", "01-enable", defaults.isCloisterAllowed(), "");
 
        // gen props
    	chunksPerCloister = config.getInt("chunksPerCloister", "02-gen", defaults.getChunksPerCloister(), 200, 32000, "");
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
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#getRawBiomeBlackList()
	 */
	@Override
	public String[] getRawBiomeBlackList() {
		return this.rawBiomeBlackList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#setRawBiomeBlackList(java.lang.String[])
	 */
	@Override
	public IWitchCloisterConfig setRawBiomeBlackList(String[] rawBiomeBlackList) {
		this.rawBiomeBlackList = rawBiomeBlackList;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#getRawBiomeWhiteList()
	 */
	@Override
	public String[] getRawBiomeWhiteList() {
		return rawBiomeWhiteList;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#setRawBiomeWhiteList(java.lang.String[])
	 */
	@Override
	public IWitchCloisterConfig setRawBiomeWhiteList(String[] rawBiomeWhiteList) {
		this.rawBiomeWhiteList = rawBiomeWhiteList;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#isCloisterAllowed()
	 */
	@Override
	public boolean isCloisterAllowed() {
		return cloisterAllowed;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#getChunksPerCloister()
	 */
	@Override
	public int getChunksPerCloister() {
		return chunksPerCloister;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#getGenProbability()
	 */
	@Override
	public double getGenProbability() {
		return genProbability;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#setCloisterAllowed(boolean)
	 */
	@Override
	public IWitchCloisterConfig setCloisterAllowed(boolean isAllowed) {
		this.cloisterAllowed = isAllowed;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#setChunksPerCloister(int)
	 */
	@Override
	public IWitchCloisterConfig setChunksPerCloister(int chunksPerCloister) {
		this.chunksPerCloister = chunksPerCloister;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.treasure2.config.IWitchCloisterConfig#setGenProbability(double)
	 */
	@Override
	public IWitchCloisterConfig setGenProbability(double genProbability) {
		this.genProbability = genProbability; 
		return this;
	}
}
