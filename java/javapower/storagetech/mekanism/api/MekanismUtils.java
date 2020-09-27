package javapower.storagetech.mekanism.api;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.api.util.IComparer;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class MekanismUtils
{
	public static GasStack getGas(ItemStack stack, boolean simulate)
	{
        if (stack.isEmpty())
            return GasStack.EMPTY;

        if (stack.getCount() > 1)
            stack = ItemHandlerHelper.copyStackWithSize(stack, 1);

        IGasHandler handler = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, null).orElse(null);
        if (handler != null)
            return handler.extractChemical(1000, simulate ? Action.SIMULATE : Action.EXECUTE);

        return GasStack.EMPTY;
    }
	
    public static boolean isEqual(@Nonnull GasStack left, @Nonnull GasStack right, int flags)
	{
        if (left.isEmpty() && right.isEmpty())
        {
            return true;
        }

        if (left.getType() != right.getType())
        {
            return false;
        }

        if ((flags & IComparer.COMPARE_QUANTITY) == IComparer.COMPARE_QUANTITY)
        {
            if (left.getAmount() != right.getAmount())
            {
                return false;
            }
        }

        return true;
    }
}
