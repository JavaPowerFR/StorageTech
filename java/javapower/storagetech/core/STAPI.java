package javapower.storagetech.core;

import javapower.storagetech.data.STNetworkManager;
import net.minecraft.world.server.ServerWorld;

public class STAPI
{
	public static STNetworkManager getNetworkManager(ServerWorld world)
	{
        return world.getSavedData().getOrCreate(() -> new STNetworkManager(STNetworkManager.NAME, world), STNetworkManager.NAME);
    }
}
