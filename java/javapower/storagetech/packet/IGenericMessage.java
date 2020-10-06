package javapower.storagetech.packet;

import javapower.storagetech.core.StorageTech;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IGenericMessage
{
	public void recivePacket(CompoundNBT nbt);
	
	public static void sendToContainer(CompoundNBT nbt)
	{
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketGenericContainer(nbt));
	}
	
	public static void sendToScreen(ServerPlayerEntity player, CompoundNBT nbt)
	{
		StorageTech.sendTo(player, new PacketGenericContainer(nbt));
	}
}
