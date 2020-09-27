package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import com.refinedmods.refinedstorage.screen.BaseScreen;

import javapower.storagetech.mekanism.inventory.GasFilterSlot;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketGasFilterSlotUpdateMessage
{
	private final int containerSlot;
    private final GasStack stack;

    public PacketGasFilterSlotUpdateMessage(int containerSlot, GasStack stack)
    {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static void encoder(PacketGasFilterSlotUpdateMessage message, PacketBuffer buf)
    {
        buf.writeInt(message.containerSlot);
        message.stack.writeToPacket(buf);
    }

    public static PacketGasFilterSlotUpdateMessage decoder(PacketBuffer buf)
    {
        return new PacketGasFilterSlotUpdateMessage(buf.readInt(), GasStack.readFromPacket(buf));
    }

    public static void handle(PacketGasFilterSlotUpdateMessage message, Supplier<NetworkEvent.Context> ctx)
    {
        BaseScreen.executeLater(gui -> {
            if (message.containerSlot >= 0 && message.containerSlot < gui.getContainer().inventorySlots.size())
            {
                Slot slot = gui.getContainer().getSlot(message.containerSlot);

                if (slot instanceof GasFilterSlot)
                {
                    ((GasFilterSlot) slot).getGasInventory().setGas(slot.getSlotIndex(), message.stack);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
