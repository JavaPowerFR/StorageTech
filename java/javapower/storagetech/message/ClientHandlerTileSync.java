package javapower.storagetech.message;

import javapower.storagetech.proxy.ClientProxy;
import javapower.storagetech.util.IGUITileSync;
import javapower.storagetech.util.NetworkUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerTileSync implements IMessageHandler<NetworkTileSync, IMessage>
{
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(NetworkTileSync message, MessageContext ctx)
	{
		if(ClientProxy.minecraft.currentScreen instanceof IGUITileSync)
		{
			((IGUITileSync)ClientProxy.minecraft.currentScreen).reciveDataFromServer(message.nbt);
		}
		else
		{
			NetworkUtils.sendToServerPlayerStopOpenGUI(message.env);
		}
		return null;
	}

}