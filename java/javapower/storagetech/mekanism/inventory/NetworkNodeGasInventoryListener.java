package javapower.storagetech.mekanism.inventory;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener;

public class NetworkNodeGasInventoryListener implements InventoryListener<GasInventory>
{
	private final INetworkNode node;
	
	public NetworkNodeGasInventoryListener(INetworkNode _node)
	{
		node = _node;
	}

	@Override
	public void onChanged(GasInventory handler, int slot, boolean reading)
	{
		if (!reading)
		{
			node.markDirty();
		}
	}

}
