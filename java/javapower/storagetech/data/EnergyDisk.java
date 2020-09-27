package javapower.storagetech.data;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyDisk
{
	public final UUID id;
	public final int capacity;
	public final int io_capacity;
	
	protected int energy_stored;
	
	public EnergyDisk(UUID _id, int _capacity, int _io_capacity)
	{
		id = _id;
		capacity = _capacity;
		io_capacity = _io_capacity;
		energy_stored = 0;
	}
	
	public void writeToNBT(CompoundNBT nbt)
	{
		nbt.putUniqueId("Id", id);
		nbt.putInt("Capacity", capacity);
		nbt.putInt("Io", io_capacity);
		nbt.putInt("Stored", energy_stored);
	}
	
	public static EnergyDisk readFromNBT(CompoundNBT nbt)
	{
		if(nbt.hasUniqueId("Id"))
		{
			EnergyDisk disk = new EnergyDisk(nbt.getUniqueId("Id"), nbt.getInt("Capacity"), nbt.getInt("Io"));
			disk.energy_stored = nbt.getInt("Stored");
			return disk;
		}
		return null;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public int getIOCapacity()
	{
		return io_capacity;
	}
	
    public int getEnergyStored()
    {
    	return energy_stored;
    }
    
    public int getEnergySpace()
    {
    	return capacity - energy_stored;
    }
    
    public int getEnergyCanExtract()
    {
    	return Math.min(energy_stored, io_capacity);
    }
    
    public int getEnergyCanInsert()
    {
    	return Math.min(capacity - energy_stored, io_capacity);
    }
	
	/**
    * Adds energy to the storage. Returns quantity of energy that was accepted.
    *
    * @param maxReceive
    *            Maximum amount of energy to be inserted.
    * @param simulate
    *            If TRUE, the insertion will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
    */
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		int accepted = Math.min(Math.min(capacity - energy_stored, maxReceive), io_capacity);
		if(!simulate)
			energy_stored += accepted;
		return accepted;
	}

    /**
    * Removes energy from the storage. Returns quantity of energy that was removed.
    *
    * @param maxExtract
    *            Maximum amount of energy to be extracted.
    * @param simulate
    *            If TRUE, the extraction will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
    */
    public int extractEnergy(int maxExtract, boolean simulate)
    {
    	int energyExtracteble = Math.min(Math.min(energy_stored, maxExtract), io_capacity);
    	if(!simulate)
    		energy_stored -= energyExtracteble;
    	return energyExtracteble;
    }
    
    public boolean tryToChargeEnergyStorage(IEnergyStorage energyStorage)
    {
    	if(energyStorage != null && energy_stored > 0 && energyStorage.canReceive())
    	{
    		energy_stored -= energyStorage.receiveEnergy(Math.min(energy_stored, io_capacity), false); 
    		return true;
    	}
    	return false;
    }
	
    public boolean tryToDischargeEnergyStorage(IEnergyStorage energyStorage)
    {
    	int space = getEnergyCanInsert();
    	
    	if(energyStorage != null && space > 0 && energyStorage.canExtract())
    	{
    		energy_stored += energyStorage.extractEnergy(space, false);
    		return true;
    	}
    	return false;
    }
	
}
