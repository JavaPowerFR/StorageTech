package javapower.storagetech.api;

import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;

public interface IDiskStateUpdater
{
	public DiskState[] getDiskState();
	
	public void setClientDiskState(DiskState[] diskState);
}
