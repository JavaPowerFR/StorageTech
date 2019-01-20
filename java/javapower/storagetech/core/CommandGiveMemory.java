package javapower.storagetech.core;

import javapower.storagetech.item.ItemMemory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandGiveMemory extends CommandBase
{

	@Override
	public String getName()
	{
		return "givememory";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "givememory <Item|Fluid> <amount> [k,M,G]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(sender.getCommandSenderEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = ((EntityPlayer)sender.getCommandSenderEntity());
			if(args != null)
			{
				if(args.length == 2)
				{
					try
					{
						int type = getType(args[0]);
						long mem = Long.parseLong(args[1]);
						if(type == 1)//item
						{
							ItemStack is = ItemMemory.createItem(mem, false);
							player.dropItem(is, true);
							return;
						}
						else if(type == 2)//fluid
						{
							ItemStack is = ItemMemory.createItem(mem, true);
							player.dropItem(is, true);
							return;
						}
						else
						{
							throw new CommandException("Bad argument /givememory <Item|Fluid> <amount> [k,M,G]", args[0]);
						}
					}
					catch (NumberFormatException | NullPointerException e)
					{
						throw new CommandException("Bad argument /givememory <Item|Fluid> <amount> [k,M,G]", args[1]);
					}
				}
				else if(args.length == 3)
				{
					try
					{
						int type = getType(args[0]);
						long mem = Long.parseLong(args[1]);
						int multi = getMultiplier(args[2]);
						
						if(multi == -1)
							throw new CommandException("Bad argument /givememory <Item|Fluid> <amount> [k,M,G]", args[0]);
						
						if(type == 1)//item
						{
							ItemStack is = ItemMemory.createItem(mem*multi, false);
							player.dropItem(is, true);
							return;
						}
						else if(type == 2)//fluid
						{
							ItemStack is = ItemMemory.createItem(mem*multi, true);
							player.dropItem(is, true);
							return;
						}
						else
						{
							throw new CommandException("Bad argument /givememory <Item|Fluid> <amount> [k,M,G]", args[0]);
						}
					}
					catch (NumberFormatException | NullPointerException e)
					{
						throw new CommandException("Bad argument /givememory <Item|Fluid> <amount> [k,M,G]", args[1]);
					}
				}
				else
				{
					throw new CommandException("Bad amount of arguments /givememory <Item|Fluid> <amount> [k,M,G]", args.length);
				}
			}
			else
			{
				throw new CommandException("Bad amount of arguments /givememory <Item|Fluid> <amount> [k,M,G]", "");
			}
		}
	}
	
	private int getType(String arg)
	{
		if(arg != null)
		{
			if(arg.equalsIgnoreCase("Item"))
				return 1;
			else if(arg.equalsIgnoreCase("Fluid"))
				return 2;
		}
		return 0;
	}
	
	private int getMultiplier(String arg)
	{
		if(arg == null || arg.length() != 1)
			return -1;
		
		int m = "kMG".indexOf(arg.charAt(0));
		if(m == 0)
			return 1000;
		else if(m == 1)
			return 1000000;
		else if(m == 2)
			return 1000000000;
		return -1;
	}
	
}