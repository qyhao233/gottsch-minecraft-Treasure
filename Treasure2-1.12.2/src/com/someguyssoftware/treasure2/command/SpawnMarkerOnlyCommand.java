/**
 * 
 */
package com.someguyssoftware.treasure2.command;

import java.util.Random;

import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.generator.marker.GravestoneMarkerGenerator;
import com.someguyssoftware.treasure2.generator.marker.IMarkerGenerator;
import com.someguyssoftware.treasure2.generator.marker.RandomStructureMarkerGenerator;
import com.someguyssoftware.treasure2.world.gen.structure.IStructureInfoProvider;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Jan 29, 2019
 *
 */
public class SpawnMarkerOnlyCommand extends CommandBase {

	@Override
	public String getName() {
		return "trmarkeronly";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "/trmarkeronly <x> <y> <z> [marker]: spawns a named Treasure! marker at location (x,y,z)";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) {
		EntityPlayer player = (EntityPlayer) commandSender.getCommandSenderEntity();
		try {

			int x, y, z = 0;
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);
			
			String markerName = "";
			if (args.length > 3) {
				markerName = args[3];
			}
			
			if (markerName.equals("")) markerName = "RANDOM_STRUCT";
			Treasure.logger.debug("Marker: " + markerName);
			
			if (player != null) {
    			World world = commandSender.getEntityWorld();
    			Treasure.logger.debug("Starting to build Treasure! marker only ...");

    			Random random = new Random();
    			ICoords spawnCoords = new Coords(x, y, z);
    			ICoords surfaceCoords = WorldInfo.getDryLandSurfaceCoords(world, new Coords(x, WorldInfo.getHeightValue(world, spawnCoords), z));
    			Treasure.logger.debug("spawn coords @ {}", spawnCoords.toShortString());
    			Treasure.logger.debug("surfaceCoords @ {}", surfaceCoords.toShortString());

    			IMarkerGenerator markerGen =  null;
    			if (markerName.toUpperCase().equals("RANDOM_STRUCT")) {
    				// select a pit from the subset
    				markerGen = new RandomStructureMarkerGenerator();
    				// create a new pit instance (new instance as it contains state)
    			}
    			else {
    				markerGen = new GravestoneMarkerGenerator();
    			}    			
    			boolean isGen = markerGen.generate(world, random, spawnCoords);
    			
    			if (markerGen instanceof IStructureInfoProvider) {
    				// TODO add fog?
    			}
    		}
		}
		catch(Exception e) {
			player.sendMessage(new TextComponentString("Error:  " + e.getMessage()));
			Treasure.logger.error("Error generating Treasure! marker:", e);
			e.printStackTrace();
		}
	}
}
