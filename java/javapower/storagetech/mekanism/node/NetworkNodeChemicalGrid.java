package javapower.storagetech.mekanism.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.block.BlockChemicalGrid;
import javapower.storagetech.mekanism.grid.ChemicalGrid;
import javapower.storagetech.mekanism.item.ItemChemicalFilter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeChemicalGrid extends NetworkNode 
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockChemicalGrid.raw_name);
	
    //public static final String NBT_TAB_SELECTED = "TabSelected";
	
	private final BaseItemHandler filters = new BaseItemHandler(4)
	        .addValidator((stack) -> stack != null && stack.getItem() instanceof ItemChemicalFilter)
	        .addListener(new NetworkNodeInventoryListener(this))
	        .addListener((handler, slot, reading) ->
	        {
	            if (!world.isRemote && !reading)
	                    WorldUtils.updateBlock(world, pos);
	        });
	
	private STData stData = null;
	private ChemicalGrid grid;
	
	public NetworkNodeChemicalGrid(World world, BlockPos pos)
	{
		super(world, pos);
		grid = new ChemicalGrid(world, stData);
	}

	@Override
	public int getEnergyUsage()
	{
		return RS.SERVER_CONFIG.getGrid().getFluidGridUsage();
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
			
		}
	}
	
	@Override
	protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
	{
		super.onConnectedStateChange(network, state, cause);
		
		if(!world.isRemote && !world.isAirBlock(pos))
			world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BlockChemicalGrid.CONNECTED, Boolean.valueOf(state)), 3);
		
		if(state)
		{
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).getMekanismData().putChemicalViewListener(grid);
			stData = STAPI.getNetworkManager((ServerWorld) world).getStData(network);
		}
		else
		{
			STAPI.getNetworkManager((ServerWorld) world).getStData(network).getMekanismData().removeChemicalViewListener(grid);
			stData = null;
		}
	}
	
	@Override
    public CompoundNBT write(CompoundNBT tag)
	{
        super.write(tag);
        
        StackUtils.writeItems(filters, 0, tag);
        //tag.putInt(NBT_TAB_SELECTED, tabSelected);
        grid.write(tag);
        return tag;
    }
	
	@Override
    public void read(CompoundNBT tag)
	{
        super.read(tag);
        
        StackUtils.readItems(filters, 0, tag);
        
        //if (tag.contains(NBT_TAB_SELECTED))
        //    tabSelected = tag.getInt(NBT_TAB_SELECTED);
        grid.read(tag);
    }
	
	@Override
	public CompoundNBT writeConfiguration(CompoundNBT tag)
	{
		super.writeConfiguration(tag);
		
		//tag.putInt(NBT_SORTING_DIRECTION, sortingDirection);
		grid.writeConfiguration(tag);
		return tag;
	}
	
	@Override
	public void readConfiguration(CompoundNBT tag)
	{
		super.readConfiguration(tag);
		
		//if (tag.contains(NBT_SORTING_DIRECTION))
        //    sortingDirection = tag.getInt(NBT_SORTING_DIRECTION);
		grid.readConfiguration(tag);
	}

	public int getChemicalViewType()
	{
		return grid.getChemicalViewType();
	}

	public void setChemicalViewType(int v)
	{
		grid.setChemicalViewType(v);
		markDirty();
	}

	public int getSortingDirection()
	{
		return grid.getSortingDirection();
	}

	public void setSortingDirection(int v)
	{
		grid.setSortingDirection(v);
		markDirty();
	}

	public int getSortingType()
	{
		return grid.getSortingType();
	}

	public void setSortingType(int v)
	{
		grid.setSortingType(v);
		markDirty();
	}

	public int getSearchBoxMode()
	{
		return grid.getSearchBoxMode();
	}

	public void setSearchBoxMode(int v)
	{
		grid.setSearchBoxMode(v);
		markDirty();
	}

	public int getSize()
	{
		return grid.getSize();
	}

	public void setSize(int v)
	{
		grid.setSize(v);
		markDirty();
	}
	
	public ChemicalGrid getGrid()
	{
		return grid;
	}

	public String getSearch()
	{
		return grid.getSearch();
	}

	public void setSearch(String v)
	{
		grid.setSearch(v);
		markDirty();
	}

	public IItemHandler getFilter()
	{
		return filters;
	}
	
	public STData getStData()
	{
		return stData;
	}

	public int getTab()
	{
		return grid.getTabId();
	}

	public void setTab(int v)
	{
		grid.setTabId(v);
		markDirty();
	}
}
