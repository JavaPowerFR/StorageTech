package javapower.storagetech.tileentity;

import java.util.UUID;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.API;

import javapower.storagetech.container.ContainerDiskWorkbench;
import javapower.storagetech.core.CommonConfig;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.eventio.IEventVoid;
import javapower.storagetech.item.STItems;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.EnergyBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityDiskWorkbench extends TileEntityBase implements IInventory, ITickableTileEntity, INamedContainerProvider, ICreateDisk
{
	@ObjectHolder(StorageTech.MODID+":diskworkbench")
	public static final TileEntityType<TileEntityDiskWorkbench> CURRENT_TILE = null;
	
	public long memory = 0l;
	//boolean update = true;
	public NonNullList<ItemStack> block_inv_content = NonNullList.<ItemStack>withSize(7, ItemStack.EMPTY);
	
	public EnergyBuffer energyBuffer = new EnergyBuffer(CommonConfig.Value_EnergyBuffer, CommonConfig.Value_EnergyBuffer, 0);
	
	private LazyOptional<IEnergyStorage> energyProxyCap = LazyOptional.of(() -> energyBuffer);
	
	public boolean prosses = false;
	public int time = 0;
	public int createProsses = 0;
	public int diskSize = 0;
	
	public TileEntityDiskWorkbench()
	{
		super(CURRENT_TILE);
		
		energyBuffer.eventchange = new IEventVoid()
		{
			
			@Override
			public void event()
			{
				if(world != null && !world.isRemote)
				{
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);//send update to client
				}
				
			}
		};
		
		energyBuffer.eventchange = new IEventVoid()
		{
			@Override
			public void event()
			{
				if(world != null && !world.isRemote)
				{
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);//send update to client
				}
			}
		};
	}
	
	public void update_prosses()
	{
		if(prosses)
		{
			if(createProsses >= diskSize || !CommonConfig.Value_EnableCostDisk)
			{
					ItemStack itemstack_diskcustom = new ItemStack(STItems.item_diskcustom);
					itemstack_diskcustom.getItem().onCreated(itemstack_diskcustom, world, null);
					CompoundNBT nbtitemdisk = itemstack_diskcustom.getOrCreateTag();
					
					if(nbtitemdisk == null)
						nbtitemdisk = new CompoundNBT();
					
					nbtitemdisk.putInt("st_cap", diskSize);
					
					UUID id = UUID.randomUUID();
					API.instance().getStorageDiskManager((ServerWorld) world).set(id, API.instance().createDefaultItemDisk((ServerWorld) world, diskSize));
	                API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
	                nbtitemdisk.putUniqueId("Id", id);
	                itemstack_diskcustom.setTag(nbtitemdisk);
	                
					block_inv_content.set(2, itemstack_diskcustom);
					
					prosses = false;
					//update = true;
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);
			}
			else
			{
				if(time > CommonConfig.Value_TimeCostPerSize)
				{
					if(energyBuffer.energy >= CommonConfig.Value_EnergyCostPerSize+countUpgrade())
					{
						time = 0;
						createProsses += CommonConfig.Value_ProssesAdvancementSize*countUpgrade();
						energyBuffer.energy -= CommonConfig.Value_EnergyCostPerSize+countUpgrade();
						
						//update = true;
						markDirty();
						world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);
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
	public void tick()
	{
		ItemStack i = block_inv_content.get(0);
		if(!i.isEmpty())
		{
			if(DiskUtils.validItemPart(i))
			{
				long memadd = DiskUtils.getMemoryFromItemPart(i);
				memory += memadd;
				//update = true;
				block_inv_content.set(0, ItemStack.EMPTY);
				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);
			}
		}
		
		if(!world.isRemote)
			update_prosses();
		//super.update();
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
	public void readFromNBT(CompoundNBT tag)
	{
		if(tag.contains("memory"))
			memory = tag.getLong("memory");
		
		if(tag.contains("inv"))
			ItemStackHelper.loadAllItems(tag.getCompound("inv"), block_inv_content);
		
		energyBuffer.ReadFromNBT(tag);
		
		if(tag.contains("prosses"))
			prosses = tag.getBoolean("prosses");
		
		if(tag.contains("time"))
			time = tag.getInt("time");
		
		if(tag.contains("createprosses"))
			createProsses = tag.getInt("createprosses");
		
		if(tag.contains("disksize"))
			diskSize = tag.getInt("disksize");
		
		markDirty();
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag)
	{
		tag.putLong("memory", memory);
		
		CompoundNBT nbt_inv = new CompoundNBT();
		ItemStackHelper.saveAllItems(nbt_inv, block_inv_content);
		tag.put("inv", nbt_inv);
		
		energyBuffer.WriteToNBT(tag);
		
		tag.putBoolean("prosses", prosses);
		tag.putInt("time", time);
		tag.putInt("createprosses", createProsses);
		tag.putInt("disksize", diskSize);
		
		return tag;
	}

	@Override
	protected void readFromServer(CompoundNBT tag)
	{
		if(tag.contains("memory"))
			memory = tag.getLong("memory");
		
		if(tag.contains("inv"))
			ItemStackHelper.loadAllItems(tag.getCompound("inv"), block_inv_content);
		
		energyBuffer.ReadFromNBT(tag);
		
		if(tag.contains("prosses"))
			prosses = tag.getBoolean("prosses");
		
		if(tag.contains("time"))
			time = tag.getInt("time");
		
		if(tag.contains("createprosses"))
			createProsses = tag.getInt("createprosses");
		
		if(tag.contains("disksize"))
			diskSize = tag.getInt("disksize");
	}

	@Override
	protected CompoundNBT writeToClient(CompoundNBT tag)
	{
		tag.putLong("memory", memory);
		
		CompoundNBT nbt_inv = new CompoundNBT();
		ItemStackHelper.saveAllItems(nbt_inv, block_inv_content);
		tag.put("inv", nbt_inv);
		
		energyBuffer.WriteToNBT(tag);
		
		tag.putBoolean("prosses", prosses);
		tag.putInt("time", time);
		tag.putInt("createprosses", createProsses);
		tag.putInt("disksize", diskSize);
		
		return tag;
	}
	
	@Override
	public CompoundNBT getUpdateTag()
	{
		CompoundNBT nbt = super.getUpdateTag();
		
		//send server config to client
		CompoundNBT nbt_cfg = new CompoundNBT();
		nbt_cfg.putLong("BP", pos.toLong());
		
		nbt_cfg.putInt("DMS",CommonConfig.Value_DiskMaxSize);
		nbt_cfg.putInt("DFMS",CommonConfig.Value_DiskFluidMaxSize);
		nbt_cfg.putBoolean("ECD",CommonConfig.Value_EnableCostDisk);
		nbt_cfg.putInt("ECPS",CommonConfig.Value_EnergyCostPerSize);
		nbt_cfg.putInt("TCPS",CommonConfig.Value_TimeCostPerSize);
		nbt_cfg.putInt("PAS",CommonConfig.Value_ProssesAdvancementSize);
		nbt_cfg.putInt("PASF",CommonConfig.Value_ProssesAdvancementSizeFluid);
		
		nbt.put("cfg", nbt_cfg);
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(CompoundNBT nbt)
	{
		super.handleUpdateTag(nbt);
		
		//update configuration from server
		if(nbt.contains("cfg"))
		{
			CompoundNBT nbt_cfg = nbt.getCompound("cfg");
			
			pos = BlockPos.fromLong(nbt_cfg.getLong("BP"));
			
			CommonConfig.Value_DiskMaxSize = nbt_cfg.getInt("DMS");
			CommonConfig.Value_DiskFluidMaxSize = nbt_cfg.getInt("DFMS");
			CommonConfig.Value_EnableCostDisk = nbt_cfg.getBoolean("ECD");
			CommonConfig.Value_EnergyCostPerSize = nbt_cfg.getInt("ECPS");
			CommonConfig.Value_TimeCostPerSize = nbt_cfg.getInt("TCPS");
			CommonConfig.Value_ProssesAdvancementSize = nbt_cfg.getInt("PAS");
			CommonConfig.Value_ProssesAdvancementSizeFluid = nbt_cfg.getInt("PASF");
		}
	}

	// ---------------- INVENTORY ----------------
	@Override
	public void clear()
	{
		block_inv_content.clear();
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
		if(index == 2)
			return ItemStackHelper.getAndSplit(block_inv_content, index, count);
		return ItemStack.EMPTY;
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
	public boolean isUsableByPlayer(PlayerEntity player)
	{
		return true;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		if(slot == 0)
		{
			ItemStack slot_itemStack = block_inv_content.get(0);
			if(slot_itemStack.isEmpty())
				return DiskUtils.validItemPart(stack);
			if(slot_itemStack.getCount() < slot_itemStack.getMaxStackSize() && slot_itemStack.isItemEqual(stack))
				return true;
		}
		else if(slot == 1)
		{
			ItemStack slot_itemStack = block_inv_content.get(1);
			if(slot_itemStack.isEmpty())
				return stack.getItem() == RSItems.STORAGE_HOUSING;
			if(slot_itemStack.getCount() < slot_itemStack.getMaxStackSize() && slot_itemStack.isItemEqual(stack))
				return true;
		}
		
		return false; 
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityEnergy.ENERGY)
		{
			return energyProxyCap.cast();
		}
		
		return super.getCapability(cap, side);
	}

	@Override
	public Container createMenu(int windowid, PlayerInventory playerInv, PlayerEntity player)
	{
		return new ContainerDiskWorkbench(windowid, this, playerInv);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent("diskworkbench");
	}

	@Override
	public void createDisk(int disksize)
	{
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
				
				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), Constants.BlockFlags.BLOCK_UPDATE);
				
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
}
