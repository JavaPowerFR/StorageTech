package javapower.storagetech.mekanism.inventory;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener;

public class NetworkNodeChemicalInventoryListener implements InventoryListener<ChemicalInventory>
{
	private final INetworkNode node;
	
	public NetworkNodeChemicalInventoryListener(INetworkNode _node)
	{
		node = _node;
	}
	
	@Override
	public void onChanged(ChemicalInventory handler, int slot, boolean reading)
	{
		if (!reading)
		{
			node.markDirty();
		}
	}

}
