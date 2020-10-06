package javapower.storagetech.packet;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketGenericContainer
{
	CompoundNBT nbt;
	
	public PacketGenericContainer(CompoundNBT _nbt)
	{
		nbt = _nbt;
	}
	
	
	
	public static void encoder(PacketGenericContainer msg, PacketBuffer packetBuffer)
	{
		packetBuffer.writeCompoundTag(msg.nbt);
	}
	
	public static PacketGenericContainer decoder(PacketBuffer packetBuffer)
	{
		return new PacketGenericContainer(packetBuffer.readCompoundTag());
	}
	
	public static void handle(PacketGenericContainer msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
			{
				if(ctx.get().getSender().openContainer instanceof IGenericMessage)
            		((IGenericMessage)ctx.get().getSender().openContainer).recivePacket(msg.nbt);
			}
			else if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			{
				clientHandle(msg, ctx);
			}
	    });
	    ctx.get().setPacketHandled(true);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void clientHandle(PacketGenericContainer msg, Supplier<NetworkEvent.Context> ctx)
	{
		if(Minecraft.getInstance().currentScreen instanceof IGenericMessage)
    	{
    		((IGenericMessage)Minecraft.getInstance().currentScreen).recivePacket(msg.nbt);
    	}
	}
}
