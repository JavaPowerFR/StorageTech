package javapower.storagetech.data;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.refinedmods.refinedstorage.api.network.INetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class STNetworkManager extends WorldSavedData
{
	public static final String NAME = "storagetech_networks";
	private final World world;
	
	private final ConcurrentHashMap<BlockPos, STData> networks = new ConcurrentHashMap<>();
	
	public STNetworkManager(String name, World _world)
	{
		super(name);
		world = _world;
	}

	@Override
	public void read(CompoundNBT nbt)
	{
		if(nbt.contains("networks"))
		{
			ListNBT networksTag = nbt.getList("networks", Constants.NBT.TAG_COMPOUND);
			
			networks.clear();
			
			for (int i = 0; i < networksTag.size(); ++i)
			{
                CompoundNBT stTag = networksTag.getCompound(i);
                
                CompoundNBT data = stTag.getCompound("data");
                BlockPos pos = BlockPos.fromLong(stTag.getLong("pos"));
                STData stdata = new STData(world, pos, this);
                stdata.readFromNbt(data);
                
                networks.put(pos, stdata);
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		ListNBT list = new ListNBT();
		
		for(STData stdata : all())
		{
			CompoundNBT stTag = new CompoundNBT();
			
			stTag.putLong("pos", stdata.getControllerPos().toLong());
			stTag.put("data", stdata.writeToNbt(new CompoundNBT()));
			
			list.add(stTag);
		}
		
		nbt.put("networks", list);
		return nbt;
	}
	
    public Collection<STData> all()
    {
        return networks.values();
    }
    
    public void markForSaving()
    {
        markDirty();
    }
    
    public STData getStData(INetwork nw)
    {
    	STData data = networks.get(nw.getPosition());
    	if(data != null)
    		return data;
    	
    	data = new STData(world, nw.getPosition(), this);
    	networks.put(nw.getPosition(), data);
    	markForSaving();
    	return data;
    }
	
}
