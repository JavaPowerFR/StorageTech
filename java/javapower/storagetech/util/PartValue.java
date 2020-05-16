package javapower.storagetech.util;

import net.minecraft.item.Item;

public class PartValue
{
	private Item item;
	private long value;
	
	public PartValue(Item _item, long _value)
	{
		item = _item;
		value = _value;
	}
	
	public Item getItem() {
		return item;
	}
	
	public long getValue() {
		return value;
	}
}
