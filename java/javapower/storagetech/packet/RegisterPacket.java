package javapower.storagetech.packet;

import javapower.storagetech.util.IdDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RegisterPacket
{
	public static void register(SimpleChannel INSTANCE_CHANNEL, IdDistributor id)
	{
        INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStructureConstructor.class, PacketStructureConstructor::encoder, PacketStructureConstructor::decoder, PacketStructureConstructor::handle);
        INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStorageEnergyDiskSizeRequest.class, PacketStorageEnergyDiskSizeRequest::encoder, PacketStorageEnergyDiskSizeRequest::decoder, PacketStorageEnergyDiskSizeRequest::handle);
        INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketStorageEnergyDiskSizeResponse.class, PacketStorageEnergyDiskSizeResponse::encoder, PacketStorageEnergyDiskSizeResponse::decoder, PacketStorageEnergyDiskSizeResponse::handle);
        INSTANCE_CHANNEL.registerMessage(id.getNextId(), PacketGenericContainer.class, PacketGenericContainer::encoder, PacketGenericContainer::decoder, PacketGenericContainer::handle);

	}
}
