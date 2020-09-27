package javapower.storagetech.mekanism.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.block.BlockGasImporter;
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

public class NetworkNodeGasImporter extends NetworkNode implements IWhitelistBlacklist
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockGasImporter.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 20;
	public int tickUpdateDelay = TICK_UPDATE_DELAY;
	public boolean stackUpgrade = false;
	
	STData stData = null;
	
	public int tickUpdate;
	
	private int mode = IWhitelistBlacklist.BLACKLIST;
	
	private final GasInventory gasFilters = new GasInventory(9).addListener(new NetworkNodeGasInventoryListener(this));
	
	private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK)
	        .addListener(new NetworkNodeInventoryListener(this))
	        .addListener((handler, slot, reading) ->
	        {
	        	//int speed = ((UpgradeItemHandler)handler).getUpgradeCount(UpgradeItem.Type.SPEED);
	        	//tickUpdateDelay = TICK_UPDATE_DELAY - speed*5;
	        	tickUpdateDelay = ((UpgradeItemHandler)handler).getSpeed();
	        	stackUpgrade = ((UpgradeItemHandler)handler).getUpgradeCount(UpgradeItem.Type.STACK) > 0;
	        	
	            if (!world.isRemote)
	            {
	                if (!reading)
	                    WorldUtils.updateBlock(world, pos);
	            }
	        });
	
	public NetworkNodeGasImporter(World world, BlockPos pos)
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
					IGasHandler handler = te.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
					if(handler != null && stData != null)
					{
						GasStack result = handler.extractChemical(stackUpgrade ? 64000 : 1000, Action.SIMULATE);
						if(isAllowed(result))
						{
							GasStack network_result = stData.getMekanismData().insertChemical(result, Action.SIMULATE);
							long amntImported = result.getAmount() - network_result.getAmount();
							
							if(amntImported > 0)
								stData.getMekanismData().insertChemical(handler.extractChemical(amntImported, Action.EXECUTE), Action.EXECUTE);
						}
					}
				}
			}
			else
				++tickUpdate;
		}
	}

	private boolean isAllowed(GasStack result)
	{
		if(result.isEmpty())
			return false;
		
		if(gasFilters.isEmpty())
			return true;
		
		if(mode == IWhitelistBlacklist.WHITELIST)
		{
			for(GasStack stackIn : gasFilters.getGass())
			{
				if(stackIn.isTypeEqual(result))
					return true;
			}
			return false;
		}
		else
		{
			for(GasStack stackIn : gasFilters.getGass())
			{
				if(stackIn.isTypeEqual(result))
					return false;
			}
			return true;
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
		
		tag.putInt("FilterMode", mode);
		tag.put("GasFilters", gasFilters.writeToNbt());
		
		return tag;
	}
	
	@Override
	public void readConfiguration(CompoundNBT tag)
	{
		super.readConfiguration(tag);
		
		if (tag.contains("FilterMode"))
            mode = tag.getInt("FilterMode");
		
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
	
	@Override
    public int getWhitelistBlacklistMode()
	{
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int _mode)
    {
        mode = _mode;
        markDirty();
    }

}
