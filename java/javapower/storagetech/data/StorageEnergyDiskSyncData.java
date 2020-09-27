package javapower.storagetech.data;

public class StorageEnergyDiskSyncData
{
	private final int stored;
	private final int capacity;
	private final int io_capacity;

    public StorageEnergyDiskSyncData(int stored, int capacity, int io_capacity)
    {
        this.stored = stored;
        this.capacity = capacity;
        this.io_capacity = io_capacity;
    }
    
    public int getStored()
    {
        return stored;
    }
    
    public int getCapacity()
    {
        return capacity;
    }
    
    public int getIoCapacity()
    {
		return io_capacity;
	}
}
