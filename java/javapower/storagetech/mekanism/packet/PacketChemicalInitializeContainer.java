package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import javapower.storagetech.mekanism.container.ContainerChemicalGrid;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalInitializeContainer
{
	int yPlayerInv, rows;

    public PacketChemicalInitializeContainer(int _yPlayerInv, int _rows)
    {
    	yPlayerInv = _yPlayerInv;
    	rows = _rows;
    }

    public static PacketChemicalInitializeContainer decoder(PacketBuffer buf)
    {
    	return new PacketChemicalInitializeContainer(buf.readInt(), buf.readInt());
    }

    public static void encoder(PacketChemicalInitializeContainer message, PacketBuffer buf)
    {
    	buf.writeInt(message.yPlayerInv);
    	buf.writeInt(message.rows);
    }

    public static void handle(PacketChemicalInitializeContainer message, Supplier<NetworkEvent.Context> ctx)
    {
            ctx.get().enqueueWork(() ->
            {
            	if(ctx.get().getSender().openContainer instanceof ContainerChemicalGrid)
            	{
            		((ContainerChemicalGrid)ctx.get().getSender().openContainer).initSlotServer(message.yPlayerInv, message.rows);
            	}
            });
        ctx.get().setPacketHandled(true);
    }
}
