package javapower.storagetech.packet;

import java.util.UUID;
import java.util.function.Supplier;

import javapower.storagetech.api.STAPI;
import javapower.storagetech.data.StorageEnergyDiskSyncData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketStorageEnergyDiskSizeResponse
{
	private final UUID id;
    private final int stored;
    private final int capacity;
    private final int io_capacity;
	
	public PacketStorageEnergyDiskSizeResponse(UUID _id, int _stored, int _capacity, int _io_capacity)
	{
		id = _id;
		stored = _stored;
		capacity = _capacity;
		io_capacity = _io_capacity;
        
	}
	
	public static void encoder(PacketStorageEnergyDiskSizeResponse msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeUniqueId(msg.id);
        packetBuffer.writeInt(msg.stored);
        packetBuffer.writeInt(msg.capacity);
        packetBuffer.writeInt(msg.io_capacity);
	}
	
	public static PacketStorageEnergyDiskSizeResponse decoder(PacketBuffer packetBuffer)
	{
		return new PacketStorageEnergyDiskSizeResponse(packetBuffer.readUniqueId(), packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readInt());
	}
	
	public static void handle(PacketStorageEnergyDiskSizeResponse msg, Supplier<NetworkEvent.Context> ctx)
	{
		 ctx.get().enqueueWork(() ->
		 {
			 STAPI.STORAGE_DISK_SYNC.setData(msg.id, new StorageEnergyDiskSyncData(msg.stored, msg.capacity, msg.io_capacity));
		 });
		 ctx.get().setPacketHandled(true);
	}
}
