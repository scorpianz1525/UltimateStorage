/**
 * TileStorageBlock
 *
 * @author Garrett Spicer-Davis
 */
package tterrag.ultimateStorage.tile;

import tterrag.ultimateStorage.client.GuiStorageBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * @author Garrett Spicer-Davis
 * 
 */
public class TileStorageBlock extends TileEntity implements ISidedInventory
{
	public ItemStack[] inventory = new ItemStack[3];
	public long stored;
		public final long max = 1000000000000L;
	private ItemStack storedItems;

	public TileStorageBlock()
	{
		stored = 0;
	}

	@Override
	public void updateEntity()
	{
		if (stored == max)
			return;
		
		if (inventory[1] != null)
		{
			if (stacksEqual(inventory[1], storedItems))
			{
				if (inventory[1].stackSize + storedItems.stackSize <= 64)
				{
					storedItems.stackSize = inventory[1].stackSize + storedItems.stackSize;
					inventory[1] = null;
					stored = storedItems.stackSize;
				}
				else if (storedItems.stackSize < 64)
				{
					int add = 64 - storedItems.stackSize;
					storedItems.stackSize = 64;
					inventory[1].stackSize -= add;
					stored = storedItems.stackSize;
				}
				else
				{
					add(inventory[1].stackSize);
					inventory[1] = null;
				}
			}
			else if (storedItems == null && inventory[1] != null)
			{
				storedItems = inventory[1];
				inventory[1] = null;
				add(storedItems.stackSize);
			}
		}
	}
	
	/**
	 * Adds the amount to the current storage
	 * @param amnt
	 * @return the amount left over, if any
	 */
	private int add(int amnt)
	{
		if ((stored + amnt) > max)
		{
			stored = max;
			return (int) (0 - (max - (amnt + stored)));
		}
		else
		{
			stored += amnt;
		}
		
		return 0;
	}

	@Override
	public int getSizeInventory()
	{
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int var1)
	{
		return inventory[var1];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (this.inventory[i] != null)
		{
			ItemStack itemstack;
			if (this.inventory[i].stackSize <= j)
			{
				itemstack = this.inventory[i];
				this.inventory[i] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.inventory[i].splitStack(j);
				if (this.inventory[i].stackSize == 0)
				{
					this.inventory[i] = null;
				}
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		if (this.inventory[i] != null)
		{
			ItemStack itemstack = this.inventory[i];
			this.inventory[i] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		this.inventory[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
		{
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName()
	{
		return "tterrag.inv.storageBlock";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return true;
	}

	@Override
	public void openInventory()
	{}

	@Override
	public void closeInventory()
	{}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1)
	{
		return new int[]{0, 1};
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3)
	{
		return true;
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3)
	{
		if(var1 == 2){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		if(storedItems != null){
			if (i == 1 & (itemstack.getItem().equals(storedItems.getItem()))){
				return true;
			}
		}
		if(i == 0 & (FluidContainerRegistry.isContainer(itemstack) == true)){
			return true;
		}
		return false;
	}

	/**
	 * @author powercrystals
	 */
	public static boolean stacksEqual(ItemStack s1, ItemStack s2)
	{
		if (s1 == null || s2 == null) return false;
		if (!s1.isItemEqual(s2)) return false;
		if (s1.getTagCompound() == null && s2.getTagCompound() == null) return true;
		if (s1.getTagCompound() == null || s2.getTagCompound() == null) return false;
		return s1.getTagCompound().equals(s2.getTagCompound());
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		ItemStack stack = storedItems;
		stack.writeToNBT(nbt);
		nbt.setLong("stored", stored);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		storedItems = ItemStack.loadItemStackFromNBT(nbt);
		stored = nbt.getLong("stored");
		System.out.println("Read");
	}
}
