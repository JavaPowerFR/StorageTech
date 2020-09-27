package javapower.storagetech.mekanism.node;

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

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STNetworkManager;
import javapower.storagetech.mekanism.api.IGasStorageNode;
import javapower.storagetech.mekanism.api.IItemGasStorageDisk;
import javapower.storagetech.mekanism.block.BlockGasDrive;
import javapower.storagetech.mekanism.data.GasDisk;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeGasDrive extends NetworkNode implements IGasStorageNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockGasDrive.raw_name);
	
	private static final int DISK_STATE_UPDATE_THROTTLE = 30;
	private int ticksSinceBlockUpdateRequested;
    private boolean blockUpdateRequested;
    
    private long capacity = 0l;
	
	private final BaseItemHandler disksHandler = new BaseItemHandler(8)
	        .addValidator((stack) -> stack != null && stack.getItem() instanceof IItemGasStorageDisk)
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
	
	private List<GasDisk> disks = new ArrayList<GasDisk>();
	private STNetworkManager stNetworkManager = null;
	

	public NetworkNodeGasDrive(World world, BlockPos pos)
	{
		super(world, pos);
	}

	private void updateContainer()
	{
		if(!world.isRemote)
		{
			long _capacity = 0;
			disks.clear();
			
			for(int slotId = 0; slotId < disksHandler.getSlots(); ++slotId)
	        {
				ItemStack stack = disksHandler.getStackInSlot(slotId);
				if(stack != null && stack.getItem() instanceof IItemGasStorageDisk)
				{
					GasDisk GasDisk = STAPI.getNetworkManager((ServerWorld) world).getMekanisumManager().getGasDisk(((IItemGasStorageDisk)stack.getItem()).getId(stack));
					if(GasDisk != null)
					{
						disks.add(GasDisk);
						_capacity += GasDisk.capacity;
					}
				}
	        }
			
			capacity = _capacity;
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
		
		StackUtils.readItems(disksHandler, 0, tag);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		
		StackUtils.writeItems(disksHandler, 0, tag);
		
		return tag;
	}
	
	@Override
	protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
	{
		WorldUtils.updateBlock(world, pos);
		if(state)
		{
			//connect
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).getMekanismData().putGasStorageListener(this);
			stNetworkManager = STAPI.getNetworkManager((ServerWorld)world);
		}
		else
		{
			//disconnect
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).getMekanismData().removeGasStorageListener(this);
			stNetworkManager = null;
		}
		
		super.onConnectedStateChange(network, state, cause);
	}
	
	public IItemHandler getDisksHandler()
	{
        return disksHandler;
    }
	
	@Override
	public IItemHandler getDrops()
	{
		return disksHandler;
	}

	public DiskState[] getDiskState()
	{
		DiskState[] diskStates = new DiskState[8];
		for (int i = 0; i < 8; ++i)
		{
            DiskState state = DiskState.NONE;
            ItemStack stack = disksHandler.getStackInSlot(i);
            
            if(stack != null && stack.getItem() instanceof IItemGasStorageDisk)
            {
            	if (!canUpdate())
	            {
	                state = DiskState.DISCONNECTED;
	            }
            	else
            	{
            		if(!world.isRemote)
            		{
            			GasDisk disk = STAPI.getNetworkManager((ServerWorld) world).getMekanisumManager().getGasDisk(((IItemGasStorageDisk)stack.getItem()).getId(stack));
            			state = getDiskState(disk.getAmount(), disk.getCapacity());
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
		return capacity;
	}

	@Override
	public long getAmount()
	{
		long stored = 0;
		
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			GasDisk disk = disks.get(slotId);
			stored += disk.getAmount();
        }
		return stored;
	}
	
	@Override
	public GasStack insertChemical(GasStack stack, Action action)
	{
		GasStack result = stack;
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			GasDisk disk = disks.get(slotId);
			
			result = disk.insertChemical(result, action);
			
			if(result.equals(GasStack.EMPTY))
				break;
        }
		
		if(action == Action.EXECUTE)
		{
			stNetworkManager.markForSaving();
			markDirty();
			requestBlockUpdate();
		}
		
		return result;
	}
	
	@Override
	public GasStack extractChemical(GasStack stack, Action action)
	{
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			GasDisk disk = disks.get(slotId);
			GasStack gasStack = disk.extractChemical(stack, action);
			if(!gasStack.equals(GasStack.EMPTY))
			{
				return gasStack;
			}
        }
		return GasStack.EMPTY;
	}
	
	public static DiskState getDiskState(long stored, long capacity)
	{
        if (stored == capacity)
        {
            return DiskState.FULL;
        }
        else if (((stored * 100) / capacity) >= DiskState.DISK_NEAR_CAPACITY_THRESHOLD)
        {
            return DiskState.NEAR_CAPACITY;
        }
        else
        {
            return DiskState.NORMAL;
        }
    }
}
