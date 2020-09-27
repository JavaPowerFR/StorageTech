package javapower.storagetech.mekanism.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.block.BlockGasExporter;
import javapower.storagetech.mekanism.inventory.GasInventory;
import javapower.storagetech.mekanism.inventory.NetworkNodeGasInventoryListener;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeGasExporter extends NetworkNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockGasExporter.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 20;
	public int tickUpdateDelay = TICK_UPDATE_DELAY;
	public boolean stackUpgrade = false;
	
	STData stData = null;
	
	public int tickUpdate;
	
	private final GasInventory gasFilters = new GasInventory(9).addListener(new NetworkNodeGasInventoryListener(this));
	
	private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK)
	        .addListener(new NetworkNodeInventoryListener(this))
	        .addListener((handler, slot, reading) ->
	        {
	        	tickUpdateDelay = ((UpgradeItemHandler)handler).getSpeed();
	        	stackUpgrade = ((UpgradeItemHandler)handler).getUpgradeCount(UpgradeItem.Type.STACK) > 0;
	        	
	            if (!world.isRemote)
	            {
	                if (!reading)
	                    WorldUtils.updateBlock(world, pos);
	            }
	        });
	
	public NetworkNodeGasExporter(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Override
	public int getEnergyUsage()
	{
		return RS.SERVER_CONFIG.getExporter().getUsage();
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
					IGasHandler handler = te.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
					if(handler != null && stData != null)
					{
						for(GasStack fstack : gasFilters.getGass())
						{
							if(!fstack.isEmpty())
							{
								GasStack network_result = stData.getMekanismData().extractChemical(new GasStack(fstack, stackUpgrade ? 64000 : 1000), Action.SIMULATE);
								if(!network_result.isEmpty())
								{
									GasStack result = handler.insertChemical(network_result, Action.SIMULATE);
									if(result.isEmpty() || result.getAmount() < network_result.getAmount())
									{
										long amntExported = network_result.getAmount() - result.getAmount();
										
										if(amntExported > 0)
										{
											handler.insertChemical(stData.getMekanismData().extractChemical(new GasStack(fstack, amntExported), Action.EXECUTE), Action.EXECUTE);
											break;
										}
									}
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

        StackUtils.writeItems(upgrades, 0, tag);
        return tag;
    }
	
	@Override
    public void read(CompoundNBT tag)
	{
        super.read(tag);
        
        StackUtils.readItems(upgrades, 0, tag);
    }
	
	@Override
	public CompoundNBT writeConfiguration(CompoundNBT tag)
	{
		super.writeConfiguration(tag);
		
		tag.put("GasFilters", gasFilters.writeToNbt());
		
		return tag;
	}
	
	@Override
	public void readConfiguration(CompoundNBT tag)
	{
		super.readConfiguration(tag);
		
		if(tag.contains("GasFilters"))
			gasFilters.readFromNbt(tag.getCompound("GasFilters"));
	}
	
	@Override
    public IItemHandler getDrops()
    {
        return upgrades;
    }

	public IItemHandler getUpgrades()
	{
		return upgrades;
	}
	
	public GasInventory getGasFilters()
	{
		return gasFilters;
	}

}
