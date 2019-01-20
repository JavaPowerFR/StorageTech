package javapower.storagetech.util;

import javapower.storagetech.core.StorageTech;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public class ItemRenderCast
{
	private int meta;
	private String variant;
	
	public ItemRenderCast(int _meta, String _variant)
	{
		meta = _meta;
		variant = _variant;
	}
	
	public int getMetadata()
	{
		return meta;
	}
	
	public ModelResourceLocation getModelRL(String registeryN)
	{
		return new ModelResourceLocation(StorageTech.MODID+":"+registeryN, variant);
	}
}
