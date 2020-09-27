package javapower.storagetech.mekanism.packet;

import java.util.UUID;
import java.util.function.Supplier;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.data.GasDisk;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketStorageGasDiskSizeRequest
{
	private final UUID id;
	
	public PacketStorageGasDiskSizeRequest(UUID _id)
	{
		id = _id;
	}
	
	public static void encoder(PacketStorageGasDiskSizeRequest msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeUniqueId(msg.id);
	}
	
	public static PacketStorageGasDiskSizeRequest decoder(PacketBuffer packetBuffer)
	{
		return new PacketStorageGasDiskSizeRequest(packetBuffer.readUniqueId());
	}
	
	public static void handle(PacketStorageGasDiskSizeRequest msg, Supplier<NetworkEvent.Context> ctx)
	{
		 ctx.get().enqueueWork(() ->
		 {
			 GasDisk disk = STAPI.getNetworkManager(ctx.get().getSender().getServerWorld()).getMekanisumManager().getGasDisk(msg.id);
			 if(disk != null)
			 {
				 StorageTech.INSTANCE_CHANNEL.sendTo(new PacketStorageGasDiskSizeResponse(
								 disk.id,
								 disk.getAmount(),
								 disk.getCapacity()
								 ), ctx.get().getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			 }
		 });
		 ctx.get().setPacketHandled(true);
	}
}
