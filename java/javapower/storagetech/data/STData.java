package javapower.storagetech.data;

import java.util.ArrayList;
import java.util.List;

import javapower.storagetech.api.IEnergyStorageNode;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class STData
{
	private World world;
	private BlockPos controllerPos;
	@SuppressWarnings("unused")
	private STNetworkManager parent;
	private List<IEnergyStorageNode> energyStorages;
	
	public STData(World _world, BlockPos _controllerPos, STNetworkManager _parent)
	{
		world = _world;
		controllerPos = _controllerPos;
		parent = _parent;
		energyStorages = new ArrayList<>();
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public BlockPos getControllerPos()
	{
		return controllerPos;
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt)
	{
		return nbt;
	}
	
	public void readFromNbt(CompoundNBT nbt)
	{
		
	}

	public void putEnergyStorageListener(IEnergyStorageNode energyStorageNode)
	{
		if(!energyStorages.contains(energyStorageNode))
			energyStorages.add(energyStorageNode);
	}

	public void removeEnergyStorageListener(IEnergyStorageNode energyStorageNode)
	{
		if(energyStorages.contains(energyStorageNode))
			energyStorages.remove(energyStorageNode);
	}
	
	public List<IEnergyStorageNode> getEnergyStorages()
	{
		return energyStorages;
	}
}
