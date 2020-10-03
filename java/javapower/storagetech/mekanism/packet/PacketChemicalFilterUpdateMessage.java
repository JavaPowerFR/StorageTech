package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import com.refinedmods.refinedstorage.util.PacketBufferUtils;

import javapower.storagetech.mekanism.container.ContainerChemicalFilter;
import javapower.storagetech.mekanism.item.ItemChemicalFilter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalFilterUpdateMessage
{
    private final int compare;
    private final int mode;
    private final boolean modFilter;
    private final String name;

    public PacketChemicalFilterUpdateMessage(int compare, int mode, boolean modFilter, String name)
    {
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
        this.name = name;
    }

    public static PacketChemicalFilterUpdateMessage decoder(PacketBuffer buf)
    {
        return new PacketChemicalFilterUpdateMessage(
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            PacketBufferUtils.readString(buf)
        );
    }

    public static void encoder(PacketChemicalFilterUpdateMessage message, PacketBuffer buf)
    {
        buf.writeInt(message.compare);
        buf.writeInt(message.mode);
        buf.writeBoolean(message.modFilter);
        buf.writeString(message.name);
    }

    public static void handle(PacketChemicalFilterUpdateMessage message, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity player = ctx.get().getSender();

        if (player != null && player.openContainer instanceof ContainerChemicalFilter)
        {
            ctx.get().enqueueWork(() -> {
                ItemChemicalFilter.setCompare(((ContainerChemicalFilter) player.openContainer).getStack(), message.compare);
                ItemChemicalFilter.setMode(((ContainerChemicalFilter) player.openContainer).getStack(), message.mode);
                ItemChemicalFilter.setModFilter(((ContainerChemicalFilter) player.openContainer).getStack(), message.modFilter);
                ItemChemicalFilter.setName(((ContainerChemicalFilter) player.openContainer).getStack(), message.name);
            });
        }

        ctx.get().setPacketHandled(true);
    }
}