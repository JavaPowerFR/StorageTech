package javapower.storagetech.data;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.refinedmods.refinedstorage.api.network.INetwork;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.data.MKNetworkManager;
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
	
	private final ConcurrentHashMap<UUID, EnergyDisk> energyDisks = new ConcurrentHashMap<>();
	
	private final MKNetworkManager mekanisum_manager;
	
	public STNetworkManager(String name, World _world)
	{
		super(name);
		world = _world;
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			mekanisum_manager = new MKNetworkManager(this);
		else
			mekanisum_manager = null;
	}

	@Override
	public void read(CompoundNBT nbt)
	{
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			mekanisum_manager.read(nbt);
		
		if(nbt.contains("energydisks"))
		{
			ListNBT disksTag = nbt.getList("energydisks", Constants.NBT.TAG_COMPOUND);
			
			energyDisks.clear();
			
			for (int i = 0; i < disksTag.size(); ++i)
			{
				EnergyDisk disk = EnergyDisk.readFromNBT(disksTag.getCompound(i));
				if(disk != null)
				{
					energyDisks.put(disk.id, disk);
				}
			}
		}
		
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
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			mekanisum_manager.write(nbt);
		
		// write energy disk
		ListNBT list_energyDisks = new ListNBT();
		
		for(EnergyDisk disk : allEnergyDisks())
		{
			if(disk != null)
			{
				CompoundNBT stTag = new CompoundNBT();
				disk.writeToNBT(stTag);
				list_energyDisks.add(stTag);
			}
		}
				
		nbt.put("energydisks", list_energyDisks);
				
		// write network
		ListNBT list_network = new ListNBT();
		
		for(STData stdata : allNetworks())
		{
			CompoundNBT stTag = new CompoundNBT();
			
			stTag.putLong("pos", stdata.getControllerPos().toLong());
			stTag.put("data", stdata.writeToNbt(new CompoundNBT()));
			
			list_network.add(stTag);
		}
		
		nbt.put("networks", list_network);
		
		return nbt;
	}
	
	public Collection<STData> allNetworks()
    {
        return networks.values();
    }
	
	public Collection<EnergyDisk> allEnergyDisks()
    {
        return energyDisks.values();
    }
	
	public MKNetworkManager getMekanisumManager()
	{
		return mekanisum_manager;
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
    
    public EnergyDisk getEnergyDisk(UUID uuid)
    {
    	if(uuid == null)
    		return null;
    	
    	return energyDisks.get(uuid);
    }
    
    public EnergyDisk removeEnergyDisk(UUID uuid)
    {
    	markForSaving();
    	return energyDisks.remove(uuid);
    }
    
    public EnergyDisk createEnergyDisk(UUID _uuid, int _capacity, int _io_capacity)
    {
    	EnergyDisk disk = new EnergyDisk(_uuid, _capacity, _io_capacity);
    	energyDisks.put(disk.id, disk);
    	markForSaving();
    	return disk;
    }
	
}
