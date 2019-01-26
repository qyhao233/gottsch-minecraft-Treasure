package com.someguyssoftware.treasure2.world.gen.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.enums.StructureMarkers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.gen.structure.template.Template;

import static com.someguyssoftware.treasure2.enums.StructureMarkers.*;


/**
 * Custom template manager that create TreasureTemplates instead of Template.
 * @author Mark Gottschling on Jan 19, 2019
 *
 */
public class TreasureTemplateManager {
	// TODO will need a better mapped system - something based on underground/above ground, rarity?
	private final Map<String, Template> templates = Maps.<String, Template>newHashMap();
	/** the folder in the assets folder where the structure templates are found. */
	private final String baseFolder;
	private final DataFixer fixer;
	
	/*
	 * builtin locations for structures
	 */
	List<String> locations = Arrays.asList(new String [] {
			"treasure2:underground/basic1",
			"treasure2:underground/basic2",
			"treasure2:underground/basic3",
			"treasure2:underground/basic4",
			"treasure2:underground/cave1",
			"treasure2:underground/cobb1",
			"treasure2:underground/crypt1"
	});

	/*
	 * standard list of blocks to scan for 
	 */
	private List<Block> scanList;
	
	/*
	 * 
	 */
	private Map<StructureMarkers, Block> markerMap;

	/**
	 * 
	 * @param baseFolder
	 * @param fixer
	 */
	public TreasureTemplateManager(String baseFolder, DataFixer fixer) {
		Treasure.logger.debug("creating a TreasureTemplateManager");
        this.baseFolder = baseFolder;
        this.fixer = fixer;        
        
        // setup standard list of markers
        markerMap = Maps.newHashMapWithExpectedSize(10);
        markerMap.put(CHEST, Blocks.CHEST);
        markerMap.put(StructureMarkers.BOSS_CHEST, Blocks.ENDER_CHEST);
        markerMap.put(StructureMarkers.SPAWNER, Blocks.MOB_SPAWNER);
        markerMap.put(StructureMarkers.ENTRANCE, Blocks.GOLD_BLOCK);
        markerMap.put(StructureMarkers.OFFSET, Blocks.REDSTONE_BLOCK);
        markerMap.put(StructureMarkers.PROXIMITY_SPAWNER, Blocks.IRON_BLOCK);
        markerMap.put(StructureMarkers.NULL, Blocks.BEDROCK);
        		
        // default scan list
        scanList = Arrays.asList(new Block[] {
    			markerMap.get(CHEST), 
    			markerMap.get(SPAWNER),
    			markerMap.get(ENTRANCE),
    			markerMap.get(OFFSET),
    			markerMap.get(PROXIMITY_SPAWNER)
    			});
        // load all the structure templates
        loadAll();
    }

	/**
	 * 
	 * @param server
	 * @param id
	 * @return
	 */
	public Template getTemplate(/*@Nullable MinecraftServer server, */ResourceLocation r) {
		String s = r.getResourcePath();

		Template template = null;
		if (this.templates.containsKey(s)) {
			return this.templates.get(s);
		}
		
//		Template template = this.get(/*server,*/id);
//
//		if (template == null) {
//			template = new Template();
//			this.templates.put(id.getResourcePath(), template);
//		}

		return template;
	}

	/**
	 * 
	 */
	public void loadAll() {
		Treasure.logger.debug("loading all structures...");
		for (String s : locations) {
			Treasure.logger.debug("loading from -> {}", s);
			load(new ResourceLocation(s), scanList);
		}
	}
	
	/**
	 * 
	 * @param server
	 * @param templatePath
	 * @return
	 */
	public Template load(/*@Nullable MinecraftServer server, */ResourceLocation templatePath, List<Block> scanForBlocks) {
		String s = templatePath.getResourcePath();
//
//		if (this.templates.containsKey(s)) {
//			return this.templates.get(s);
//		} else {
//			if (server == null) {
//				this.readTemplateFromJar(templatePath);
//			} else {
		this.readTemplate(templatePath, scanForBlocks);
		if (this.templates.get(s) != null) {
			Treasure.logger.debug("Loaded structure from -> {}", templatePath.toString());
		}
		else {
			Treasure.logger.debug("Unable to read structure from -> {}", templatePath.toString());
		}
		
//			}

//			return this.templates.containsKey(s) ? (Template) this.templates.get(s) : null;
		return this.templates.get(s);
//		}
	}

	/**
	 * This reads a structure template from the given location and stores it. This
	 * first attempts get the template from an external folder. If it isn't there
	 * then it attempts to take it from the minecraft jar.
	 */
	public boolean readTemplate(ResourceLocation location, List<Block> scanForBlocks) {
		String s = location.getResourcePath();
		File file1 = new File(this.baseFolder, s + ".nbt");

		if (!file1.exists()) {
			Treasure.logger.debug("file does not exist, read from jar -> {}", file1.getAbsolutePath());
			return this.readTemplateFromJar(location, scanForBlocks);
		} else {
			InputStream inputstream = null;
			boolean flag;

			try {
				inputstream = new FileInputStream(file1);
				this.readTemplateFromStream(s, inputstream, scanForBlocks);
				return true;
			} catch (Throwable var10) {
				flag = false;
			} finally {
				IOUtils.closeQuietly(inputstream);
			}

			return flag;
		}
	}

	/**
	 * reads a template from the minecraft jar
	 */
	private boolean readTemplateFromJar(ResourceLocation id, List<Block> scanForBlocks) {
		String s = id.getResourceDomain();
		String s1 = id.getResourcePath();
		InputStream inputstream = null;
		boolean flag;

		try {
			Treasure.logger.debug("attempting to open resource stream -> {}", "/assets/" + s + "/strucutres/" + s1 + ".nbt");
			inputstream = MinecraftServer.class.getResourceAsStream("/assets/" + s + "/structures/" + s1 + ".nbt");
			this.readTemplateFromStream(s1, inputstream, scanForBlocks);
			return true;
		} catch (Throwable var10) {
			Treasure.logger.error("error reading resource: ", var10);
			flag = false;
		} finally {
			IOUtils.closeQuietly(inputstream);
		}

		return flag;
	}

	/**
	 * reads a template from an inputstream
	 */
	private void readTemplateFromStream(String id, InputStream stream, List<Block> scanForBlocks) throws IOException {
		NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);

		if (!nbttagcompound.hasKey("DataVersion", 99)) {
			nbttagcompound.setInteger("DataVersion", 500);
		}

		TreasureTemplate template = new TreasureTemplate();
		template.read(this.fixer.process(FixTypes.STRUCTURE, nbttagcompound), scanForBlocks);
		Treasure.logger.debug("adding template to map with key -> {}", id);
		this.templates.put(id, template);
	}

	/**
	 * writes the template to an external folder
	 */
	public boolean writeTemplate(@Nullable MinecraftServer server, ResourceLocation id) {
		String s = id.getResourcePath();

		if (server != null && this.templates.containsKey(s)) {
			File file1 = new File(this.baseFolder);

			if (!file1.exists()) {
				if (!file1.mkdirs()) {
					return false;
				}
			} else if (!file1.isDirectory()) {
				return false;
			}

			File file2 = new File(file1, s + ".nbt");
			Template template = this.templates.get(s);
			OutputStream outputstream = null;
			boolean flag;

			try {
				NBTTagCompound nbttagcompound = template.writeToNBT(new NBTTagCompound());
				outputstream = new FileOutputStream(file2);
				CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
				return true;
			} catch (Throwable var13) {
				flag = false;
			} finally {
				IOUtils.closeQuietly(outputstream);
			}

			return flag;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param templatePath
	 */
	public void remove(ResourceLocation templatePath) {
		this.templates.remove(templatePath.getResourcePath());
	}

	public Map<StructureMarkers, Block> getMarkerMap() {
		return markerMap;
	}

	public void setMarkerMap(Map<StructureMarkers, Block> markerMap) {
		this.markerMap = markerMap;
	}

	public List<Block> getScanList() {
		return scanList;
	}

	public void setScanList(List<Block> scanList) {
		this.scanList = scanList;
	}

	public Map<String, Template> getTemplates() {
		return templates;
	}
}
