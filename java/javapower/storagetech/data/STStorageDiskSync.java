package javapower.storagetech.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.packet.PacketStorageEnergyDiskSizeRequest;

public class STStorageDiskSync
{
	private static final int THROTTLE_MS = 500;

    private final Map<UUID, StorageEnergyDiskSyncData> data = new HashMap<>();
    private final Map<UUID, Long> syncTime = new HashMap<>();
    
    public StorageEnergyDiskSyncData getData(UUID id)
    {
    	if(id == null)
    		return null;
        return data.get(id);
    }

    public void setData(UUID id, StorageEnergyDiskSyncData data)
    {
        this.data.put(id, data);
    }
    
    public void sendRequest(UUID id)
    {
    	if(id == null)
    		return;
    	
        long lastSync = syncTime.getOrDefault(id, 0L);

        if (System.currentTimeMillis() - lastSync > THROTTLE_MS)
        {
        	try
        	{
            StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStorageEnergyDiskSizeRequest(id));
        	}
        	catch(NullPointerException e)
        	{
        		
        	}
            syncTime.put(id, System.currentTimeMillis());
        }
    }
}
