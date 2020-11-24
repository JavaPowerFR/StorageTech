package javapower.storagetech.mekanism.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.screen.BaseScreen;

import javapower.storagetech.api.TooltipRenderer;
import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.mekanism.inventory.ChemicalFilterSlot;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
/**
 * 
 * Allow GasStack implementation 
 *
 */
public abstract class BaseScreen2<T extends Container> extends BaseScreen<T>
{

	public BaseScreen2(T container, int xSize, int ySize, PlayerInventory inventory, ITextComponent title)
	{
		super(container, xSize, ySize, inventory, title);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float renderPartialTicks, int mouseX, int mouseY)
	{
		super.drawGuiContainerBackgroundLayer(matrixStack, renderPartialTicks, mouseX, mouseY);
		
		for(Slot slot : container.inventorySlots)
        {
        	if(slot != null && slot instanceof ChemicalFilterSlot)
        	{
        		ChemicalFilterSlot gfs = (ChemicalFilterSlot) slot;
        		ChemicalStack<?> stack = gfs.getChemicalInSlot();
        		if(stack != null && !stack.isEmpty())
        		{
	        		//Gas gas = stack.getType();
	        		int tint = stack.getType().getTint();
	        		
	        	    float red = (float)(tint >> 16 & 255) / 255.0F;
	        	    float green = (float)(tint >> 8 & 255) / 255.0F;
	        	    float blue = (float)(tint & 255) / 255.0F;
	        	    
	        		minecraft.getTextureManager().bindTexture(ResourceLocationRegister.mekanism_gas_texture);
	        		RenderSystem.color4f(red, green, blue, 1);
	        		blit(matrixStack, guiLeft + slot.xPos, guiTop + slot.yPos, 0, ((System.currentTimeMillis()/100)%32)*16, 16, 16, 16, 512);
        		}
        	}
        }
	}
	
	@Override
	public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		if(hoveredSlot != null && hoveredSlot instanceof ChemicalFilterSlot)
        {
        	ChemicalFilterSlot gfs = (ChemicalFilterSlot) hoveredSlot;
    		ChemicalStack<?> stack = gfs.getChemicalInSlot();
    		if(stack != null && !stack.isEmpty())
    		{
    			List<ITextProperties> textLines = new ArrayList<>();
				ILangEntry type = MekanismLang.LIQUID;
				
	            if (stack instanceof GasStack)
	            {
	                type = MekanismLang.GAS;
	            }
	            else if (stack instanceof InfusionStack)
	            {
	                type = MekanismLang.INFUSE_TYPE;
	            }
	            else if (stack instanceof PigmentStack)
	            {
	                type = MekanismLang.PIGMENT;
	            }
	            else if (stack instanceof SlurryStack)
	            {
	                type = MekanismLang.SLURRY;
	            }
				
	            textLines.add(type.translateColored(EnumColor.YELLOW, EnumColor.ORANGE, stack.getTextComponent()));
				
				TooltipRenderer.drawHoveringText(textLines, mouseX, mouseY, matrixStack);
    		}
        }
	}

}
