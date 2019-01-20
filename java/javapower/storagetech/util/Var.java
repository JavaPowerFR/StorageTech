package javapower.storagetech.util;

public class Var<T>
{
	public T var;
	
	public Var(T _var)
	{
		var = _var;
	}
	
	public void setVar(T _var)
	{
		var = _var;
	}
	
	public T getVar()
	{
		return var;
	}
}
