package javapower.storagetech.proxy;

import java.lang.reflect.Field;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.gui.GuiHandler;
import javapower.storagetech.item.STItems;
import javapower.storagetech.message.ClientHandlerTileSync;
import javapower.storagetech.message.NetworkTileSync;
import javapower.storagetech.message.ServerHandlerTileSync;
import javapower.storagetech.util.RegisterUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
	public static MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
	
	public static SimpleNetworkWrapper network_TileSynchroniser = NetworkRegistry.INSTANCE.newSimpleChannel(StorageTech.MODID+"1");
			
    public void preInit(FMLPreInitializationEvent e)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    	//StorageTechBlocks.registerTileEntitys();
    	
    	//TilesEntity
    	Field[] fields1 = STBlocks.class.getFields();
    	RegisterUtils.RegisterTilesEntity(fields1);
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(StorageTech.INSTANCE, new GuiHandler());
    	
    	network_TileSynchroniser.registerMessage(ServerHandlerTileSync.class, NetworkTileSync.class, 0, Side.SERVER);
    	network_TileSynchroniser.registerMessage(ClientHandlerTileSync.class, NetworkTileSync.class, 1, Side.CLIENT);
    }
	
    public void init(FMLInitializationEvent e)
    {
    	//Recipe.register();
    }
    
    public void postInit(FMLPostInitializationEvent e)
    {
    	
    }
    
    public void load(FMLLoadEvent e)
    {
    	
    }
    
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
    	//Blocks
    	Field[] fields1 = STBlocks.class.getFields();
    	RegisterUtils.RegisterBlocks(fields1, event.getRegistry());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
    	//Items
    	Field[] fields0 = STItems.class.getFields();
    	RegisterUtils.RegisterItems(fields0, event.getRegistry());
    	
    	//Blocks
    	Field[] fields1 = STBlocks.class.getFields();
    	RegisterUtils.RegisterItems(fields1, event.getRegistry());
    }
}
