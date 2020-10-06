package javapower.storagetech.api;

import java.util.UUID;

import javapower.storagetech.data.StorageEnergyDiskSyncData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IItemEnergyStorageDisk extends IItemProgressBarOverlay
{
	public UUID getId(ItemStack stack);
	
	@OnlyIn(Dist.CLIENT)
	@Override
	default float getOverlayBarValue(ItemStack stack)
	{
		StorageEnergyDiskSyncData sedsd = STAPI.STORAGE_DISK_SYNC.getData(getId(stack));
		if(sedsd != null)
		{
			int capa = sedsd.getCapacity();
			
			if(capa > 0)
				return sedsd.getStored()/(float)capa;
		}
		
		return -0.1f;
	}
}
