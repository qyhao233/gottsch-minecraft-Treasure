package com.someguyssoftware.treasure2.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

/**
 * This is the base/standard container for chests that is similar configuration to that of a vanilla container.
 * @author Mark Gottschling on Jan 16, 2018
 *
 */
public class StandardChestContainer extends AbstractChestContainer {
	/**
	 * 
	 * @param invPlayer
	 * @param inventory
	 */
	public StandardChestContainer(InventoryPlayer invPlayer, IInventory inventory) {
		super(invPlayer, inventory);

		// build the container
		buildContainer(invPlayer, inventory);
	}
}
