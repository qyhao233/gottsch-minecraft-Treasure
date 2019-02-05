/**
 * 
 */
package com.someguyssoftware.treasure2.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.config.Configs;
import com.someguyssoftware.treasure2.config.IWitchDenConfig;
import com.someguyssoftware.treasure2.enums.Pits;
import com.someguyssoftware.treasure2.generator.pit.IPitGenerator;
import com.someguyssoftware.treasure2.generator.pit.StructurePitGenerator;
import com.someguyssoftware.treasure2.generator.special.WitchDenGenerator;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfoProvider;
import com.someguyssoftware.treasure2.worldgen.ChestWorldGenerator;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Mar 8, 2018
 *
 */
public class SpawnWitchDenOnlyCommand extends CommandBase {

	@Override
	public String getName() {
		return "trdenonly";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "/trdenonlt <x> <y> <z>: spawns a named Treasure! witches den at location (x,y,z)";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) commandSender.getCommandSenderEntity();
		try {

			int x, y, z = 0;
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);

			if (player != null) {
    			World world = commandSender.getEntityWorld();
    			Treasure.logger.debug("Starting to build Treasure! witches den only ...");

    			// get the config
    			IWitchDenConfig denConfig = Configs.witchDenConfig;
    			if (denConfig == null) {
    				Treasure.logger.warn("Unable to locate a config for wither tree {}.", denConfig);
    				return;
    			}
    			
    			Random random = new Random();
    			ICoords spawnCoords = new Coords(x, y, z);
    			ICoords surfaceCoords = WorldInfo.getDryLandSurfaceCoords(world, new Coords(x, WorldInfo.getHeightValue(world, spawnCoords), z));
    			Treasure.logger.debug("spawn coords @ {}", spawnCoords.toShortString());
    			Treasure.logger.debug("surfaceCoords @ {}", surfaceCoords.toShortString());
    			WitchDenGenerator gen = new WitchDenGenerator();
  			
    			boolean isGen = gen.generate(world, random, surfaceCoords, denConfig);
    		}
		}
		catch(Exception e) {
			player.sendMessage(new TextComponentString("Error:  " + e.getMessage()));
			Treasure.logger.error("Error generating Treasure! pit:", e);
			e.printStackTrace();
		}
	}
}
