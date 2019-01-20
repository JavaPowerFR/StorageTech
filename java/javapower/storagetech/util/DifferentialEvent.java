package javapower.storagetech.util;

import javapower.storagetech.eventio.IEventOut;

public class DifferentialEvent<T>
{
	T buffer;
	IEventOut<T> event;
	
	public DifferentialEvent(IEventOut<T> _event)
	{
		event = _event;
	}
	
	public DifferentialEvent(T defaultvalue, IEventOut<T> _event)
	{
		event = _event;
		buffer = defaultvalue;
	}
	
	public void SetValue(T v)
	{
		if(!v.equals(buffer))
		{
			buffer = v;
			event.event(buffer);
		}
	}
	
	public T GetValue()
	{
		return buffer;
	}
}
