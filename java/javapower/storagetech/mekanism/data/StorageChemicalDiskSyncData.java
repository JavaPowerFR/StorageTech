package javapower.storagetech.mekanism.data;

public class StorageChemicalDiskSyncData
{
	private final long stored;
	private final long capacity;

    public StorageChemicalDiskSyncData(long stored, long capacity)
    {
        this.stored = stored;
        this.capacity = capacity;
    }
    
    public long getStored()
    {
        return stored;
    }
    
    public long getCapacity()
    {
        return capacity;
    }
}
