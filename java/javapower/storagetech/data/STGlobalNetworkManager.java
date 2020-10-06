package javapower.storagetech.data;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javapower.storagetech.core.StorageTech;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class STGlobalNetworkManager extends WorldSavedData
{
	public static final String NAME = "storagetech_global";
	//private final World world;
	
	private final ConcurrentHashMap<UUID, EnergyDisk> energyDisks = new ConcurrentHashMap<>();
	
	private final javapower.storagetech.mekanism.data.MKGlobalNetworkManager mekanisum_manager;
	
	public STGlobalNetworkManager(String name, World _world)
	{
		super(name);
		//world = _world;
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			mekanisum_manager = new javapower.storagetech.mekanism.data.MKGlobalNetworkManager(this);
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
		
		return nbt;
	}
	
	public Collection<EnergyDisk> allEnergyDisks()
    {
        return energyDisks.values();
    }
	
	public void markForSaving()
    {
        markDirty();
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
    
    public javapower.storagetech.mekanism.data.MKGlobalNetworkManager getGlobalMekanisumManager()
    {
		return mekanisum_manager;
	}

}
