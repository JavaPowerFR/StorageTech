package javapower.storagetech.mekanism.packet;

import java.util.UUID;
import java.util.function.Supplier;

import javapower.storagetech.mekanism.api.STMKAPI;
import javapower.storagetech.mekanism.data.StorageChemicalDiskSyncData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketStorageChemicalDiskSizeResponse
{
	private final UUID id;
    private final long stored;
    private final long capacity;
	
	public PacketStorageChemicalDiskSizeResponse(UUID _id, long _stored, long _capacity)
	{
		id = _id;
		stored = _stored;
		capacity = _capacity;    
	}
	
	public static void encoder(PacketStorageChemicalDiskSizeResponse msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeUniqueId(msg.id);
        packetBuffer.writeLong(msg.stored);
        packetBuffer.writeLong(msg.capacity);
	}
	
	public static PacketStorageChemicalDiskSizeResponse decoder(PacketBuffer packetBuffer)
	{
		return new PacketStorageChemicalDiskSizeResponse(packetBuffer.readUniqueId(), packetBuffer.readLong(), packetBuffer.readLong());
	}
	
	public static void handle(PacketStorageChemicalDiskSizeResponse msg, Supplier<NetworkEvent.Context> ctx)
	{
		 ctx.get().enqueueWork(() ->
		 {
			 STMKAPI.STORAGE_DISK_SYNC.setData(msg.id, new StorageChemicalDiskSyncData(msg.stored, msg.capacity));
		 });
		 ctx.get().setPacketHandled(true);
	}
}
