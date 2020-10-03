package javapower.storagetech.mekanism.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalGridInitialize
{
	private List<ChemicalStack<?>> /*TreeMap<Chemical<?>, Long>*/ chemicals;
	private String search;

    public PacketChemicalGridInitialize(List<ChemicalStack<?>> _chemicals, String _search)
    {
    	chemicals = _chemicals;
    	search = _search;
    }

    public static PacketChemicalGridInitialize decoder(PacketBuffer buf)
    {
    	PacketChemicalGridInitialize packet = new PacketChemicalGridInitialize(new ArrayList<>(), "");
    	
    	packet.search = buf.readString();
    	
    	int size = buf.readInt();
    	for(int index = 0; index < size; ++index)
    	{
    		//Chemical<?> ch = MekanismUtils.buildChemicalById(buf.readByte(), buf.readCompoundTag());
    		packet.chemicals.add(MekanismUtils.buildChemicalStackById(buf.readByte(), buf.readCompoundTag()));
    	}
        return packet;
    }

    public static void encoder(PacketChemicalGridInitialize message, PacketBuffer buf)
    {
    	buf.writeString(message.search);
    	buf.writeInt(message.chemicals.size());
    	message.chemicals.forEach((v) ->
    	{
    		buf.writeByte(MekanismUtils.getChemicalTypeId(v.getType()));
    		buf.writeCompoundTag(v.write(new CompoundNBT()));
    	});
    }

    public static void handle(PacketChemicalGridInitialize message, Supplier<NetworkEvent.Context> ctx)
    {
            ctx.get().enqueueWork(() ->
            {
            	if(Minecraft.getInstance().currentScreen instanceof ScreenChemicalGrid)
            	{
            		((ScreenChemicalGrid)Minecraft.getInstance().currentScreen).getGrid().initalize(message.chemicals, message.search);
            	}
            });
        ctx.get().setPacketHandled(true);
    }
}
