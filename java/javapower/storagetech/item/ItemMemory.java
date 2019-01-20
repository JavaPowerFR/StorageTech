package javapower.storagetech.item;

import java.util.List;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.IItemRegister;
import javapower.storagetech.util.IRenderItemRegister;
import javapower.storagetech.util.ItemRenderCast;
import javapower.storagetech.util.Tools;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemMemory extends Item implements IItemRegister, IRenderItemRegister
{
	public ItemMemory()
	{
		setRegistryName("memory");
		setUnlocalizedName("memory");
		setCreativeTab(StorageTech.creativeTab);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
        {
			for(int i = 0; i <= 1; ++i)
				items.add(new ItemStack(this, 1, i));
        }
	}
	
	@Override
	public ItemRenderCast[] getItemsRender()
	{
		return new ItemRenderCast[]
				{
						new ItemRenderCast(0, "memory"),
						new ItemRenderCast(1, "memory")
				};
	}

	@Override
	public Item getItem()
	{
		return this;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(stack != null)
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt != null && nbt.hasKey("memory"))
			{
				tooltip.add(Tools.longFormatToString(nbt.getLong("memory")) + (stack.getItemDamage() == 1 ? " VFT" : " VIB"));
				tooltip.add(stack.getItemDamage() == 1 ? I18n.format("storagetech.tooltip.putvft") : I18n.format("storagetech.tooltip.putvib"));
			}
			else
			{
				tooltip.add(stack.getItemDamage() == 1 ? I18n.format("storagetech.tooltip.novft") : I18n.format("storagetech.tooltip.novib"));
			}
		}
	}
	
	public static ItemStack createItem(long quant, boolean isFluid)
	{
		ItemStack item = new ItemStack(STItems.item_memory, 1, isFluid ? 1 : 0);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("memory", quant);
		item.setTagCompound(nbt);
		
		return item;
	}
	
}
