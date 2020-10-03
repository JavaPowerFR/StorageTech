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
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.block.BlockChemicalExporter;
import javapower.storagetech.mekanism.inventory.ChemicalInventory;
import javapower.storagetech.mekanism.inventory.NetworkNodeChemicalInventoryListener;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeChemicalExporter extends NetworkNode
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockChemicalExporter.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 20;
	public int tickUpdateDelay = TICK_UPDATE_DELAY;
	public boolean stackUpgrade = false;
	
	STData stData = null;
	
	public int tickUpdate;
	
	private int chemicalType = 0;
	
	private final ChemicalInventory chemicalFilters = new ChemicalInventory(9).addListener(new NetworkNodeChemicalInventoryListener(this));
	
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
	
	public NetworkNodeChemicalExporter(World world, BlockPos pos)
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
					if(chemicalType == MekanismUtils.CHEMICAL_TYPE_GAS)
					{
						IGasHandler handler = te.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
						if(handler != null && stData != null)
						{
							for(ChemicalStack<?> fstack : chemicalFilters.getChemicals())
							{
								if(fstack instanceof GasStack && !fstack.isEmpty())
								{
									GasStack network_result = (GasStack) stData.getMekanismData().extractChemical(new GasStack((GasStack) fstack, stackUpgrade ? 64000 : 1000), Action.SIMULATE);
									if(!network_result.isEmpty())
									{
										GasStack result = handler.insertChemical(network_result, Action.SIMULATE);
										if(result.isEmpty() || result.getAmount() < network_result.getAmount())
										{
											long amntExported = network_result.getAmount() - result.getAmount();
											
											if(amntExported > 0)
											{
												handler.insertChemical((GasStack) stData.getMekanismData().extractChemical(new GasStack((GasStack) fstack, amntExported), Action.EXECUTE), Action.EXECUTE);
												break;
											}
										}
									}
								}
							}
						}
					}
					else if(chemicalType == MekanismUtils.CHEMICAL_TYPE_INFUSION)
					{
						IInfusionHandler handler = te.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
						if(handler != null && stData != null)
						{
							for(ChemicalStack<?> fstack : chemicalFilters.getChemicals())
							{
								if(fstack instanceof InfusionStack && !fstack.isEmpty())
								{
									InfusionStack network_result = (InfusionStack) stData.getMekanismData().extractChemical(new InfusionStack((InfusionStack) fstack, stackUpgrade ? 64000 : 1000), Action.SIMULATE);
									if(!network_result.isEmpty())
									{
										InfusionStack result = handler.insertChemical(network_result, Action.SIMULATE);
										if(result.isEmpty() || result.getAmount() < network_result.getAmount())
										{
											long amntExported = network_result.getAmount() - result.getAmount();
											
											if(amntExported > 0)
											{
												handler.insertChemical((InfusionStack) stData.getMekanismData().extractChemical(new InfusionStack((InfusionStack) fstack, amntExported), Action.EXECUTE), Action.EXECUTE);
												break;
											}
										}
									}
								}
							}
						}
					}
					else if(chemicalType == MekanismUtils.CHEMICAL_TYPE_SLURRY)
					{
						ISlurryHandler handler = te.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY, getDirection().getOpposite()).orElse(null);
						if(handler != null && stData != null)
						{
							for(ChemicalStack<?> fstack : chemicalFilters.getChemicals())
							{
								if(fstack instanceof SlurryStack && !fstack.isEmpty())
								{
									SlurryStack network_result = (SlurryStack) stData.getMekanismData().extractChemical(new SlurryStack((SlurryStack) fstack, stackUpgrade ? 64000 : 1000), Action.SIMULATE);
									if(!network_result.isEmpty())
									{
										SlurryStack result = handler.insertChemical(network_result, Action.SIMULATE);
										if(result.isEmpty() || result.getAmount() < network_result.getAmount())
										{
											long amntExported = network_result.getAmount() - result.getAmount();
											
											if(amntExported > 0)
											{
												handler.insertChemical((SlurryStack) stData.getMekanismData().extractChemical(new SlurryStack((SlurryStack) fstack, amntExported), Action.EXECUTE), Action.EXECUTE);
												break;
											}
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
		
		tag.put("Filters", chemicalFilters.writeToNbt());
		tag.putInt("ChemicalType", chemicalType);
		
		return tag;
	}
	
	@Override
	public void readConfiguration(CompoundNBT tag)
	{
		super.readConfiguration(tag);
		
		if(tag.contains("Filters"))
			chemicalFilters.readFromNbt(tag.getCompound("Filters"));
		
		if (tag.contains("ChemicalType"))
            chemicalType = tag.getInt("ChemicalType");
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
	
	public ChemicalInventory getChemicalFilters()
	{
		return chemicalFilters;
	}
	
	public int getChemicalType()
	{
		return chemicalType;
	}

	public void setChemicalType(int v)
	{
		chemicalType = v;
		markDirty();
	}

}
