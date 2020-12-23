package javapower.storagetech.mekanism.node;

import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.RedstoneMode;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.block.BlockChemicalDetector;
import javapower.storagetech.mekanism.inventory.ChemicalInventory;
import javapower.storagetech.mekanism.inventory.NetworkNodeChemicalInventoryListener;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class NetworkNodeChemicalDetector extends NetworkNode implements IComparable
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockChemicalDetector.raw_name);
	
	private static final int SPEED = 5;
	
	private final ChemicalInventory chemicalFilters = new ChemicalInventory(1).addListener(new NetworkNodeChemicalInventoryListener(this));
	
	private int compare = IComparer.COMPARE_NBT;
	private int mode = DetectorNetworkNode.MODE_EQUAL;
    private long amount = 0;
    
    private boolean powered = false;
    private boolean wasPowered;
    
    private STData stData = null;
    
	public NetworkNodeChemicalDetector(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Override
	public int getEnergyUsage()
	{
		return RS.SERVER_CONFIG.getDetector().getUsage();
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
		
		if (powered != wasPowered && world.isBlockPresent(pos))
		{
            wasPowered = powered;

            world.setBlockState(pos, world.getBlockState(pos).with(BlockChemicalDetector.POWERED, powered));
            world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
        }
		
		if (canUpdate() && ticks % SPEED == 0 && stData != null)
		{
			ChemicalStack<?> filter = chemicalFilters.getChemical(0);
			if(filter != null)
			{
				ChemicalStack<?> stack = stData.getMekanismData().getChemicalTotalStack(filter.getType());
				if(stack != null)
				{
					powered = isPowered(stack.getAmount());
				}
			}
		}
	}
	
	@Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
	{
        super.onConnectedStateChange(network, state, cause);
        
        if(state)
			stData = STAPI.getNetworkManager((ServerWorld) world).getStData(network);
		else
		{
			stData = null;
			powered = false;
		}
    }
	
	public boolean isPowered()
	{
		return powered;
	}
	
	public void setPowered(boolean _powered)
	{
		powered = _powered;
	}
	
	private boolean isPowered(@Nullable Long size)
	{
        if (size != null)
        {
            switch (mode)
            {
                case DetectorNetworkNode.MODE_UNDER:
                    return size < amount;
                case DetectorNetworkNode.MODE_EQUAL:
                    return size == amount;
                case DetectorNetworkNode.MODE_ABOVE:
                    return size > amount;
                default:
                    return false;
            }
        }
        else
        {
            if (mode == DetectorNetworkNode.MODE_UNDER && amount != 0)
                return true;

            return mode == DetectorNetworkNode.MODE_EQUAL && amount == 0;
        }
    }


	@Override
	public int getCompare()
	{
		return compare;
	}

	@Override
	public void setCompare(int _compare)
	{
		compare = _compare;
        markDirty();
	}
	
	public int getMode()
	{
        return mode;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }
    
    public long getAmount()
    {
        return amount;
    }

    public void setAmount(long amount)
    {
        this.amount = amount;
    }
    
    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag)
    {
        super.writeConfiguration(tag);

        tag.putInt("Compare", compare);
        tag.putInt("Mode", mode);
        tag.putLong("Amount", amount);
        
        tag.put("Filters", chemicalFilters.writeToNbt());

        return tag;
    }
    
    @Override
    public void readConfiguration(CompoundNBT tag)
    {
        super.readConfiguration(tag);

        if (tag.contains("Compare"))
            compare = tag.getInt("Compare");

        if (tag.contains("Mode"))
            mode = tag.getInt("Mode");

        if (tag.contains("Amount"))
            amount = tag.getLong("Amount");
        
        if(tag.contains("Filters"))
			chemicalFilters.readFromNbt(tag.getCompound("Filters"));
    }
    
    @Override
    public void setRedstoneMode(RedstoneMode redstoneMode)
    {
    	
    }
    
    public ChemicalInventory getChemicalFilters()
	{
		return chemicalFilters;
	}

}
