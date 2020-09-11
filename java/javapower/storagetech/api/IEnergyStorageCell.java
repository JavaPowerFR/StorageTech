package javapower.storagetech.api;

import net.minecraft.item.ItemStack;

public interface IEnergyStorageCell
{
	/**
    * Returns the maximum amount of energy that can be stored.
    */
	public int getCapacity(ItemStack stack);
	
	/**
    * Returns the maximum amount of energy that can be transfer.
    */
	public int getIOCapacity(ItemStack stack);
	
	/**
    * Returns the amount of energy currently stored.
    */
    public int getEnergyStored(ItemStack stack);
	
	/**
    * Adds energy to the storage. Returns quantity of energy that was accepted.
    *
    * @param maxReceive
    *            Maximum amount of energy to be inserted.
    * @param simulate
    *            If TRUE, the insertion will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
    */
	public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate);

    /**
    * Removes energy from the storage. Returns quantity of energy that was removed.
    *
    * @param maxExtract
    *            Maximum amount of energy to be extracted.
    * @param simulate
    *            If TRUE, the extraction will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
    */
    public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate);

	public ItemStack[] getParts(ItemStack stack);
}
