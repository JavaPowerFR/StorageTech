package javapower.storagetech.mekanism.packet;

import java.util.function.Supplier;

import com.refinedmods.refinedstorage.screen.BaseScreen;

import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.inventory.container.Slot;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketChemicalFilterSlotUpdateMessage
{
	private final int containerSlot;
    private final ChemicalStack<?> stack;

    public PacketChemicalFilterSlotUpdateMessage(int containerSlot, ChemicalStack<?> stack)
    {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }
    
    private PacketChemicalFilterSlotUpdateMessage(int containerSlot, PacketBuffer buf)
    {
        this.containerSlot = containerSlot;
        
        this.stack = MekanismUtils.buildChemicalStackById(buf.readByte(), buf.readCompoundTag());
    }

    public static void encoder(PacketChemicalFilterSlotUpdateMessage message, PacketBuffer buf)
    {
        buf.writeInt(message.containerSlot);
        buf.writeByte(MekanismUtils.getChemicalTypeId(message.stack.getType()));
        buf.writeCompoundTag(message.stack.write(new CompoundNBT()));
    }

    public static PacketChemicalFilterSlotUpdateMessage decoder(PacketBuffer buf)
    {
        return new PacketChemicalFilterSlotUpdateMessage(buf.readInt(), buf);
    }

    public static void handle(PacketChemicalFilterSlotUpdateMessage message, Supplier<NetworkEvent.Context> ctx)
    {
        BaseScreen.executeLater(gui -> {
            if (message.containerSlot >= 0 && message.containerSlot < gui.getContainer().inventorySlots.size())
            {
                Slot slot = gui.getContainer().getSlot(message.containerSlot);

                if (slot instanceof ChemicalFilterSlot)
                {
                    ((ChemicalFilterSlot) slot).getChemicalInventory().setChemical(slot.getSlotIndex(), message.stack);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
