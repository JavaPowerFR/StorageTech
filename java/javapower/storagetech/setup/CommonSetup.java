package javapower.storagetech.setup;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.ControllerBlock;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.core.CommonConfig;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.event.ControllerLoadEvent;
import javapower.storagetech.item.STItems;
import javapower.storagetech.recipe.RecipeCell;
import javapower.storagetech.recipe.RecipeCustomDisk;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.EPartType;
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
	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent e)
	{
		CommonConfig.loadConfig();
		
		STBlocks.registerNodes(API.instance().getNetworkNodeRegistry());
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			javapower.storagetech.mekanism.block.MKBlocks.registerNodes(API.instance().getNetworkNodeRegistry());
		
		DiskUtils.getParts().clear();
		
		DiskUtils.getParts().add(new PartValueCustom(STItems.item_custom_storage_part, EPartType.ITEM));
		DiskUtils.getParts().add(new PartValueCustom(STItems.item_custom_fluid_storage_part, EPartType.FLUID));
		DiskUtils.getParts().add(new PartValueCustom(STItems.item_custom_energy_storage_part, EPartType.ENERGY));
		
		if(StorageTech.MOD_MEKANISM_IS_LOADED)
			DiskUtils.getParts().add(new PartValueCustom(javapower.storagetech.mekanism.item.MKItems.item_custom_chemical_storage_part, EPartType.CHEMICAL));
		
		for(Item item : ForgeRegistries.ITEMS.getValues())
		{
			String path = item.getRegistryName().getPath();
			if(path.contains("_storage_part"))
			{
				String[] args = path.split("_");
				if(args.length == 3)
				{
					int value = valueExtractor(args[0]);
					if(value > 0)
						DiskUtils.getParts().add(new PartValue(item, value, EPartType.ITEM));
					
				}
				else if(args.length == 4)
				{
					int value = valueExtractor(args[0]);
					if(value > 0)
					{
						DiskUtils.getParts().add(new PartValue(item, value, valueType(args[1])));
					}
				}
			}
		}
	}
	
	private int valueExtractor(String value)
	{
		int prevalue = 0;
		int multi = 0;
		int aftervalue = 0;
				
		for(int index = 0; index < value.length(); ++index)
		{
			char c = value.charAt(index);
			
			int value_char = "0123456789".indexOf(c);
			if(value_char != -1)
			{
				if(multi == 0)
					prevalue = prevalue*10 + value_char;
				else
					aftervalue = aftervalue*10 + value_char;
			}
			else
			{
				
				int sufix = "kKmMgG".indexOf(value.charAt(index))/2;
				if(sufix != -1)
				{
					if(sufix == 0)
						multi = 1000;
					else if(sufix == 1)
						multi = 1000_000;
					else if(sufix == 2)
						multi = 1000_000_000;
				}
			}
		}
		
		int composed = prevalue * multi;
		
		int cntZo = (""+multi).length()-1;
		cntZo -= (""+aftervalue).length();
		int afterMulti = 1;
		
		for(int z0 = 0; z0 < cntZo; ++z0)
			afterMulti *= 10;
		
		composed += aftervalue*afterMulti;
		
		return composed;
	}
	
	private EPartType valueType(String value)
	{
		if(value.equalsIgnoreCase("fluid"))
			return EPartType.FLUID;
		if(value.equalsIgnoreCase("energy"))
			return EPartType.ENERGY;
		if(value.equalsIgnoreCase("chemical"))
			return EPartType.CHEMICAL;
		
		return EPartType.ITEM;
	}
	
	public static SpecialRecipeSerializer<RecipeCell> CRAFTING_STORAGETECH_CELL = new SpecialRecipeSerializer<>(RecipeCell::new);
	public static SpecialRecipeSerializer<RecipeCustomDisk> CRAFTING_CUSTOM_DISK = new SpecialRecipeSerializer<>(RecipeCustomDisk::new);	

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> register)
    {
    	register.getRegistry().register(CRAFTING_STORAGETECH_CELL.setRegistryName(StorageTech.MODID, "recipe_cell"));
    	register.getRegistry().register(CRAFTING_CUSTOM_DISK.setRegistryName(StorageTech.MODID, "recipe_custom_disk"));
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
    	{
    		javapower.storagetech.mekanism.block.MKBlocks.registerContainers(register.getRegistry());
    		javapower.storagetech.mekanism.item.MKItems.registerContainers(register.getRegistry());
    	}
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
