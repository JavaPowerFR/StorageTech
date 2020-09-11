package javapower.storagetech.api;

public interface IEnergyStorageNode
{
	/**
    * Returns the maximum amount of energy that can be stored.
    */
	public long getCapacity();
	
	/**
    * Returns the maximum amount of energy that can be transfer.
    */
	public long getIOCapacity();
	
	/**
    * Returns the amount of energy currently stored.
    */
    public long getEnergyStored();
    
    /**
     * Returns the energy space.
     */
    public default long getEnergySpace() {return getCapacity() - getEnergyStored();}
    
    /**
     * Returns the can extract.
     */
    public default long getEnergyCanExtract() {return Math.min(getEnergyStored(), getIOCapacity());}
    
    /**
     * Returns the can insert.
     */
    public default long getEnergyCanInsert() {return Math.min(getEnergySpace(), getIOCapacity());}
	
	/**
    * Adds energy to the storage. Returns quantity of energy that was accepted.
    *
    * @param maxReceive
    *            Maximum amount of energy to be inserted.
    * @param simulate
    *            If TRUE, the insertion will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
    */
	public long receiveEnergy(long maxReceive, boolean simulate);

    /**
    * Removes energy from the storage. Returns quantity of energy that was removed.
    *
    * @param maxExtract
    *            Maximum amount of energy to be extracted.
    * @param simulate
    *            If TRUE, the extraction will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
    */
    public long extractEnergy(long maxExtract, boolean simulate);
}
