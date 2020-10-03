package javapower.storagetech.mekanism.api;

import java.util.List;

import javapower.storagetech.mekanism.data.ChemicalDisk;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;

public interface IChemicalStorageNode
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
    
    public ChemicalStack<?> extractChemical(ChemicalStack<?> stack, Action action);
    
	public ChemicalStack<?> insertChemical(ChemicalStack<?> stack, Action action);
	
	public List<ChemicalDisk> getDisks();
	
}
