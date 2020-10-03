package javapower.storagetech.mekanism.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.packet.PacketStorageChemicalDiskSizeRequest;

public class StorageChemicalDiskSync
{
	private static final int THROTTLE_MS = 500;

    private final Map<UUID, StorageChemicalDiskSyncData> data = new HashMap<>();
    private final Map<UUID, Long> syncTime = new HashMap<>();
    
    public StorageChemicalDiskSyncData getData(UUID id)
    {
    	if(id == null)
    		return null;
        return data.get(id);
    }

    public void setData(UUID id, StorageChemicalDiskSyncData data)
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
        		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStorageChemicalDiskSizeRequest(id));
        	}
        	catch(NullPointerException e)
        	{
        		
        	}
            syncTime.put(id, System.currentTimeMillis());
        }
    }
}
