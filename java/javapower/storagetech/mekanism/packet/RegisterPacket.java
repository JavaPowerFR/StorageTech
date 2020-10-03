package javapower.storagetech.mekanism.packet;

import javapower.storagetech.util.IdDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RegisterPacket
{

	public static void register(SimpleChannel INSTANCE_CHANNEL, IdDistributor id)
	{
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStorageChemicalDiskSizeRequest.class, PacketStorageChemicalDiskSizeRequest::encoder, PacketStorageChemicalDiskSizeRequest::decoder, PacketStorageChemicalDiskSizeRequest::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStorageChemicalDiskSizeResponse.class, PacketStorageChemicalDiskSizeResponse::encoder, PacketStorageChemicalDiskSizeResponse::decoder, PacketStorageChemicalDiskSizeResponse::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketChemicalFilterSlotUpdateMessage.class, PacketChemicalFilterSlotUpdateMessage::encoder, PacketChemicalFilterSlotUpdateMessage::decoder, PacketChemicalFilterSlotUpdateMessage::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketChemicalFilterUpdateMessage.class, PacketChemicalFilterUpdateMessage::encoder, PacketChemicalFilterUpdateMessage::decoder, PacketChemicalFilterUpdateMessage::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketChemicalGridInitialize.class, PacketChemicalGridInitialize::encoder, PacketChemicalGridInitialize::decoder, PacketChemicalGridInitialize::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketChemicalGridStackUpdate.class, PacketChemicalGridStackUpdate::encoder, PacketChemicalGridStackUpdate::decoder, PacketChemicalGridStackUpdate::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketChemicalInitializeContainer.class, PacketChemicalInitializeContainer::encoder, PacketChemicalInitializeContainer::decoder, PacketChemicalInitializeContainer::handle);
		INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketChemicalGridHeldStack.class, PacketChemicalGridHeldStack::encoder, PacketChemicalGridHeldStack::decoder, PacketChemicalGridHeldStack::handle);
		
	}

}
