package javapower.storagetech.util;

import javapower.storagetech.eventio.IEventVoid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyBuffer implements IEnergyStorage
{
	public int energy;
    public int capacity;
    public int maxReceive;
    public int maxExtract;
    public IEventVoid eventchange = null;

    public EnergyBuffer(int capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyBuffer(int capacity, int maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyBuffer(int capacity, int maxReceive, int maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyBuffer(int capacity, int maxReceive, int maxExtract, int energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
        {
        	if(eventchange != null && energyReceived > 0)
        		eventchange.event();
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
        {
        	if(eventchange != null && energyExtracted > 0)
        		eventchange.event();
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return capacity;
    }

    @Override
    public boolean canExtract()
    {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive()
    {
        return this.maxReceive > 0;
    }
    
    public void WriteToNBT(CompoundNBT nbt)
    {
    	nbt.putInt("energy", energy);
    }
    
    public boolean ReadFromNBT(CompoundNBT nbt)
    {
    	if(nbt.contains("energy"))
    	{
    		energy = nbt.getInt("energy");
    		return true;
    	}
    	return false;
    }
}