package javapower.storagetech.data;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.api.network.INetwork;

import javapower.storagetech.api.IEnergyStorageNode;
import javapower.storagetech.core.StorageTech;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class STData
{
	private World world;
	private BlockPos controllerPos;
	
	private STNetworkManager parent;
	
	private List<IEnergyStorageNode> energyStorages;
	
	private javapower.storagetech.mekanism.data.STMKData mekanism_data;
	
	private final INetwork rs_network;
	
	public STData(World _world, INetwork nw, STNetworkManager _parent)
	{
		rs_network = nw;
		world = _world;
		controllerPos = nw.getPosition();
		parent = _parent;
		energyStorages = new ArrayList<>();
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			mekanism_data = new javapower.storagetech.mekanism.data.STMKData(this);
	}
	
	public INetwork getRsNetwork()
	{
		return rs_network;
	}
	
	public void markForSaving()
	{
		parent.markForSaving();
	}
	
	public javapower.storagetech.mekanism.data.STMKData getMekanismData()
	{
		return mekanism_data;
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

	public void updateView()
	{
		
	}
}
