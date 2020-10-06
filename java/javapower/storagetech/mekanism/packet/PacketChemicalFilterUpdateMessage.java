package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import javapower.storagetech.mekanism.container.ContainerChemicalFilter;
import javapower.storagetech.mekanism.item.ItemChemicalFilter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalFilterUpdateMessage
{
    private final int mode;
    private final String name;

    public PacketChemicalFilterUpdateMessage(int mode, String name)
    {
        this.mode = mode;
        this.name = name;
    }

    public static PacketChemicalFilterUpdateMessage decoder(PacketBuffer buf)
    {
        return new PacketChemicalFilterUpdateMessage(
            buf.readInt(),
            buf.readString()
        );
    }

    public static void encoder(PacketChemicalFilterUpdateMessage message, PacketBuffer buf)
    {
        buf.writeInt(message.mode);
        buf.writeString(message.name);
    }

    public static void handle(PacketChemicalFilterUpdateMessage message, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity player = ctx.get().getSender();

        if (player != null && player.openContainer instanceof ContainerChemicalFilter)
        {
            ctx.get().enqueueWork(() -> {
                ItemChemicalFilter.setMode(((ContainerChemicalFilter) player.openContainer).getStack(), message.mode);
                ItemChemicalFilter.setName(((ContainerChemicalFilter) player.openContainer).getStack(), message.name);
            });
        }

        ctx.get().setPacketHandled(true);
    }
}