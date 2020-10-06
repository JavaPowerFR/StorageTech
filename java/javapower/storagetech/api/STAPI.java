package javapower.storagetech.api;

import javapower.storagetech.data.STGlobalNetworkManager;
import javapower.storagetech.data.STNetworkManager;
import javapower.storagetech.data.STStorageDiskSync;
import net.minecraft.world.server.ServerWorld;

public class STAPI
{
	public static final STStorageDiskSync STORAGE_DISK_SYNC = new STStorageDiskSync();
	
	public static STNetworkManager getNetworkManager(ServerWorld world)
	{
        return world.getSavedData().getOrCreate(() -> new STNetworkManager(STNetworkManager.NAME, world), STNetworkManager.NAME);
    }
	
	public static STGlobalNetworkManager getGlobalNetworkManager(ServerWorld anyWorld)
	{
		ServerWorld world = anyWorld.getServer().func_241755_D_();
		return world.getSavedData().getOrCreate(() -> new STGlobalNetworkManager(STGlobalNetworkManager.NAME, world), STGlobalNetworkManager.NAME);
	}
}
