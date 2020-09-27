package javapower.storagetech.item;

import java.util.List;

import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import javapower.storagetech.util.SCElement;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemStructureCopier extends Item
{
	public ItemStructureCopier()
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID,"structurecopier");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		if(stack != null && stack.hasTag())
		{
			tooltip.add(new TranslationTextComponent("tooltip.storagetech.structurecopier.paste").func_230530_a_(Styles.GRAY));
			
			ListNBT list_elements = stack.getTag().getList("Elements", Constants.NBT.TAG_COMPOUND);
	        for (int i = 0; i < list_elements.size(); ++i)
	        {
	        	CompoundNBT nbt_element = list_elements.getCompound(i);
	        	SCElement.Client element = SCElement.Client.getFromNBT(nbt_element);
	        	if(element != null)
	        	{
	        		if(element.drop)
	        		{
	        			tooltip.add(new TranslationTextComponent("tooltip.storagetech.structurecopier.drop", element.stack.getDisplayName(), element.localPos.getI(), element.localPos.getJ(), element.localPos.getK()).func_230530_a_(Styles.YELLOW));
	        		}
	        		else
	        		{
	        			tooltip.add(new TranslationTextComponent("tooltip.storagetech.structurecopier.place", element.stack.getDisplayName(), element.localPos.getI(), element.localPos.getJ(), element.localPos.getK()).func_230530_a_(Styles.YELLOW));
	        		}
	        	}
	        		
	        }
		}
		else
		{
			tooltip.add(new TranslationTextComponent("tooltip.storagetech.structurecopier.blank").func_230530_a_(Styles.GRAY));
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return stack.hasTag();
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		TileEntity te = context.getWorld().getTileEntity(context.getPos());
		if(!context.getWorld().isRemote && te instanceof TileEntityStructureConstructor)
		{
			if(context.getItem().hasTag())
			{
				ListNBT list_elements = context.getItem().getTag().getList("Elements", Constants.NBT.TAG_COMPOUND);
				((TileEntityStructureConstructor)te).getNode().elements.clear();
		        
		        for (int i = 0; i < list_elements.size(); ++i)
		        {
		        	CompoundNBT nbt_element = list_elements.getCompound(i);
		        	SCElement element = SCElement.getFromNBT(nbt_element);
		        	if(element != null)
		        		((TileEntityStructureConstructor)te).getNode().elements.add(element);
		        }
			}
			else
			{
				ListNBT list_elements = new ListNBT();
		        for (SCElement element : ((TileEntityStructureConstructor)te).getNode().elements)
		        {
		        	if(element != null)
		        	{
			        	CompoundNBT nbt_element = new CompoundNBT();
			        	element.writeToNBT(nbt_element);
			        	list_elements.add(nbt_element);
		        	}
		        }
		        CompoundNBT nbtitem = new CompoundNBT();
		        nbtitem.put("Elements", list_elements);
		        context.getItem().setTag(nbtitem);
			}
		}
		return ActionResultType.FAIL;
	}
}
