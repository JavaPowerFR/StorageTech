package javapower.storagetech.mekanism.api;

import java.util.UUID;

import javapower.storagetech.api.IItemProgressBarOverlay;
import javapower.storagetech.mekanism.data.StorageChemicalDiskSyncData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IItemChemicalStorageDisk extends IItemProgressBarOverlay
{
	public UUID getId(ItemStack stack);
	
	@OnlyIn(Dist.CLIENT)
	@Override
	default float getOverlayBarValue(ItemStack stack)
	{
		StorageChemicalDiskSyncData scdsd = STMKAPI.STORAGE_DISK_SYNC.getData(getId(stack));
		if(scdsd != null)
		{
			long capa = scdsd.getCapacity();
			
			if(capa > 0)
				return scdsd.getStored()/(float)capa;
		}
		return -1;
	}
}
