package javapower.storagetech.proxy;

import java.lang.reflect.Field;
import java.util.UUID;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.ConfigClient;
import javapower.storagetech.item.STItems;
import javapower.storagetech.util.RegisterUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
	public static Minecraft minecraft = Minecraft.getMinecraft();
	
	@Override
	public void preInit(FMLPreInitializationEvent e)
    {
		ConfigClient.init(e);
		super.preInit(e);
		
		//Field[] fields1 = STBlocks.class.getFields();
		//RegisterUtils.RegisterTilesEntitySR(fields1);
    }
	
	@Override
    public void init(FMLInitializationEvent e)
    {
		ResourceLocationRegister.register();
		
    	super.init(e);
    }
    
	@Override
    public void postInit(FMLPostInitializationEvent e)
    {
    	super.postInit(e);
    }
	
	@SubscribeEvent
    public void registerModels(ModelRegistryEvent e)
	{
		//Items
    	Field[] fields0 = STItems.class.getFields();
    	RegisterUtils.RegisterIRender(fields0);
    	
    	//Blocks
    	Field[] fields1 = STBlocks.class.getFields();
    	RegisterUtils.RegisterIRender(fields1);
    	//ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
	}
	
	@SubscribeEvent
    public void drawTooltip(RenderTooltipEvent.PostBackground event)
    {
		if(ConfigClient.overlayEnable)
		{
	    	ItemStack itemstack = event.getStack();
	    	if(itemstack != null && itemstack.getItem() instanceof IStorageDiskProvider)
	    	{
	    		if(!((IStorageDiskProvider)itemstack.getItem()).isValid(itemstack))
	    			return;
	    		UUID uuid = ((IStorageDiskProvider)itemstack.getItem()).getId(itemstack);
	    		if(uuid == null)
	    			return;
	    		IStorageDiskSyncData data = API.instance().getStorageDiskSync().getData(uuid);
	    		if(data != null)
	    		{
	    			if(data.getCapacity() != -1)
	    			{
		    			float size = data.getStored()/(float)data.getCapacity();
		    			int color = size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded;
		    			minecraft.renderEngine.bindTexture(ResourceLocationRegister.overlay);
		    			GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
		    			Gui.drawRect(event.getX(), event.getY() - 18, event.getX() + 60, event.getY() - 10, 0xff444444);
		    			if(size > 0)
		    				Gui.drawRect(event.getX(), event.getY() - 18, event.getX() + (int)(60*size), event.getY() - 10, color);
		    			minecraft.fontRenderer.drawString(((int) (size*100))+"%", event.getX() + 62, event.getY() - 17, color);
	    			}
	    			else
	    			{
	    				minecraft.renderEngine.bindTexture(ResourceLocationRegister.overlay);
	    				GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
		    			minecraft.fontRenderer.drawString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded);
	    			}
	    		}
	    	}
		}
    }
}
