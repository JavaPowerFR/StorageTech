package javapower.storagetech.mekanism.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javapower.storagetech.data.STNetworkManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class MKNetworkManager
{
	private final ConcurrentHashMap<UUID, GasDisk> gasDisks = new ConcurrentHashMap<>();
	private STNetworkManager stNetworkManager;
	
	public MKNetworkManager(STNetworkManager _stNetworkManager)
	{
		stNetworkManager = _stNetworkManager;
	}
	
	public void read(CompoundNBT nbt)
	{
		if(nbt.contains("gasdisks"))
		{
			ListNBT disksTag = nbt.getList("gasdisks", Constants.NBT.TAG_COMPOUND);
			
			gasDisks.clear();
			
			for (int i = 0; i < disksTag.size(); ++i)
			{
				GasDisk disk = GasDisk.readFromNBT(disksTag.getCompound(i));
				if(disk != null)
				{
					gasDisks.put(disk.id, disk);
				}
			}
		}
	}
	
	public void write(CompoundNBT nbt)
	{
		ListNBT list_gasDisks = new ListNBT();
		
		for(GasDisk disk : gasDisks.values())
		{
			if(disk != null)
			{
				CompoundNBT stTag = new CompoundNBT();
				disk.writeToNBT(stTag);
				list_gasDisks.add(stTag);
			}
		}
		
		nbt.put("gasdisks", list_gasDisks);
	}

	public GasDisk getGasDisk(UUID id)
	{
		if(id == null)
			return null;
		return gasDisks.get(id);
	}
	
	public GasDisk createEnergyDisk(UUID uuid, long _capacity)
    {
		GasDisk disk = new GasDisk(uuid, _capacity);
    	gasDisks.put(disk.id, disk);
    	stNetworkManager.markForSaving();
    	return disk;
    }

	public GasDisk removeGasDisk(UUID id)
	{
		stNetworkManager.markForSaving();
		return gasDisks.remove(id);
	}
}
