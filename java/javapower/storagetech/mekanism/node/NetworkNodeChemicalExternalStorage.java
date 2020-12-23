package javapower.storagetech.mekanism.node;

import java.util.List;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.api.IChemicalStorageNode;
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.block.BlockChemicalExternalStorage;
import javapower.storagetech.mekanism.data.ChemicalDisk;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class NetworkNodeChemicalExternalStorage extends NetworkNode implements IChemicalStorageNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockChemicalExternalStorage.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 20;
	public int tickUpdateDelay = TICK_UPDATE_DELAY;
	//public boolean stackUpgrade = false;
	
	STData stData = null;
	
	public int tickUpdate;
	
	private int chemicalType = 0;
	
	//private final ChemicalInventory chemicalFilters = new ChemicalInventory(9).addListener(new NetworkNodeChemicalInventoryListener(this));
	
	public NetworkNodeChemicalExternalStorage(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Override
	public int getEnergyUsage()
	{
		return RS.SERVER_CONFIG.getExternalStorage().getUsage();
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
					if(chemicalType == MekanismUtils.CHEMICAL_TYPE_GAS)
					{
						IGasHandler handler = te.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
					}
					else if(chemicalType == MekanismUtils.CHEMICAL_TYPE_INFUSION)
					{
						IInfusionHandler handler = te.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
					}
					else if(chemicalType == MekanismUtils.CHEMICAL_TYPE_SLURRY)
					{
						ISlurryHandler handler = te.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
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

        //StackUtils.writeItems(upgrades, 0, tag);
        return tag;
    }
	
	@Override
    public void read(CompoundNBT tag)
	{
        super.read(tag);
        
        //StackUtils.readItems(upgrades, 0, tag);
    }
	
	@Override
	public CompoundNBT writeConfiguration(CompoundNBT tag)
	{
		super.writeConfiguration(tag);
		
		//tag.put("Filters", chemicalFilters.writeToNbt());
		tag.putInt("ChemicalType", chemicalType);
		
		return tag;
	}
	
	@Override
	public void readConfiguration(CompoundNBT tag)
	{
		super.readConfiguration(tag);
		
		/*if(tag.contains("Filters"))
			chemicalFilters.readFromNbt(tag.getCompound("Filters"));*/
		
		if (tag.contains("ChemicalType"))
            chemicalType = tag.getInt("ChemicalType");
	}
	
	/*@Override
    public IItemHandler getDrops()
    {
        return upgrades;
    }

	public IItemHandler getUpgrades()
	{
		return upgrades;
	}
	
	public ChemicalInventory getChemicalFilters()
	{
		return chemicalFilters;
	}*/
	
	public int getChemicalType()
	{
		return chemicalType;
	}

	public void setChemicalType(int v)
	{
		chemicalType = v;
		markDirty();
	}

	@Override
	public long getCapacity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAmount()
	{
		return 0;
	}

	@Override
	public ChemicalStack<?> extractChemical(ChemicalStack<?> stack, Action action)
	{
		// TODO Auto-generated method stub
		
		return MekanismUtils.getEmpty(stack);
	}

	@Override
	public ChemicalStack<?> insertChemical(ChemicalStack<?> stack, Action action)
	{
		// TODO Auto-generated method stub
		return stack;
	}

	@Override
	public List<ChemicalDisk> getDisks()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
