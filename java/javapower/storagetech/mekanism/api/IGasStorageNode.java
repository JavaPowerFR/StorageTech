package javapower.storagetech.mekanism.api;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;

public interface IGasStorageNode
{
	/**
    * Returns the maximum amount of gas that can be stored.
    */
	public long getCapacity();
		
	/**
    * Returns the amount of gas currently stored.
    */
    public long getAmount();
    
    /**
     * Returns the gas space.
     */
    public default long getSpace() {return getCapacity() - getAmount();}
    
    public GasStack extractChemical(GasStack stack, Action action);
    
	public GasStack insertChemical(GasStack stack, Action action);
}
