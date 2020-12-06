package javapower.storagetech.setup;

import javapower.storagetech.util.EPartType;
import javapower.storagetech.util.PartValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PartValueCustom extends PartValue
{
	public PartValueCustom(Item i, EPartType _type)
	{
		super(i, 0, _type);
	}
	
	@Override
	public int getValue(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains("partvalue"))
			return stack.getTag().getInt("partvalue");
		
		return super.getValue(stack);
	}

}
