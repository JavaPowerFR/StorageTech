package javapower.storagetech.util;

import java.lang.reflect.Field;

import javapower.storagetech.core.StorageTech;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterUtils
{
	public static void RegisterItems(Field[] fields, IForgeRegistry<Item> register)
	{
		for(Field field : fields)
    	{
    		try
    		{
				Object var = field.get(null);
				if(var instanceof IItemRegister)
				{
					Item i = ((IItemRegister)var).getItem();
					if(i != null)
						register.register(i);
				}
			}
    		catch (Exception ex)
    		{
				ex.printStackTrace();
			}
    	}
	}
	
	public static void RegisterBlocks(Field[] fields, IForgeRegistry<Block> register)
	{
		for(Field field : fields)
    	{
    		try
    		{
				Object var = field.get(null);
				if(var instanceof IBlockRegister)
				{
					Block b = ((IBlockRegister)var).getBlock();
					if(b != null)
						register.register(b);
				}
			}
    		catch (Exception ex)
    		{
				ex.printStackTrace();
			}
    	}
	}
	
	public static void RegisterTilesEntity(Field[] fields)
	{
		for(Field field : fields)
    	{
    		try
    		{
				Object var = field.get(null);
				if(var instanceof ITileRegister)
				{
					TileNamed[] tiles = ((ITileRegister)var).getTilesEntity();
					if(tiles != null && tiles.length > 0)
						for(TileNamed cte : tiles)
						{
							GameRegistry.registerTileEntity(cte.TClass, StorageTech.MODID + ":" + cte.TName);
						}
				}
			}
    		catch (Exception ex)
    		{
				ex.printStackTrace();
			}
    	}
	}
	
	@SideOnly(Side.CLIENT)
	public static void RegisterIRender(Field[] fields)
	{
		for(Field field : fields)
    	{
    		try
    		{
				Object var = field.get(null);
				if(var instanceof IRenderItemRegister && var instanceof IItemRegister)
				{
					Item item = ((IItemRegister)var).getItem();
					for(ItemRenderCast renderCast : ((IRenderItemRegister)var).getItemsRender())
					{
						if(renderCast != null)
						{
							String name = item.getUnlocalizedName().replace("item.", "");
							if(name.startsWith("tile."))
								name = name.replace("tile.", "");
							ModelLoader.setCustomModelResourceLocation(item, renderCast.getMetadata(), renderCast.getModelRL(name));
						}
					}
				}
			}
    		catch (Exception ex)
    		{
				ex.printStackTrace();
			}
    	}
	}
}
