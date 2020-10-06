package javapower.storagetech.packet;

import java.util.UUID;
import java.util.function.Supplier;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.EnergyDisk;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketStorageEnergyDiskSizeRequest
{
	private final UUID id;
	
	public PacketStorageEnergyDiskSizeRequest(UUID _id)
	{
		id = _id;
	}
	
	public static void encoder(PacketStorageEnergyDiskSizeRequest msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeUniqueId(msg.id);
	}
	
	public static PacketStorageEnergyDiskSizeRequest decoder(PacketBuffer packetBuffer)
	{
		return new PacketStorageEnergyDiskSizeRequest(packetBuffer.readUniqueId());
	}
	
	public static void handle(PacketStorageEnergyDiskSizeRequest msg, Supplier<NetworkEvent.Context> ctx)
	{
		 ctx.get().enqueueWork(() ->
		 {
			 EnergyDisk disk = STAPI.getGlobalNetworkManager(ctx.get().getSender().getServerWorld()).getEnergyDisk(msg.id);
			 if(disk != null)
			 {
				 StorageTech.INSTANCE_CHANNEL.sendTo(new PacketStorageEnergyDiskSizeResponse(
								 disk.id,
								 disk.getEnergyStored(),
								 disk.getCapacity(),
								 disk.getIOCapacity()
								 ), ctx.get().getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			 }
		 });
		 ctx.get().setPacketHandled(true);
	}
}
