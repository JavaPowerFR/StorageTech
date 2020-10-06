package javapower.storagetech.node;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.api.IEnergyStorageNode;
import javapower.storagetech.api.IItemEnergyStorageDisk;
import javapower.storagetech.api.STAPI;
import javapower.storagetech.block.BlockPOEDrive;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.EnergyDisk;
import javapower.storagetech.data.STNetworkManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodePOEDrive extends NetworkNode implements IEnergyStorageNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockPOEDrive.raw_name);
	
	private static final int DISK_STATE_UPDATE_THROTTLE = 30;
	private int ticksSinceBlockUpdateRequested;
    private boolean blockUpdateRequested;
    
    private long energyCapacity = 0l;
    private long energyIOCapacity = 0l;
	
	private final BaseItemHandler cells = new BaseItemHandler(8)
	        .addValidator((stack) -> stack != null && stack.getItem() instanceof IItemEnergyStorageDisk)
	        .addListener(new NetworkNodeInventoryListener(this))
	        .addListener((handler, slot, reading) ->
	        {
	        	updateContainer();
	        	requestBlockUpdate();
	            if (!world.isRemote)
	            {
	                if (!reading)
	                    WorldUtils.updateBlock(world, pos);
	            }
	        });
	
	private List<EnergyDisk> disks = new ArrayList<EnergyDisk>();
	private STNetworkManager stNetworkManager = null;
	

	public NetworkNodePOEDrive(World world, BlockPos pos)
	{
		super(world, pos);
	}

	private void updateContainer()
	{
		if(!world.isRemote)
		{
			long capacity = 0;
			long iocapacity = 0;	
			disks.clear();
			
			for(int slotId = 0; slotId < cells.getSlots(); ++slotId)
	        {
				ItemStack stack = cells.getStackInSlot(slotId);
				if(stack != null && stack.getItem() instanceof IItemEnergyStorageDisk)
				{
					EnergyDisk energydisk = STAPI.getGlobalNetworkManager((ServerWorld) world).getEnergyDisk(((IItemEnergyStorageDisk)stack.getItem()).getId(stack));
					if(energydisk != null)
					{
						disks.add(energydisk);
						capacity += energydisk.capacity;
						iocapacity += energydisk.io_capacity;
					}
				}
	        }
			
			energyCapacity = capacity;
			energyIOCapacity = iocapacity;
			
			if(network != null)
			{
				if(stNetworkManager == null)
					stNetworkManager = STAPI.getNetworkManager((ServerWorld) world);
				
				stNetworkManager.getStData(network).updateView();
			}
		}
	}

	@Override
	public int getEnergyUsage()
	{
		return  RS.SERVER_CONFIG.getDiskDrive().getUsage();
	}
	
	@Override
    public void update()
	{
        super.update();

        if (blockUpdateRequested)
        {
            ++ticksSinceBlockUpdateRequested;

            if (ticksSinceBlockUpdateRequested > DISK_STATE_UPDATE_THROTTLE)
            {
                WorldUtils.updateBlock(world, pos);

                blockUpdateRequested = false;
                ticksSinceBlockUpdateRequested = 0;
            }
        }
        else
        {
            ticksSinceBlockUpdateRequested = 0;
        }
    }

    void requestBlockUpdate()
    {
        blockUpdateRequested = true;
    }

	@Override
	public ResourceLocation getId()
	{
		return NETWORK_NODE_ID;
	}
	
	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		
		StackUtils.readItems(cells, 0, tag);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		
		StackUtils.writeItems(cells, 0, tag);
		
		return tag;
	}
	
	@Override
	protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
	{
		WorldUtils.updateBlock(world, pos);
		if(state)
		{
			//connect
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).putEnergyStorageListener(this);
			stNetworkManager = STAPI.getNetworkManager((ServerWorld)world);
		}
		else
		{
			//disconnect
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).removeEnergyStorageListener(this);
			stNetworkManager = null;
		}
		
		super.onConnectedStateChange(network, state, cause);
	}
	
	public IItemHandler getEnergyCells()
	{
        return cells;
    }
	
	@Override
	public IItemHandler getDrops()
	{
		return cells;
	}

	public DiskState[] getDiskState()
	{
		DiskState[] diskStates = new DiskState[8];
		for (int i = 0; i < 8; ++i)
		{
            DiskState state = DiskState.NONE;
            ItemStack stack = cells.getStackInSlot(i);
            
            if(stack != null && stack.getItem() instanceof IItemEnergyStorageDisk)
            {
            	if (!canUpdate())
	            {
	                state = DiskState.DISCONNECTED;
	            }
            	else
            	{
            		if(!world.isRemote)
            		{
            			EnergyDisk disk = STAPI.getGlobalNetworkManager((ServerWorld) world).getEnergyDisk(((IItemEnergyStorageDisk)stack.getItem()).getId(stack));
            			if(disk != null)
            				state = DiskState.get(disk.getEnergyStored(), disk.getCapacity());
            		}
            	}
            }
            
            diskStates[i] = state;
		}
		return diskStates;
	}

	@Override
	public long getCapacity()
	{
		return energyCapacity;
	}

	@Override
	public long getIOCapacity()
	{
		return energyIOCapacity;
	}

	@Override
	public long getEnergyStored()
	{
		long stored = 0;
		
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			EnergyDisk disk = disks.get(slotId);
			stored += disk.getEnergyStored();
        }
		return stored;
	}

	@Override
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		long energyReceved = 0l;
		
		long limitedMaxReceive = Math.min(maxReceive, getIOCapacity());
		
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			EnergyDisk disk = disks.get(slotId);
			
			long energyInsertable = limitedMaxReceive - energyReceved;
			if(energyInsertable <= 0)
    			break;
			
			energyReceved += disk.receiveEnergy((int) Math.min(energyInsertable, Integer.MAX_VALUE - 1) , simulate);
        }
		
		if(energyReceved > 0 && !simulate)
		{
			stNetworkManager.markForSaving();
			stNetworkManager.getGlobal().markForSaving();
			markDirty();
			requestBlockUpdate();
		}
		
		return energyReceved;
	}

	@Override
	public long extractEnergy(long maxExtract, boolean simulate)
	{
		long energyExtracted = 0l;
		
		long limitedMaxExtract = Math.min(maxExtract, getIOCapacity());
		
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			EnergyDisk disk = disks.get(slotId);
			
			long energyExtracteble = limitedMaxExtract - energyExtracted;
			if(energyExtracteble <= 0)
    			break;
			
			energyExtracted += disk.extractEnergy((int) Math.min(energyExtracteble, Integer.MAX_VALUE - 1), simulate);
        }
		
		if(energyExtracted > 0 && !simulate)
		{
			stNetworkManager.markForSaving();
			stNetworkManager.getGlobal().markForSaving();
			markDirty();
			requestBlockUpdate();
		}
		
		return energyExtracted;
	}
}
