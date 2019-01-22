package javapower.storagetech.message;

import org.lwjgl.input.Mouse;

import javapower.storagetech.proxy.ClientProxy;
import javapower.storagetech.util.IGUITileSync;
import javapower.storagetech.util.NetworkUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
		int lock_key = message.nbt_inf.getInteger("lk");
		
		if(lock_key == 4)
		{
			if(ClientBuffer.currentGuiBuilder != null && ClientBuffer.currentGuiBuilder instanceof IGUITileSync)
			{
				ClientBuffer.currentGuiBuilder.mc = ClientProxy.minecraft;
				((IGUITileSync)ClientBuffer.currentGuiBuilder).reciveDataFromServer(message.nbt);
				
				FMLCommonHandler.instance().showGuiScreen(ClientBuffer.currentGuiBuilder);
				//ClientProxy.minecraft.currentScreen = ClientBuffer.currentGuiBuilder;
				ClientBuffer.currentGuiBuilder = null;
			}
		}
		else if(ClientProxy.minecraft.currentScreen instanceof IGUITileSync)
		{
			((IGUITileSync)ClientProxy.minecraft.currentScreen).reciveDataFromServer(message.nbt);
		}
		else if(ClientBuffer.currentGuiBuilder == null)
		{
			NetworkUtils.sendToServerPlayerStopOpenGUI(message.env);
		}
		return null;
	}

}