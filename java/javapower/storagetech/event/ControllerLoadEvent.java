package javapower.storagetech.event;

import com.refinedmods.refinedstorage.api.network.INetwork;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class ControllerLoadEvent extends Event
{
	private BlockPos pos;
	private ServerWorld world;
	private INetwork network;
	
	public ControllerLoadEvent(BlockPos _pos, ServerWorld _world, INetwork _network)
	{
		pos = _pos;
		world = _world;
		network = _network;
	}
	
	public ServerWorld getServerWorld()
	{
		return world;
	}
	
	public BlockPos getControllerPos()
	{
		return pos;
	}
	
	public INetwork getNetwork()
	{
		return network;
	}
}
