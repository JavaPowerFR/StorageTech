package javapower.storagetech.node;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;

import javapower.storagetech.block.BlockStructureConstructor;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import javapower.storagetech.util.SCElement;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeStructureConstructor extends NetworkNode
{
	private TileEntityStructureConstructor tile;
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockStructureConstructor.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 20;
	public int tickUpdateDelay = TICK_UPDATE_DELAY;
	public int tickUpdate;
	
	public List<SCElement> elements = new ArrayList<SCElement>();
	public static final int MIN_IJ = -8;
	public static final int MIN_K = 1;
	public static final int MAX_IJ = 8;
	public static final int MAX_K = 16;
	
	private boolean processing_force_step = false;
	private boolean working = false;
	private int processIndex = 0;
	
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
	
	public NetworkNodeStructureConstructor(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Override
	public int getEnergyUsage()
	{
		return RS.SERVER_CONFIG.getConstructor().getUsage();
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
		
		if(tile == null)
		{
			tile = (TileEntityStructureConstructor) world.getTileEntity(pos);
		}
		
		if(!world.isRemote && canUpdate())
		{
			if(tickUpdate >= tickUpdateDelay)
			{
				tickUpdate = 0;
				//tick update ->>
				
				if(working)
				{
					if(processIndex < elements.size())
					{
						SCElement el = elements.get(processIndex);
						if(el.isLoaded)
						{
							if(el.worldProcess(world, network) || processing_force_step)
								++processIndex;
						}
						else
						{
							el.load(tile);
							++processIndex;
						}
					}
					else
					{
						processIndex = 0;
						markDirty();
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
		world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BlockStructureConstructor.CONNECTED, Boolean.valueOf(state)), 3);
	}
	
	@Override
    public CompoundNBT write(CompoundNBT tag)
	{
        super.write(tag);

        StackUtils.writeItems(upgrades, 0, tag);
        
        tag.putBoolean("FroceStep", processing_force_step);
        tag.putBoolean("Working", working);
        tag.putInt("Pindex", processIndex);
        
        ListNBT list_elements = new ListNBT();
        for (SCElement element : elements)
        {
        	if(element != null)
        	{
	        	CompoundNBT nbt_element = new CompoundNBT();
	        	element.writeToNBT(nbt_element);
	        	list_elements.add(nbt_element);
        	}
        }
        tag.put("Elements", list_elements);
        
        return tag;
    }
	
	@Override
    public void read(CompoundNBT tag)
	{
        super.read(tag);

        StackUtils.readItems(upgrades, 0, tag);
        
        processing_force_step = tag.getBoolean("FroceStep");
        working = tag.getBoolean("Working");
        processIndex = tag.getInt("Pindex");
        
        ListNBT list_elements = tag.getList("Elements", Constants.NBT.TAG_COMPOUND);
        elements.clear();
        
        for (int i = 0; i < list_elements.size(); ++i)
        {
        	CompoundNBT nbt_element = list_elements.getCompound(i);
        	SCElement element = SCElement.getFromNBT(nbt_element);
        	if(element != null)
        		elements.add(element);
        }
        
    }

	public IItemHandler getUpgrades()
	{
		return upgrades;
	}
	
	public boolean getProcessingMode()
	{
		return processing_force_step;
	}
	
	public void setProcessingMode(boolean value)
	{
		processing_force_step = value;
		markDirty();
	}

	public boolean isWorking()
	{
		return working;
	}
	
	public void setWorking(boolean value)
	{
		working = value;
		markDirty();
	}
	
	public int getIndex()
	{
		return processIndex;
	}
	
	public void resetIndex()
	{
		processIndex = 0;
		markDirty();
	}

}
