package javapower.storagetech.mekanism.data;

import java.util.UUID;

import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.nbt.CompoundNBT;

public class VirtualChemicalDisk extends ChemicalDisk
{
	private IChemicalHandler handler = null;
	
	public VirtualChemicalDisk()
	{
		super(UUID.randomUUID(), 0);
	}
	
	public VirtualChemicalDisk(UUID _id)
	{
		super(_id, 0);
	}
	
	@Override
	public void writeToNBT(CompoundNBT nbt)
	{
		nbt.putUniqueId("Id", id);
	}
	
	public static VirtualChemicalDisk readFromNBT(CompoundNBT nbt)
	{
		if(nbt.hasUniqueId("Id"))
		{
			VirtualChemicalDisk disk = new VirtualChemicalDisk(nbt.getUniqueId("Id"));
			return disk;
		}
		return null;
	}
	
	@Override
	public long getAmount()
	{
		/*if(handler != null)
			return handler.*/
		return 0;
	}
	
	
}
