package javapower.storagetech.tileentity;

import java.util.UUID;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.API;

import javapower.storagetech.core.Config;
import javapower.storagetech.eventio.IEventVoid;
import javapower.storagetech.item.STItems;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.EnergyBuffer;
import javapower.storagetech.util.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityDiskWorkbench extends TileEntitySynchronized implements IInventory
{
	public long memory = 0l;
	boolean update = true;
	public NonNullList<ItemStack> block_inv_content = NonNullList.<ItemStack>withSize(7, ItemStack.EMPTY);
	
	public EnergyBuffer energyBuffer = new EnergyBuffer(1000000, 1000000, 0);
	
	public boolean prosses = false;
	public int time = 0;
	public int createProsses = 0;
	public int diskSize = 0;
	
	public TileEntityDiskWorkbench()
	{
		energyBuffer.eventchange = new IEventVoid()
		{
			@Override
			public void event()
			{
				update = true;
				markDirty();
			}
		};
	}
	
	public void update_prosses()
	{
		if(prosses)
		{
			if(createProsses >= diskSize || !Config.EnableCostDisk)
			{
					ItemStack itemstack_diskcustom = new ItemStack(STItems.item_diskcustom);
					itemstack_diskcustom.getItem().onCreated(itemstack_diskcustom, world, null);
					NBTTagCompound nbtitemdisk = itemstack_diskcustom.getTagCompound();
					
					if(nbtitemdisk == null)
						nbtitemdisk = new NBTTagCompound();
					
					nbtitemdisk.setInteger("st_cap", diskSize);
					
					UUID id = UUID.randomUUID();
					API.instance().getStorageDiskManager(world).set(id, API.instance().createDefaultItemDisk(world, diskSize));
	                API.instance().getStorageDiskManager(world).markForSaving();
	                
	                nbtitemdisk.setUniqueId("Id", id);
	                itemstack_diskcustom.setTagCompound(nbtitemdisk);
	                
					block_inv_content.set(2, itemstack_diskcustom);
					
					prosses = false;
					update = true;
					markDirty();
			}
			else
			{
				if(time > Config.TimeCostPerSize)
				{
					if(energyBuffer.energy >= Config.EnergyCostPerSize+countUpgrade())
					{
						time = 0;
						createProsses += Config.ProssesAdvancementSizeFluid*countUpgrade();
						energyBuffer.energy -= Config.EnergyCostPerSize+countUpgrade();
						
						update = true;
						markDirty();
					}
				}
				else
				{
					//System.out.println(countUpgrade());
					++time;
				}
			}
		}
	}
	
	@Override
	public void reciveDataFromClient(NBTTagCompound nbt, EntityPlayer player)
	{
		if(nbt.hasKey("up"))
			update = true;
		
		if(nbt.hasKey("startCreateDisk"))
		{
			int disksize = nbt.getInteger("startCreateDisk");
			ItemStack itemin = block_inv_content.get(1);
			ItemStack itemout = block_inv_content.get(2);
			
			if(itemout.isEmpty() && !itemin.isEmpty() && itemin.getCount() > 0 && disksize > 0 && disksize <= memory)
			{
				if(!prosses)
				{
					time = 0;
					createProsses = 0;
					diskSize = disksize;
					prosses = true;
					memory -= disksize;
					update = true;
					markDirty();
					if(itemin.getCount() == 1)
					{
						block_inv_content.set(1, ItemStack.EMPTY);
					}
					else
					{
						itemin.setCount(itemin.getCount()-1);
					}
				}
			}
		}
		
		/*if(nbt.hasKey("createDisk"))
		{
			int disksize = nbt.getInteger("createDisk");
			ItemStack itemin = block_inv_content.get(1);
			if(block_inv_content.get(2).isEmpty() && !itemin.isEmpty() && itemin.getCount() > 0 && disksize <= memory)
			{
				ItemStack itemstack_diskcustom = new ItemStack(STItems.item_diskcustom);
				itemstack_diskcustom.getItem().onCreated(itemstack_diskcustom, world, player);
				NBTTagCompound nbtitemdisk = itemstack_diskcustom.getTagCompound();
				
				if(nbtitemdisk == null)
					nbtitemdisk = new NBTTagCompound();
				
				nbtitemdisk.setInteger("st_cap", disksize);
				//itemstack_diskcustom.setTagCompound(nbtitemdisk);
				
				UUID id = UUID.randomUUID();
				API.instance().getStorageDiskManager(world).set(id, API.instance().createDefaultItemDisk(world, disksize));
                API.instance().getStorageDiskManager(world).markForSaving();
                
                nbtitemdisk.setUniqueId("Id", id);
                itemstack_diskcustom.setTagCompound(nbtitemdisk);
                
				block_inv_content.set(2, itemstack_diskcustom);
				if(itemin.getCount() == 1)
				{
					block_inv_content.set(1, ItemStack.EMPTY);
				}
				else
				{
					itemin.setCount(itemin.getCount()-1);
				}
				
				memory -= disksize;
				update = true;
				markDirty();
			}
		}*/
	}

	@Override
	public void onPlayerOpenGUISendData(NBTTagCompound nbt, EntityPlayer player)
	{
		nbt.setLong("memory", memory);
		nbt.setInteger("max", Math.min(Config.DiskMaxSize, Tools.limiteLTI(memory)));
		
		nbt.setBoolean("prosses", prosses);
		nbt.setFloat("prossestime", (createProsses/(float)diskSize));
		
		nbt.setInteger("energy", energyBuffer.energy);
		nbt.setInteger("capacity", energyBuffer.capacity);
		nbt.setBoolean("encd", Config.EnableCostDisk);
	}

	@Override
	public NBTTagCompound updateData()
	{
		if(update)
		{
			update = false;
			NBTTagCompound nbt_update = new NBTTagCompound();
			nbt_update.setLong("memory", memory);
			nbt_update.setInteger("max", Math.min(Config.DiskMaxSize, Tools.limiteLTI(memory)));
			
			nbt_update.setBoolean("prosses", prosses);
			nbt_update.setFloat("prossestime", (createProsses/(float)diskSize));
			
			nbt_update.setInteger("energy", energyBuffer.energy);
			nbt_update.setInteger("capacity", energyBuffer.capacity);
			nbt_update.setBoolean("encd", Config.EnableCostDisk);
			return nbt_update;
		}
		return null;
	}
	
	@Override
	public void read(NBTTagCompound tag)
	{
		if(tag.hasKey("memory"))
			memory = tag.getLong("memory");
		
		if(tag.hasKey("inv"))
			ItemStackHelper.loadAllItems(tag.getCompoundTag("inv"), block_inv_content);
		
		energyBuffer.ReadFromNBT(tag);
		
		if(tag.hasKey("prosses"))
			prosses = tag.getBoolean("prosses");
		
		if(tag.hasKey("time"))
			time = tag.getInteger("time");
		
		if(tag.hasKey("createprosses"))
			createProsses = tag.getInteger("createprosses");
		
		if(tag.hasKey("disksize"))
			diskSize = tag.getInteger("disksize");
		
		markDirty();
	}
	
	@Override
	public NBTTagCompound write(NBTTagCompound tag)
	{
		tag.setLong("memory", memory);
		
		NBTTagCompound nbt_inv = new NBTTagCompound();
		ItemStackHelper.saveAllItems(nbt_inv, block_inv_content);
		tag.setTag("inv", nbt_inv);
		
		energyBuffer.WriteToNBT(tag);
		
		tag.setBoolean("prosses", prosses);
		tag.setInteger("time", time);
		tag.setInteger("createprosses", createProsses);
		tag.setInteger("disksize", diskSize);
		
		return tag;
	}
	
	@Override
	public void update()
	{
		ItemStack i = block_inv_content.get(0);
		if(!i.isEmpty())
		{
			if(DiskUtils.validItemDisk(i))
			{
				long memadd = DiskUtils.getMemoryFromItemDisk(i);
				memory += memadd;
				update = true;
				block_inv_content.set(0, ItemStack.EMPTY);
				markDirty();
			}
			/*if(i.getItem().equals(RSItems.STORAGE_PART))
			{
				int dam = i.getItemDamage();
				int quant = i.getCount();
				long memadd = (long) (Math.pow(2, 2*dam)*quant*1000);
				if(memadd + memory > memory)
				{
					memory += memadd;
					update = true;
					block_inv_content.set(0, ItemStack.EMPTY);
					markDirty();
				}
			}
			else if(i.getItem().equals(STItems.item_memory) && i.getItemDamage() == 0 && i.getTagCompound() != null)
			{
				memory += i.getTagCompound().getLong("memory");
				update = true;
				block_inv_content.set(0, ItemStack.EMPTY);
				markDirty();
			}*/
		}
		
		update_prosses();
		super.update();
	}
	
	public int countUpgrade()
	{
		int u = 1;
		if(!block_inv_content.get(3).isEmpty())
			u *= 4;
		if(!block_inv_content.get(4).isEmpty())
			u *= 4;
		if(!block_inv_content.get(5).isEmpty())
			u *= 4;
		if(!block_inv_content.get(6).isEmpty())
			u *= 4;
		
		return u;
	}

	@Override
	public String getName()
	{
		return "diskWB";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		return block_inv_content.size();
	}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack is: block_inv_content)
			if(!is.isEmpty())
				return false;
		
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		if(index < block_inv_content.size())
			return block_inv_content.get(index);
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		 return ItemStackHelper.getAndSplit(block_inv_content, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(block_inv_content, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		block_inv_content.set(index, stack);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		if(index >= 2 && index <= 6)
			return false;
		else if(index == 0)
		{
			return DiskUtils.validItemDisk(stack);
			//return stack.getItem().equals(RSItems.STORAGE_PART) || (stack.getItem().equals(STItems.item_memory) && stack.getItemDamage() == 0);
		}
		else
		{
			return stack.isItemEqualIgnoreDurability(new ItemStack(RSItems.STORAGE_HOUSING));
		}
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		block_inv_content.clear();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityEnergy.ENERGY ||
				super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
		{
			return (T) energyBuffer;
		}
		return super.getCapability(capability, facing);
	}

}
