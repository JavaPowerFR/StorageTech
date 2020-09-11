package javapower.storagetech.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.api.IEnergyStorageNode;
import javapower.storagetech.block.BlockPOEImporter;
import javapower.storagetech.core.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodePOEImporter extends NetworkNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockPOEImporter.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 20;
	public int tickUpdateDelay = TICK_UPDATE_DELAY;
	
	STData stData = null;
	
	public int tickUpdate;
	
	private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED)
	        .addListener(new NetworkNodeInventoryListener(this))
	        .addListener((handler, slot, reading) ->
	        {
	        	int speed = ((UpgradeItemHandler)handler).getUpgradeCount(UpgradeItem.Type.SPEED);
	        	tickUpdateDelay = TICK_UPDATE_DELAY - speed*5;
	        	
	            if (!world.isRemote)
	            {
	                if (!reading)
	                    WorldUtils.updateBlock(world, pos);
	            }
	        });
	
	public NetworkNodePOEImporter(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Override
	public int getEnergyUsage()
	{
		return RS.SERVER_CONFIG.getImporter().getUsage();
	}

	@Override
	public ResourceLocation getId()
	{
		return NETWORK_NODE_ID;
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(!world.isRemote && canUpdate())
		{
			if(tickUpdate > tickUpdateDelay)
			{
				tickUpdate = 0;
				TileEntity te = world.getTileEntity(pos.offset(getDirection()));
				if(te != null)
				{
					IEnergyStorage energystorage = te.getCapability(CapabilityEnergy.ENERGY, getDirection().getOpposite()).orElse(null);
					if(energystorage != null && stData != null && energystorage.canExtract())
					{
						for(IEnergyStorageNode energy_node : stData.getEnergyStorages())
						{
							if(energy_node != null)
							{
								long space = energy_node.getEnergyCanInsert();
								if(space > 0)
								{
									int extractedEnergy = energystorage.extractEnergy((int) Math.min(space, Integer.MAX_VALUE - 1), false); 
									energy_node.receiveEnergy(extractedEnergy, false);
									break;
								}
							}
						}
					}
				}
			}
			else
				++tickUpdate;
		}
	}
	
	@Override
	protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
	{
		super.onConnectedStateChange(network, state, cause);
		
		if(state)
			stData = STAPI.getNetworkManager((ServerWorld) world).getStData(network);
		else
			stData = null;
	}
	
	@Override
    public CompoundNBT write(CompoundNBT tag)
	{
        super.write(tag);

        StackUtils.writeItems(upgrades, 1, tag);

        return tag;
    }
	
	@Override
    public void read(CompoundNBT tag)
	{
        super.read(tag);

        StackUtils.readItems(upgrades, 1, tag);
    }

	public IItemHandler getUpgrades()
	{
		return upgrades;
	}

}
