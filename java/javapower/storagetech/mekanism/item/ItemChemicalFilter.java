package javapower.storagetech.mekanism.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.STItems;
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.container.ContainerChemicalFilter;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemChemicalFilter extends Item
{
	
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_MOD_FILTER = "ModFilter";
    private static final String NBT_NAME = "Name";
    private static final String NBT_CHEMICAL_ICON = "ChemicalIcon";
    public static final String NBT_CHEMICAL_FILTERS = "ChemicalFilters";
    
    public ItemChemicalFilter()
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID, "chemical_filter");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
	
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote)
        {
            if (player.isCrouching())
            {
                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(this));
            }

            player.openContainer(new INamedContainerProvider()
            {
                @Override
                public ITextComponent getDisplayName()
                {
                    return new TranslationTextComponent("gui.storagetech.chemical_filter");
                }

                @Nullable
                @Override
                public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player)
                {
                    return new ContainerChemicalFilter(player, inventory.getCurrentItem(), windowId);
                }
            });

            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<>(ActionResultType.PASS, stack);
    }
	
	@Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("sidebutton.refinedstorage.mode." + (getMode(stack) == IFilter.MODE_WHITELIST ? "whitelist" : "blacklist")).func_230530_a_(Styles.YELLOW));

        if (isModFilter(stack))
        {
            tooltip.add(new TranslationTextComponent("gui.refinedstorage.filter.mod_filter").func_230530_a_(Styles.BLUE));
        }
        //GasFilterInventory chemicals = new GasFilterInventory(stack);

        //MekanismUtils.addCombinedGassToTooltip(tooltip, false, chemicals.getFilteredGass());
    }
	
	@Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
        return false;
    }
	
	public static int getCompare(ItemStack stack)
	{
        return (stack.hasTag() && stack.getTag().contains(NBT_COMPARE)) ? stack.getTag().getInt(NBT_COMPARE) : IComparer.COMPARE_NBT;
    }
	
	public static void setCompare(ItemStack stack, int compare)
	{
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());

        stack.getTag().putInt(NBT_COMPARE, compare);
    }
	
	public static int getMode(ItemStack stack)
	{
        return (stack.hasTag() && stack.getTag().contains(NBT_MODE)) ? stack.getTag().getInt(NBT_MODE) : IFilter.MODE_WHITELIST;
    }

    public static void setMode(ItemStack stack, int mode)
    {
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());

        stack.getTag().putInt(NBT_MODE, mode);
    }
    
    public static boolean isModFilter(ItemStack stack)
    {
        return stack.hasTag() && stack.getTag().contains(NBT_MOD_FILTER) && stack.getTag().getBoolean(NBT_MOD_FILTER);
    }

    public static void setModFilter(ItemStack stack, boolean modFilter)
    {
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());

        stack.getTag().putBoolean(NBT_MOD_FILTER, modFilter);
    }
    
    public static String getName(ItemStack stack)
    {
        return stack.hasTag() && stack.getTag().contains(NBT_NAME) ? stack.getTag().getString(NBT_NAME) : "";
    }

    public static void setName(ItemStack stack, String name)
    {
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());

        stack.getTag().putString(NBT_NAME, name);
    }
    
    public static void setGasIcon(ItemStack stack, @Nullable ChemicalStack<?> icon)
    {
        if (!stack.hasTag())
        {
            stack.setTag(new CompoundNBT());
        }

        if (icon == null)
        {
            stack.getTag().remove(NBT_CHEMICAL_ICON);
        }
        else
        {
        	CompoundNBT nbt = icon.write(new CompoundNBT());
        	nbt.putByte("Chemical", MekanismUtils.getChemicalTypeId(icon.getType()));
            stack.getTag().put(NBT_CHEMICAL_ICON, nbt);
        }
    }

    @Nonnull
    public static ChemicalStack<?> getGasIcon(ItemStack stack)
    {
    	if(stack.hasTag() && stack.getTag().contains(NBT_CHEMICAL_ICON))
    	{
    		CompoundNBT nbt = stack.getTag().getCompound(NBT_CHEMICAL_ICON);
    		if(nbt.contains("Chemical"))
    			return MekanismUtils.buildChemicalStackById(nbt.getByte("Chemical"), nbt);
    	}
        return GasStack.EMPTY;
    }

}
