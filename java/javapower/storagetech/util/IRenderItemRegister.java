package javapower.storagetech.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRenderItemRegister
{
	@SideOnly(Side.CLIENT)
	public ItemRenderCast[] getItemsRender();
}
