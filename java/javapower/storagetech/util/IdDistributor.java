package javapower.storagetech.util;

public class IdDistributor
{
	int id;
	public IdDistributor()
	{
		id = 0;;
	}
	
	public int getNextId()
	{
		return id++;
	}
}
