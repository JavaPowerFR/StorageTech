package javapower.storagetech.mekanism.grid;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.util.IFilter;

import javapower.storagetech.api.TooltipRenderer;
import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.mekanism.item.ItemChemicalFilter;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class FilterTab
{
	boolean hasIcon = false;
	int iconTinit = 0;
	String title = "";
	boolean whiteList = false;
	List<Chemical<?>> filters;
	
	public FilterTab(boolean _hasIcon, int _iconTinit, String _title, boolean _whiteList, List<Chemical<?>> _filters)
	{
		hasIcon = _hasIcon;
		iconTinit = _iconTinit;
		title = _title;
		whiteList = _whiteList;
		filters = _filters;
	}
	
	public static FilterTab getFilterTab(ItemStack stack)
	{
		if(stack != null && ! stack.isEmpty() && stack.getItem() instanceof ItemChemicalFilter)
		{
			boolean _hasIcon = false;
			int _iconTinit = 0;
			
			String _title = ItemChemicalFilter.getName(stack);
			boolean _whiteList = ItemChemicalFilter.getMode(stack) == IFilter.MODE_WHITELIST;
			
			List<Chemical<?>> _filters = new ArrayList<Chemical<?>>();
			
			ChemicalStack<?> icon = ItemChemicalFilter.getGasIcon(stack);
			if(icon != null && ! icon.isEmpty())
			{
				_iconTinit = icon.getType().getTint();
				_hasIcon = true;
				if(_title.length() <= 0)
				{
					_title = icon.getTextComponent().getString();
				}
			}
			
			ChemicalStack<?>[] filters = ItemChemicalFilter.getFilters(stack);
			if(filters != null)
			{
				for(ChemicalStack<?> cs : filters)
				{
					if(cs != null && !cs.isEmpty())
					{
						_filters.add(cs.getType());
						if(!_hasIcon)
						{
							_iconTinit = cs.getType().getTint();
							_hasIcon = true;
							if(_title.length() <= 0)
							{
								_title = cs.getTextComponent().getString();
							}
						}
					}
				}
			}
			
			if(_filters.size() <= 0)
				return null;
			
			return new FilterTab(_hasIcon, _iconTinit, _title, _whiteList, _filters);
		}
		
		return null;
	}

	public void drawTab(Minecraft minecraft, ScreenChemicalGrid screen, MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, boolean isSelected)
	{
		minecraft.getTextureManager().bindTexture(ScreenChemicalGrid.GUI_SPRITE);
		y += 2;
		GL11.glColor4f(1, 1, 1, 1);
		screen.blit(matrixStack, x, y, 228, isSelected ? 43 : 15, 28, isSelected ? 29 : 26);
		
		drawStack(matrixStack, x + 6, y + 6);
		if(mouseX >= x && mouseX <= x + 27 && mouseY >= y && mouseY < y + 26)
		TooltipRenderer.drawHoveringText(new StringTextComponent(title), mouseX - 4, y + 43, matrixStack);
		
		GL11.glColor4f(1, 1, 1, 1);
	}
	
	public boolean isOnTab(int x, int y, int mouseX, int mouseY)
	{
		return mouseX >= x && mouseX <= x + 27 && mouseY >= y && mouseY < y + 26;
	}
	
	public void drawStack(MatrixStack matrixStack, int x, int y)
    {
    	if(hasIcon)
		{
    	    float red = (float)(iconTinit >> 16 & 255) / 255.0F;
    	    float green = (float)(iconTinit >> 8 & 255) / 255.0F;
    	    float blue = (float)(iconTinit & 255) / 255.0F;
    	    
    		Minecraft.getInstance().getTextureManager().bindTexture(ResourceLocationRegister.mekanism_gas_texture);
    		GL11.glColor4f(red, green, blue, 1);
    		Screen.blit(matrixStack, x, y, 0, ((System.currentTimeMillis()/100)%32)*16, 16, 16, 16, 512);
		}
    }
}
