package javapower.storagetech.mekanism.packet;

import java.util.UUID;
import java.util.function.Supplier;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.data.ChemicalDisk;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketStorageChemicalDiskSizeRequest
{
	private final UUID id;
	
	public PacketStorageChemicalDiskSizeRequest(UUID _id)
	{
		id = _id;
	}
	
	public static void encoder(PacketStorageChemicalDiskSizeRequest msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeUniqueId(msg.id);
	}
	
	public static PacketStorageChemicalDiskSizeRequest decoder(PacketBuffer packetBuffer)
	{
		return new PacketStorageChemicalDiskSizeRequest(packetBuffer.readUniqueId());
	}
	
	public static void handle(PacketStorageChemicalDiskSizeRequest msg, Supplier<NetworkEvent.Context> ctx)
	{
		 ctx.get().enqueueWork(() ->
		 {
			 ChemicalDisk disk = STAPI.getNetworkManager(ctx.get().getSender().getServerWorld()).getMekanisumManager().getChemicalDisk(msg.id);
			 if(disk != null)
			 {
				 StorageTech.INSTANCE_CHANNEL.sendTo(new PacketStorageChemicalDiskSizeResponse(
								 disk.id,
								 disk.getAmount(),
								 disk.getCapacity()
								 ), ctx.get().getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			 }
		 });
		 ctx.get().setPacketHandled(true);
	}
}
