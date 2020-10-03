package javapower.storagetech.setup;

import java.util.ArrayList;
import java.util.List;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.ControllerBlock;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.CommonConfig;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.event.ControllerLoadEvent;
import javapower.storagetech.item.STItems;
import javapower.storagetech.recipe.StorageTechRecipeCell;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.PartValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonSetup
{
	public static final SpecialRecipeSerializer<StorageTechRecipeCell> CRAFTING_STORAGETECH_CELL = IRecipeSerializer.register("storagetech:recipe_cell", new SpecialRecipeSerializer<>(StorageTechRecipeCell::new));
	
	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent e)
	{
		CommonConfig.loadConfig();
		
		STBlocks.registerNodes(API.instance().getNetworkNodeRegistry());
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			javapower.storagetech.mekanism.block.MKBlocks.registerNodes(API.instance().getNetworkNodeRegistry());
		
		List<PartValue> item_part = new ArrayList<PartValue>();
		List<PartValue> fluid_part = new ArrayList<PartValue>();
		
		for(Item item : ForgeRegistries.ITEMS.getValues())
		{
			String path = item.getRegistryName().getPath();
			if(path.contains("_fluid_storage_part"))
			{
				String value = path.substring(0, path.indexOf('_'));
				int sufix = "kKmMgG".indexOf(value.charAt(value.length()-1))/2;
				long multi = 0;
				if(sufix != -1)
				{
					if(sufix == 0)
						multi = 1000;
					else if(sufix == 1)
						multi = 1000_000;
					else if(sufix == 2)
						multi = 1000_000_000;
				}
				
				try
				{
					long part_value = Integer.parseInt(value.substring(0, value.length()-1)) * multi;
					fluid_part.add(new PartValue(item, part_value));
				}
				catch (Exception e2)
				{
					
				}
			}
			else if(path.contains("_storage_part") && !path.contains("chemical"))
			{
				String value = path.substring(0, path.indexOf('_'));
				int sufix = "kKmMgG".indexOf(value.charAt(value.length()-1))/2;
				long multi = 0;
				if(sufix != -1)
				{
					if(sufix == 0)
						multi = 1000;
					else if(sufix == 1)
						multi = 1000_000;
					else if(sufix == 2)
						multi = 1000_000_000;
				}
				
				try
				{
					long part_value = Integer.parseInt(value.substring(0, value.length()-1)) * multi;
					item_part.add(new PartValue(item, part_value));
				}
				catch (Exception e2)
				{
					
				}
			}
		}
		
		DiskUtils.updateValidParts(item_part, fluid_part);
	}

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> register)
    {
    	register.getRegistry().register(CRAFTING_STORAGETECH_CELL);
    }
    
    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> register)
    {
    	STBlocks.registerBlocks(register.getRegistry());
    	
    	if(StorageTech.MOD_MEKANISM_IS_LOADED)
    		javapower.storagetech.mekanism.block.MKBlocks.registerBlocks(register.getRegistry());
    }
    
    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> register)
    {
    	STBlocks.registerItems(register.getRegistry());
    	STItems.registerItems(register.getRegistry());
    	
    	if(StorageTech.MOD_MEKANISM_IS_LOADED)
    	{
    		javapower.storagetech.mekanism.block.MKBlocks.registerItems(register.getRegistry());
    		javapower.storagetech.mekanism.item.MKItems.registerItems(register.getRegistry());
    		
    	}
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> register)
    {
    	STBlocks.registerTiles(register.getRegistry());
    	
    	if(StorageTech.MOD_MEKANISM_IS_LOADED)
    		javapower.storagetech.mekanism.block.MKBlocks.registerTiles(register.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> register)
    {
    	STBlocks.registerContainers(register.getRegistry());
    	
    	if(StorageTech.MOD_MEKANISM_IS_LOADED)
    		javapower.storagetech.mekanism.block.MKBlocks.registerContainers(register.getRegistry());
    }
    
    public static class Events
    {
    	@SubscribeEvent
    	public void onBlockPlaced(net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent event)
    	{
    		if(event.getPlacedBlock() != null && event.getPlacedBlock().getBlock() instanceof ControllerBlock)
    		{
    			if(event.getWorld() instanceof ServerWorld)
    			{
    				INetwork network = API.instance().getNetworkManager((ServerWorld) event.getWorld()).getNetwork(event.getPos());
    				MinecraftForge.EVENT_BUS.post(new ControllerLoadEvent(event.getPos(), (ServerWorld) event.getWorld(), network));
    			}
    		}
    	}
    	
    	@SubscribeEvent
    	public void onChunkLoaded(net.minecraftforge.event.world.ChunkEvent.Load event)
    	{
    		if(event.getWorld() instanceof ServerWorld)
    		{
	    		for(BlockPos pos : event.getChunk().getTileEntitiesPos())
	    		{
	    			BlockState bs = event.getChunk().getBlockState(pos);
	    			if(bs != null && bs.getBlock() instanceof ControllerBlock)
	    			{
	    				INetwork network = API.instance().getNetworkManager((ServerWorld) event.getWorld()).getNetwork(pos);
	    				MinecraftForge.EVENT_BUS.post(new ControllerLoadEvent(pos, (ServerWorld) event.getWorld(), network));
	    			}
	    		}
    		}
    	}
    	
    	@SubscribeEvent
    	public void onControllerLoad(ControllerLoadEvent event)
    	{
    		
    	}
    }
}
