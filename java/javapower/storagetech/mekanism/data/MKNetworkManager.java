package javapower.storagetech.mekanism.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javapower.storagetech.data.STNetworkManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class MKNetworkManager
{
	private final ConcurrentHashMap<UUID, ChemicalDisk> chemicalDisks = new ConcurrentHashMap<>();
	private STNetworkManager stNetworkManager;
	
	public MKNetworkManager(STNetworkManager _stNetworkManager)
	{
		stNetworkManager = _stNetworkManager;
	}
	
	public void read(CompoundNBT nbt)
	{
		if(nbt.contains("chemicaldisks"))
		{
			ListNBT disksTag = nbt.getList("chemicaldisks", Constants.NBT.TAG_COMPOUND);
			
			chemicalDisks.clear();
			
			for (int i = 0; i < disksTag.size(); ++i)
			{
				ChemicalDisk disk = ChemicalDisk.readFromNBT(disksTag.getCompound(i));
				if(disk != null)
				{
					chemicalDisks.put(disk.id, disk);
				}
			}
		}
	}
	
	public void write(CompoundNBT nbt)
	{
		ListNBT list_chemicalDisks = new ListNBT();
		
		for(ChemicalDisk disk : chemicalDisks.values())
		{
			if(disk != null)
			{
				CompoundNBT stTag = new CompoundNBT();
				disk.writeToNBT(stTag);
				list_chemicalDisks.add(stTag);
			}
		}
		
		nbt.put("chemicaldisks", list_chemicalDisks);
	}

	public ChemicalDisk getChemicalDisk(UUID id)
	{
		if(id == null)
			return null;
		return chemicalDisks.get(id);
	}
	
	public ChemicalDisk createEnergyDisk(UUID uuid, long _capacity)
    {
		ChemicalDisk disk = new ChemicalDisk(uuid, _capacity);
		chemicalDisks.put(disk.id, disk);
    	stNetworkManager.markForSaving();
    	return disk;
    }

	public ChemicalDisk removeChemicalDisk(UUID id)
	{
		stNetworkManager.markForSaving();
		return chemicalDisks.remove(id);
	}
}
