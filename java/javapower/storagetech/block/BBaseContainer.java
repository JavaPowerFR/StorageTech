package javapower.storagetech.block;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.IBlockRegister;
import javapower.storagetech.util.IRenderItemRegister;
import javapower.storagetech.util.ITileRegister;
import javapower.storagetech.util.ItemRenderCast;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BBaseContainer extends BlockContainer implements IBlockRegister, IRenderItemRegister, ITileRegister
{
	String _name;
	Item thisItem;
	
	public BBaseContainer(Material materialIn, String name)
	{
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(name);
		_name = name;
		setCreativeTab(StorageTech.creativeTab);
		setHardness(2.5F);
		setResistance(3F);
	}
	
	@Override
	public Item getItem()
	{
		if(thisItem == null)
		{
			thisItem = new ItemBlock(this);
			thisItem.setRegistryName(_name);
		}
		return thisItem;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemRenderCast[] getItemsRender()
	{
		return new ItemRenderCast[]
				{
						new ItemRenderCast(0, "inventory")
				};
	}

}
