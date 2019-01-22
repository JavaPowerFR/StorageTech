/***************************************************
*						 ^
* 						/ \
* 					   /   \
*                     /  |  \
*                    /   .   \
*                   ___________
*                   
*   			[ W A R N I N G ! ]
*
****************************************************
*
*	All class belonging to the mod Storage Tech is developed by Cyril GENIN (Java_Power).
*	All rights reserved to Cyril GENIN.
*	it is strictly forbidden to copy or recopy!
*	These rules apply to all class, scripts, textures, configs and all file types of this project.
*
*		author: Cyril GENIN (Java_Power)
*		website: http://lithimz.fr/
*		email: cyril@famille-genin.fr
*		creation date: 04/08/2017 (dd/mm/yyyy)
*		creat at: Montigny Le Bretonneux France
*		last modification: 22/01/2019 (dd/mm/yyyy)
*		comment: RAS
*		
***************************************************/
package javapower.storagetech.core;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = StorageTech.MODID, version = StorageTech.VERSION, dependencies = "required-after:refinedstorage")
public class StorageTech
{
    public static final String MODID = "storagetech";
    public static final String VERSION = "5.0";
    
    @Instance
	public static StorageTech INSTANCE;
    
    @SidedProxy(clientSide = "javapower.storagetech.proxy.ClientProxy", serverSide = "javapower.storagetech.proxy.CommonProxy")
    public static CommonProxy proxy;
    
	public static CreativeTabs creativeTab = new CreativeTabs(MODID)
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(STBlocks.block_diskWorkbench);
		}
	};
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        proxy.preInit(e);
        Config.init(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
    	proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
    	proxy.postInit(e);
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent e)
    {
    	e.registerServerCommand(new CommandGiveMemory());
    }
}
