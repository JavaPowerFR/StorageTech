package javapower.storagetech.mekanism.api;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.core.ResourceLocationRegister;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class MekanismUtils
{
	public static final int CHEMICAL_TYPE_GAS = 0;
	public static final int CHEMICAL_TYPE_INFUSION = 1;
	public static final int CHEMICAL_TYPE_SLURRY = 2;
	public static final int CHEMICAL_TYPE_FLUID = 3;
	public static final int CHEMICAL_TYPE_HEAT = 4;
	public static final int CHEMICAL_TYPE_PIGMENT = 5;
	
	public static ChemicalStack<?> getChemical(ItemStack stack, boolean simulate)
	{
        if (stack.isEmpty())
            return GasStack.EMPTY;

        if (stack.getCount() > 1)
            stack = ItemHandlerHelper.copyStackWithSize(stack, 1);

        IGasHandler handlerGas = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, null).orElse(null);
        if (handlerGas != null)
        {
        	GasStack gasStack = handlerGas.extractChemical(1000, simulate ? Action.SIMULATE : Action.EXECUTE);
        	
        	if(!gasStack.isEmpty())
        		return gasStack;
        }
        
        IInfusionHandler handlerInfuse = stack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY, null).orElse(null);
        if (handlerInfuse != null)
        {
        	InfusionStack infusionStack = handlerInfuse.extractChemical(1000, simulate ? Action.SIMULATE : Action.EXECUTE);
        	
        	if(!infusionStack.isEmpty())
        		return infusionStack;
        }
        
        ISlurryHandler handlerSlurry = stack.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY, null).orElse(null);
        if (handlerSlurry != null)
        {
            SlurryStack slurryStack = handlerSlurry.extractChemical(1000, simulate ? Action.SIMULATE : Action.EXECUTE);
            if(!slurryStack.isEmpty())
            	return slurryStack;
        }
        
        IPigmentHandler handlerPigment = stack.getCapability(Capabilities.PIGMENT_HANDLER_CAPABILITY, null).orElse(null);
        if (handlerPigment != null)
        {
            PigmentStack pigmentStack = handlerPigment.extractChemical(1000, simulate ? Action.SIMULATE : Action.EXECUTE);
            if(!pigmentStack.isEmpty())
            	return pigmentStack;
        }
        
        return GasStack.EMPTY;
    }
	
    public static boolean isEqual(@Nonnull ChemicalStack<?> left, @Nonnull ChemicalStack<?> right, int flags)
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
    
    public static void addCombinedGassToTooltip(List<ITextComponent> tooltip, boolean displayMb, List<GasStack> stacks)
    {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i)
        {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i))
            {
                GasStack stack = stacks.get(i);

                IFormattableTextComponent data = stack.getTextComponent().copyRaw();

                long amount = stack.getAmount();

                for (int j = i + 1; j < stacks.size(); ++j)
                {
                    if (isEqual(stack, stacks.get(j), 0))
                    {
                        amount += stacks.get(j).getAmount();

                        combinedIndices.add(j);
                    }
                }

                if (displayMb)
                {
                    data = new StringTextComponent(API.instance().getQuantityFormatter().formatInBucketForm((int) amount) + " ").func_230529_a_(data);
                }

                tooltip.add(data.func_230530_a_(Styles.GRAY));
            }
        }
    }
    
    @Nullable
    public static String getModNameByModId(String modId)
    {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modId);

        return modContainer.map(container -> container.getModInfo().getDisplayName()).orElse(null);
    }
    
    @SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
    public static void drawGasStack(MatrixStack matrixStack, int x, int y, GasStack stack)
    {
    	if(stack != null && !stack.isEmpty())
		{
    		Gas gas = stack.getType();
    		int tint = gas.getChemical().getTint();
    		
    		//float alpha = (float)(tint >> 24 & 255) / 255.0F;
    	    float red = (float)(tint >> 16 & 255) / 255.0F;
    	    float green = (float)(tint >> 8 & 255) / 255.0F;
    	    float blue = (float)(tint & 255) / 255.0F;
    	    
    		Minecraft.getInstance().getTextureManager().bindTexture(ResourceLocationRegister.mekanism_gas_texture);
    		RenderSystem.color4f(red, green, blue, 1);
    		Screen.blit(matrixStack, x, y, 0, ((System.currentTimeMillis()/100)%32)*16, 16, 16, 16, 512);
		}
    }
    
    private static IForgeRegistry<Gas> gasRegistry = null;
    
    public static int getGasId(Gas gas)
    {
    	if(gasRegistry == null)
    	gasRegistry = RegistryManager.ACTIVE.getRegistry(Gas.class);
    	
    	int id = 0;
    	for(Entry<ResourceLocation, Gas> element : gasRegistry.getEntries())
    	{
    		if(gas == element.getValue())
    			return id;
    		++id;
    	}
    	return 0;
    }
    
    public static byte getChemicalTypeId(Chemical<?> chemical)
    {
    	if(chemical instanceof Gas)
    		return 1;
    	if(chemical instanceof InfuseType)
    		return 2;
    	if(chemical instanceof Slurry)
    		return 3;
    	if(chemical instanceof Pigment)
    		return 4;
    	
    	return 0;
    }
    
    public static String getChemicalTypeString(Chemical<?> chemical)
    {
    	if(chemical instanceof Gas)
    		return "Gas";
    	if(chemical instanceof InfuseType)
    		return "Infuse Type";
    	if(chemical instanceof Slurry)
    		return "Slurry";
    	if(chemical instanceof Pigment)
    		return "Pigment";
    	
    	return "?";
    }
    
    public static Chemical<?> buildChemicalById(byte chemicalId, CompoundNBT nbtTags)
    {
    	if(chemicalId == 1)
    		return Gas.readFromNBT(nbtTags);
    	if(chemicalId == 2)
    		return InfuseType.readFromNBT(nbtTags);
    	if(chemicalId == 3)
    		return Slurry.readFromNBT(nbtTags);
    	if(chemicalId == 4)
    		return Pigment.readFromNBT(nbtTags);
    	
    	return null;
    }
    
    public static ChemicalStack<?> buildChemicalStackById(byte chemicalId, CompoundNBT nbtTags)
    {
    	if(chemicalId == 1)
    		return GasStack.readFromNBT(nbtTags);
    	if(chemicalId == 2)
    		return InfusionStack.readFromNBT(nbtTags);
    	if(chemicalId == 3)
    		return SlurryStack.readFromNBT(nbtTags);
    	if(chemicalId == 4)
    		return PigmentStack.readFromNBT(nbtTags);
    	
    	return null;
    }
    
    public static ChemicalStack<?> getEmpty(ChemicalStack<?> stack)
    {
    	if(stack != null)
		{
			if(stack instanceof GasStack)
				return GasStack.EMPTY;
			if(stack instanceof InfusionStack)
				return InfusionStack.EMPTY;
			if(stack instanceof SlurryStack)
				return SlurryStack.EMPTY;
			if(stack instanceof PigmentStack)
				return PigmentStack.EMPTY;	
		}
    	return GasStack.EMPTY;
    }
}
