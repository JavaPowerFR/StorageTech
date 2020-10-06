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
import javapower.storagetech.mekanism.api.IChemicalStorageNode;
import javapower.storagetech.mekanism.api.IItemChemicalStorageDisk;
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.block.BlockChemicalDrive;
import javapower.storagetech.mekanism.data.ChemicalDisk;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeChemicalDrive extends NetworkNode implements IChemicalStorageNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockChemicalDrive.raw_name);
	
	private static final int DISK_STATE_UPDATE_THROTTLE = 30;
	private int ticksSinceBlockUpdateRequested;
    private boolean blockUpdateRequested;
    
    private long capacity = 0l;
	
	private final BaseItemHandler disksHandler = new BaseItemHandler(8)
	        .addValidator((stack) -> stack != null && stack.getItem() instanceof IItemChemicalStorageDisk)
	        .addListener(new NetworkNodeInventoryListener(this))
	        .addListener((handler, slot, reading) ->
	        {
	        	updateContainer();
	        	requestBlockUpdate();
	            if (!world.isRemote)
	            {
	                if (!reading)
	                    WorldUtils.updateBlock(world, pos);
	                //markDirty();
	            }
	        });
	
	private List<ChemicalDisk> disks = new ArrayList<ChemicalDisk>();
	private STNetworkManager stNetworkManager = null;
	
	public NetworkNodeChemicalDrive(World world, BlockPos pos)
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
				if(stack != null && stack.getItem() instanceof IItemChemicalStorageDisk)
				{
					ChemicalDisk chemicalDisk = STAPI.getGlobalNetworkManager((ServerWorld) world).getGlobalMekanisumManager().getChemicalDisk(((IItemChemicalStorageDisk)stack.getItem()).getId(stack));
					if(chemicalDisk != null)
					{
						disks.add(chemicalDisk);
						_capacity += chemicalDisk.capacity;
					}
				}
	        }
			
			capacity = _capacity;
			
			if(network != null)
			{
				if(stNetworkManager == null)
					stNetworkManager = STAPI.getNetworkManager((ServerWorld) world);
				
				stNetworkManager.getStData(network).getMekanismData().updateView();
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
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).getMekanismData().putChemicalStorageListener(this);
			stNetworkManager = STAPI.getNetworkManager((ServerWorld)world);
		}
		else
		{
			//disconnect
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).getMekanismData().removeChemicalStorageListener(this);
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
            
            if(stack != null && stack.getItem() instanceof IItemChemicalStorageDisk)
            {
            	if (!canUpdate())
	            {
	                state = DiskState.DISCONNECTED;
	            }
            	else
            	{
            		if(!world.isRemote)
            		{
            			ChemicalDisk disk = STAPI.getGlobalNetworkManager((ServerWorld) world).getGlobalMekanisumManager().getChemicalDisk(((IItemChemicalStorageDisk)stack.getItem()).getId(stack));
            			if(disk != null)
            				state = getDiskState(disk.getAmount(), disk.getCapacity());
            		}
            	}
            }
            
            diskStates[i] = state;
		}
		return diskStates;
	}
	
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
			ChemicalDisk disk = disks.get(slotId);
			stored += disk.getAmount();
        }
		return stored;
	}
	
	@Override
	public ChemicalStack<?> insertChemical(ChemicalStack<?> stack, Action action)
	{
		ChemicalStack<?> result = stack;
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			ChemicalDisk disk = disks.get(slotId);
			
			result = disk.insertChemical(result, action);
			
			if(result.isEmpty())
				break;
        }
		
		if(action == Action.EXECUTE)
		{
			stNetworkManager.markForSaving();
			stNetworkManager.getGlobal().markForSaving();
			markDirty();
			requestBlockUpdate();
		}
		
		return result;
	}
	
	@Override
	public ChemicalStack<?> extractChemical(ChemicalStack<?> stack, Action action)
	{
		for(int slotId = 0; slotId < disks.size(); ++slotId)
        {
			ChemicalDisk disk = disks.get(slotId);
			ChemicalStack<?> chemicalStack = disk.extractChemical(stack, action);
			if(!chemicalStack.isEmpty())
			{
				if(action == Action.EXECUTE)
				{
					stNetworkManager.markForSaving();
					stNetworkManager.getGlobal().markForSaving();
					markDirty();
					requestBlockUpdate();
				}
				return chemicalStack;
			}
        }
		
		return MekanismUtils.getEmpty(stack);
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

	@Override
	public List<ChemicalDisk> getDisks()
	{
		return disks;
	}
}
