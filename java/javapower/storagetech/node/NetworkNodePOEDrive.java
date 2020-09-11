package javapower.storagetech.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.api.IEnergyStorageCell;
import javapower.storagetech.api.IEnergyStorageNode;
import javapower.storagetech.block.BlockPOEDrive;
import javapower.storagetech.core.STAPI;
import javapower.storagetech.core.StorageTech;
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
	        .addValidator((stack) -> stack != null && stack.getItem() instanceof IEnergyStorageCell)
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
	

	public NetworkNodePOEDrive(World world, BlockPos pos)
	{
		super(world, pos);
	}

	private void updateContainer()
	{
		long capacity = 0;
        for(int slotId = 0; slotId < cells.getSlots(); ++slotId)
        {
        	ItemStack stack = cells.getStackInSlot(slotId);
        	if(stack != null && stack.getItem() instanceof IEnergyStorageCell)
        		capacity += ((IEnergyStorageCell)stack.getItem()).getCapacity(stack);
        }
        
        energyCapacity = capacity;
        
		long iocapacity = 0;
        for(int slotId = 0; slotId < cells.getSlots(); ++slotId)
        {
        	ItemStack stack = cells.getStackInSlot(slotId);
        	if(stack != null && stack.getItem() instanceof IEnergyStorageCell)
        		iocapacity += ((IEnergyStorageCell)stack.getItem()).getIOCapacity(stack);
        }
        
        energyIOCapacity = iocapacity;
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
		}
		else
		{
			//disconnect
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).removeEnergyStorageListener(this);
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
            if(stack != null && stack.getItem() instanceof IEnergyStorageCell)
            {
	            if (!canUpdate())
	            {
	                state = DiskState.DISCONNECTED;
	            }
	            else
	            {
	            	IEnergyStorageCell esc = ((IEnergyStorageCell)stack.getItem());
	            	state = DiskState.get(esc.getEnergyStored(stack), esc.getCapacity(stack));
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
        for(int slotId = 0; slotId < cells.getSlots(); ++slotId)
        {
        	ItemStack stack = cells.getStackInSlot(slotId);
        	if(stack != null && stack.getItem() instanceof IEnergyStorageCell)
        		stored += ((IEnergyStorageCell)stack.getItem()).getEnergyStored(stack);
        }
		return stored;
	}

	@Override
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		long energyReceved = 0l;
		
		long limitedMaxReceive = Math.min(maxReceive, getIOCapacity());
		
		for(int slotId = 0; slotId < cells.getSlots(); ++slotId)
        {
        	ItemStack stack = cells.getStackInSlot(slotId);
        	if(stack != null && stack.getItem() instanceof IEnergyStorageCell)
        	{
        		IEnergyStorageCell cell = (IEnergyStorageCell) stack.getItem();
        		long energyInsertable = limitedMaxReceive - energyReceved;
        		if(energyInsertable <= 0)
        			break;
        		energyReceved += cell.receiveEnergy(stack, (int) Math.min(energyInsertable, Integer.MAX_VALUE - 1) , simulate);
        	}
        }
		
		if(energyReceved > 0 && !simulate)
		{
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
		for(int slotId = 0; slotId < cells.getSlots(); ++slotId)
        {
        	ItemStack stack = cells.getStackInSlot(slotId);
        	if(stack != null && stack.getItem() instanceof IEnergyStorageCell)
        	{
        		IEnergyStorageCell cell = (IEnergyStorageCell) stack.getItem();
        		long energyExtracteble = limitedMaxExtract - energyExtracted;
        		if(energyExtracteble <= 0)
        			break;
        		energyExtracted += cell.extractEnergy(stack, (int) Math.min(energyExtracteble, Integer.MAX_VALUE - 1), simulate);
        	}
        }
		
		if(energyExtracted > 0 && !simulate)
		{
			markDirty();
			requestBlockUpdate();
		}
		
		return energyExtracted;
	}

}
