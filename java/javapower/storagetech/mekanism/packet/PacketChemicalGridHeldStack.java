package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.container.ContainerChemicalGrid;
import mekanism.api.chemical.Chemical;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalGridHeldStack
{
	private Chemical<?> chemical;
	private boolean putInGrid;
	private boolean shift;

    public PacketChemicalGridHeldStack(Chemical<?> _chemical, boolean _putInGrid, boolean _shift)
    {
    	chemical = _chemical;
    	putInGrid = _putInGrid;
    	shift = _shift;
    }

    public static PacketChemicalGridHeldStack decoder(PacketBuffer buf)
    {
    	if(buf.readBoolean())
    	{
    		return new PacketChemicalGridHeldStack(null, buf.readBoolean(), buf.readBoolean());
    	}
    	else
    		return new PacketChemicalGridHeldStack(MekanismUtils.buildChemicalById(buf.readByte(),buf.readCompoundTag()), buf.readBoolean(), buf.readBoolean());
    }

    public static void encoder(PacketChemicalGridHeldStack message, PacketBuffer buf)
    {
    	buf.writeBoolean(message.chemical == null);
    	if(message.chemical != null)
    	{
	    	buf.writeByte(MekanismUtils.getChemicalTypeId(message.chemical));
	    	buf.writeCompoundTag(message.chemical.write(new CompoundNBT()));
    	}
    	buf.writeBoolean(message.putInGrid);
    	buf.writeBoolean(message.shift);
    }

    public static void handle(PacketChemicalGridHeldStack message, Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() ->
        {
        	if(ctx.get().getSender().openContainer instanceof ContainerChemicalGrid)
        	{
        		((ContainerChemicalGrid)ctx.get().getSender().openContainer).tryToInteractWidthHeldStack(message.chemical, message.putInGrid, message.shift);
        	}
        });
        ctx.get().setPacketHandled(true);
    }
}
