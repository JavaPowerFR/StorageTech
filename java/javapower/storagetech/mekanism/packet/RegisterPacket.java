package javapower.storagetech.mekanism.packet;

import javapower.storagetech.util.IdDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RegisterPacket
{

	public static void register(SimpleChannel INSTANCE_CHANNEL, IdDistributor id)
	{
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStorageGasDiskSizeRequest.class, PacketStorageGasDiskSizeRequest::encoder, PacketStorageGasDiskSizeRequest::decoder, PacketStorageGasDiskSizeRequest::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStorageGasDiskSizeResponse.class, PacketStorageGasDiskSizeResponse::encoder, PacketStorageGasDiskSizeResponse::decoder, PacketStorageGasDiskSizeResponse::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketGasFilterSlotUpdateMessage.class, PacketGasFilterSlotUpdateMessage::encoder, PacketGasFilterSlotUpdateMessage::decoder, PacketGasFilterSlotUpdateMessage::handle);
		
	}

}
