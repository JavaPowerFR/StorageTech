package javapower.storagetech.util;

import javapower.storagetech.eventio.IEventVoid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;
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
    
    public void WriteToNBT(NBTTagCompound nbt)
    {
    	nbt.setInteger("energy", energy);
    }
    
    public boolean ReadFromNBT(NBTTagCompound nbt)
    {
    	if(nbt.hasKey("energy"))
    	{
    		energy = nbt.getInteger("energy");
    		return true;
    	}
    	return false;
    }
}
