package javapower.storagetech.block;

import javapower.storagetech.core.StorageTech;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public abstract class BBaseContainer extends ContainerBlock
{
	protected Item thisItem;
	public String name;
	
	protected BBaseContainer(Properties builder, String _name)
	{
		super(builder);
		name = _name;
		setRegistryName(StorageTech.MODID,name);
	}
	
	public Item getItem()
	{
		if(thisItem == null)
		{
			thisItem = new BlockItem(this, new Item.Properties().group(StorageTech.creativeTab));
			thisItem.setRegistryName(StorageTech.MODID,name);
		}
		return thisItem;
	}
	
	@Override
	public Item asItem()
	{
		return getItem();
	}

	@Override
	public Block getBlock()
	{
		return this;
	}
	
	

}
