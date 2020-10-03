package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import mekanism.api.chemical.Chemical;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalGridStackUpdate
{
	private Chemical<?> chemical;
	private long amnt;

    public PacketChemicalGridStackUpdate(Chemical<?> _chemical, long _amnt)
    {
    	chemical = _chemical;
    	amnt = _amnt;
    }

    public static PacketChemicalGridStackUpdate decoder(PacketBuffer buf)
    {
    	return new PacketChemicalGridStackUpdate(MekanismUtils.buildChemicalById(buf.readByte(),buf.readCompoundTag()), buf.readLong());
    }

    public static void encoder(PacketChemicalGridStackUpdate message, PacketBuffer buf)
    {
    	buf.writeByte(MekanismUtils.getChemicalTypeId(message.chemical));
    	buf.writeCompoundTag(message.chemical.write(new CompoundNBT()));
    	buf.writeLong(message.amnt);
    	
    }

    public static void handle(PacketChemicalGridStackUpdate message, Supplier<NetworkEvent.Context> ctx)
    {
            ctx.get().enqueueWork(() ->
            {
            	if(Minecraft.getInstance().currentScreen instanceof ScreenChemicalGrid)
            	{
            		((ScreenChemicalGrid)Minecraft.getInstance().currentScreen).getGrid().updateStack(message.chemical, message.amnt);
            	}
            });
        ctx.get().setPacketHandled(true);
    }
}
