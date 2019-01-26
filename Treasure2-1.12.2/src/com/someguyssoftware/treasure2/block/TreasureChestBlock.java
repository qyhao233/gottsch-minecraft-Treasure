/**
 * 
 */
package com.someguyssoftware.treasure2.block;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.chest.TreasureChestType;
import com.someguyssoftware.treasure2.client.gui.GuiHandler;
import com.someguyssoftware.treasure2.config.TreasureConfig;
import com.someguyssoftware.treasure2.enums.Rarity;
import com.someguyssoftware.treasure2.printer.ChestNBTPrettyPrinter;
import com.someguyssoftware.treasure2.tileentity.AbstractTreasureChestTileEntity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Jan 9, 2018
 *
 */
public class TreasureChestBlock extends AbstractChestBlock {

	/*
	 * The GUIID;
	 */
	private int chestGuiID = GuiHandler.STANDARD_CHEST_GUIID;
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param te
	 * @param type
	 */
	public TreasureChestBlock(String modID, String name, Class<? extends AbstractTreasureChestTileEntity> te, TreasureChestType type, Rarity rarity) {
		this(modID, name, Material.WOOD, te, type, rarity);
	}

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param material
	 * @param te
	 * @param type
	 */
	public TreasureChestBlock(String modID, String name, Material material, Class<? extends AbstractTreasureChestTileEntity> te, TreasureChestType type, Rarity rarity) {
		super(modID, name, material, te, type, rarity);
	}

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param material
	 */
	private TreasureChestBlock(String modID, String name, Material material) {
		super(modID, name, material);
	}

	/**
	 * Called just after the player places a block.
	 */
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Treasure.logger.debug("Placing chest from item");

		boolean shouldRotate = false;
		boolean shouldUpdate = false;
		boolean forceUpdate = false;
		AbstractTreasureChestTileEntity tcte = null;
		Direction oldPersistedChestDirection = Direction.NORTH;

		// face the block towards the player (there isn't really a front)
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 3);
		TileEntity te = worldIn.getTileEntity(pos);
		if (te != null && te instanceof AbstractTreasureChestTileEntity) {				
			// get the backing tile entity
			tcte = (AbstractTreasureChestTileEntity) te;

			// set the name of the chest
			if (stack.hasDisplayName()) {
				//		        	tcte.setCustomName(stack.getItem().getUnlocalizedName());
				tcte.setCustomName(stack.getDisplayName());
			}

			// read in nbt
//			Treasure.logger.debug("Checking if item has stack compound.");
			if (stack.hasTagCompound()) {
//				Treasure.logger.debug("Stack has compound!");

				tcte.readFromItemStackNBT(stack.getTagCompound());
				forceUpdate = true;

				// get the old tcte facing direction
				oldPersistedChestDirection = Direction.fromFacing(EnumFacing.getFront(tcte.getFacing()));
//				Treasure.logger.debug("old chest facing (int) -> {}", tcte.getFacing());
//				Treasure.logger.debug("old chest facing enum -> {}", EnumFacing.getFront(tcte.getFacing()));
//				Treasure.logger.debug("old chest direction -> {}", oldPersistedChestDirection);
				
				// dump stack NBT
				if (Treasure.logger.isDebugEnabled()) {
					dump(stack.getTagCompound(), new Coords(pos), "STACK ITEM -> CHEST NBT");
				}
			}

			// get the direction the block is facing.
			Direction direction = Direction.fromFacing(placer.getHorizontalFacing().getOpposite());
//			Treasure.logger.debug("new chest direction -> {}", direction);

//			Treasure.logger.debug("required rotation -> {}", oldPersistedChestDirection.getRotation(direction));
//			Treasure.logger.debug("should update prior to rotate -> {}", shouldUpdate);
			// rotate the lock states
			shouldUpdate = rotateLockStates(worldIn, pos, oldPersistedChestDirection.getRotation(direction)); // old -> Direction.NORTH //
			
//			Treasure.logger.debug("should update after rotate -> {}", shouldUpdate);
			
//			Treasure.logger.debug("New lock states ->");
//			for (LockState ls : tcte.getLockStates()) {
//				Treasure.logger.debug(ls);
//			}
			
			// update the TCTE facing
			tcte.setFacing(placer.getHorizontalFacing().getOpposite().getIndex());
//			Treasure.logger.debug("updated tcte facing -> {}", tcte.getFacing());
		}
		if ((forceUpdate || shouldUpdate) && tcte != null) {
			// update the client
			tcte.sendUpdates();
		}

//		if (tcte != null && tcte.getLockStates() != null) {
//			Treasure.logger.debug("Is  TE.lockStates empty? " + tcte.getLockStates().isEmpty());			
//		}
	}

//	/**
//	 * 
//	 * @param world
//	 * @param pos
//	 * @param rotate
//	 * @return
//	 */
//	public boolean  rotateLockStates(World world, BlockPos pos, Rotate rotate) {
//		boolean hasRotated = false;
//		boolean shouldRotate = false;
//		if (rotate != Rotate.NO_ROTATE) shouldRotate = true;
//		Treasure.logger.debug("Rotate to:" + rotate);
//		
//		AbstractTreasureChestTileEntity tcte = null;
//		TileEntity te = world.getTileEntity(pos);
//		if (te != null && te instanceof AbstractTreasureChestTileEntity) {
//			// get the backing tile entity
//			tcte = (AbstractTreasureChestTileEntity) te;
//		}
//		else {
//			return false;
//		}
//		
//		try {
//			for (LockState lockState : tcte.getLockStates()) {
//				if (lockState != null && lockState.getSlot() != null) {
////					Treasure.logger.debug("Original lock state:" + lockState);
//					// if a rotation is needed
//					if (shouldRotate) {
//						ILockSlot newSlot = lockState.getSlot().rotate(rotate);
////						Treasure.logger.debug("New slot position:" + newSlot);
//						lockState.setSlot(newSlot);
//						// set the flag to indicate the lockStates have rotated
//						hasRotated = true;
//					}
//				}
//			}
//		}
//		catch(Exception e) {
//			Treasure.logger.error("Error updating lock states: ", e);
//		}
//		return hasRotated;
//	}

	/**
	 * 
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		AbstractTreasureChestTileEntity te = (AbstractTreasureChestTileEntity) worldIn.getTileEntity(pos);
		
		// TODO dump TE

		// exit if on the client
		if (WorldInfo.isClientSide(worldIn)) {			
			return true;
		}
		else {
			// TODO message
		}
		
		boolean isLocked = false;
		// determine if chest is locked
		if (te.hasLocks()) {
			isLocked = true;
		}
		
		// open the chest
		if (!isLocked) {
			playerIn.openGui(Treasure.instance, getChestGuiID(), worldIn, pos.getX(), pos.getY(),	pos.getZ());
		}

		return true;
	}

	/**
	 * 
	 */
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		AbstractTreasureChestTileEntity te = (AbstractTreasureChestTileEntity) worldIn.getTileEntity(pos);

		Treasure.logger.debug("Breaking block....!");
		if (te != null && te.getInventoryProxy() != null) {
			// unlocked!
			if (!te.hasLocks()) {
//				Treasure.logger.debug("Not locked, dropping all items!");
				
				/*
				 * spawn inventory items
				 */
				InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)te.getInventoryProxy());

				/*
				 * spawn chest item
				 */
				ItemStack chestItem = new ItemStack(Item.getItemFromBlock(this), 1);
				Treasure.logger.debug("Item being created from chest -> {}", chestItem.getItem().getRegistryName());
				InventoryHelper.spawnItemStack(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), chestItem);

				/*
				 *  write the properties to the nbt
				 */
				if (!chestItem.hasTagCompound()) {
					chestItem.setTagCompound(new NBTTagCompound());
				}		
				te.writePropertiesToNBT(chestItem.getTagCompound());
			}
			else {
				Treasure.logger.debug("[BreakingBlock] Chest is locked, save locks and items to NBT");

				/*
				 * spawn chest item
				 */

				if (WorldInfo.isServerSide(worldIn)) {
					ItemStack chestItem = new ItemStack(Item.getItemFromBlock(this), 1);

					// give the chest a tag compound
//					Treasure.logger.debug("[BreakingBlock]Saving chest items:");

					NBTTagCompound nbt = new NBTTagCompound();
					nbt = te.writeToNBT(nbt);
					chestItem.setTagCompound(nbt);
					
					InventoryHelper.spawnItemStack(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), chestItem);
					
					//TEST  log all items in item
//					NonNullList<ItemStack> items = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
//					ItemStackHelper.loadAllItems(chestItem.getTagCompound(), items);
//					for (ItemStack stack : items) {
//						Treasure.logger.debug("[BreakingBlock] item in chest item -> {}", stack.getDisplayName());
//					}
				}
			}

			// remove the tile entity 
			worldIn.removeTileEntity(pos);
		}
	}

	/**
	 * Handled by breakBlock()
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	/**
	 * 
	 * @param tagCompound
	 */
	private void dump(NBTTagCompound tag, ICoords coords, String title) {
		ChestNBTPrettyPrinter printer  =new ChestNBTPrettyPrinter();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
		
		String filename = String.format("chest-nbt-%s-%s.txt", 
				formatter.format(new Date()), 
				coords.toShortString().replaceAll(" ", "-"));

		Path path = Paths.get(TreasureConfig.treasureFolder, "dumps").toAbsolutePath();
		try {
			Files.createDirectories(path);			
		} catch (IOException e) {
			Treasure.logger.error("Couldn't create directories for dump files:", e);
			return;
		}
		String s = printer.print(tag, Paths.get(path.toString(), filename), title);
		Treasure.logger.debug(s);
	}
	
//	/**
//	 * @return the tileEntityClass
//	 */
//	public Class<?> getTileEntityClass() {
//		return tileEntityClass;
//	}
//
//	/**
//	 * @param tileEntityClass the tileEntityClass to set
//	 */
//	public TreasureChestBlock setTileEntityClass(Class<?> tileEntityClass) {
//		this.tileEntityClass = tileEntityClass;
//		return this;
//	}
//
//	/**
//	 * @return the chestType
//	 */
//	public TreasureChestType getChestType() {
//		return chestType;
//	}
//
//	/**
//	 * @param chestType the chestType to set
//	 */
//	public TreasureChestBlock setChestType(TreasureChestType chestType) {
//		this.chestType = chestType;
//		return this;
//	}
//
//	/**
//	 * @return the bounds
//	 */
//	public AxisAlignedBB[] getBounds() {
//		return bounds;
//	}
//
//	/**
//	 * @param bounds the bounds to set
//	 */
//	public TreasureChestBlock setBounds(AxisAlignedBB[] bounds) {
//		this.bounds = bounds;
//		return this;
//	}
//
//	/**
//	 * @return the rarity
//	 */
//	public Rarity getRarity() {
//		return rarity;
//	}
//
//	/**
//	 * @param rarity the rarity to set
//	 */
//	public TreasureChestBlock setRarity(Rarity rarity) {
//		this.rarity = rarity;
//		return this;
//	}

	/**
	 * @return the chestGuiID
	 */
	public int getChestGuiID() {
		return chestGuiID;
	}

	/**
	 * @param chestGuiID the chestGuiID to set
	 */
	public TreasureChestBlock setChestGuiID(int chestGuiID) {
		this.chestGuiID = chestGuiID;
		return this;
	}

//	/**
//	 * @return the tileEntity
//	 */
//	public AbstractTreasureChestTileEntity getTileEntity() {
//		return tileEntity;
//	}
//
//	/**
//	 * @param tileEntity the tileEntity to set
//	 */
//	public void setTileEntity(AbstractTreasureChestTileEntity tileEntity) {
//		this.tileEntity = tileEntity;
//	}

}
