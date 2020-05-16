package javapower.storagetech.core;

import java.util.ArrayList;
import java.util.List;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.item.STItems;
import javapower.storagetech.util.DiskUtils;
import javapower.storagetech.util.PartValue;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
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
			else if(path.contains("_storage_part"))
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
	
	/*private INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node)
	{
        node.read(tag);

        return node;
    }*/

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> register)
    {
    	
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> register)
    {
    	STBlocks.registerBlocks(register.getRegistry());
    }
    
    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> register)
    {
    	STBlocks.registerItems(register.getRegistry());
    	STItems.registerItems(register.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> register)
    {
    	STBlocks.registerTiles(register.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> register)
    {
    	STBlocks.registerContainers(register.getRegistry());
    }
}
