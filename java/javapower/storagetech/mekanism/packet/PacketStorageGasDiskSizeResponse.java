package javapower.storagetech.mekanism.packet;

import java.util.UUID;
import java.util.function.Supplier;

import javapower.storagetech.mekanism.api.STMKAPI;
import javapower.storagetech.mekanism.data.StorageGasDiskSyncData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketStorageGasDiskSizeResponse
{
	private final UUID id;
    private final long stored;
    private final long capacity;
	
	public PacketStorageGasDiskSizeResponse(UUID _id, long _stored, long _capacity)
	{
		id = _id;
		stored = _stored;
		capacity = _capacity;    
	}
	
	public static void encoder(PacketStorageGasDiskSizeResponse msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeUniqueId(msg.id);
        packetBuffer.writeLong(msg.stored);
        packetBuffer.writeLong(msg.capacity);
	}
	
	public static PacketStorageGasDiskSizeResponse decoder(PacketBuffer packetBuffer)
	{
		return new PacketStorageGasDiskSizeResponse(packetBuffer.readUniqueId(), packetBuffer.readLong(), packetBuffer.readLong());
	}
	
	public static void handle(PacketStorageGasDiskSizeResponse msg, Supplier<NetworkEvent.Context> ctx)
	{
		 ctx.get().enqueueWork(() ->
		 {
			 STMKAPI.STORAGE_DISK_SYNC.setData(msg.id, new StorageGasDiskSyncData(msg.stored, msg.capacity));
		 });
		 ctx.get().setPacketHandled(true);
	}
}
