package javapower.storagetech.mekanism.grid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.util.RenderUtils;

import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.api.MekanismUtils;
import javapower.storagetech.mekanism.container.VirtualSlot;
import javapower.storagetech.mekanism.packet.PacketChemicalGridHeldStack;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalGrid;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mezz.jei.Internal;
import mezz.jei.api.helpers.IModIdHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;

public class ClientChemicalGrid
{
	List<ChemicalStack<?>> chemicals = new ArrayList<>();
	/*TreeMap<Chemical<?>, Long>*/List<ChemicalStack<?>> view = new ArrayList<>();
	
	String search = "";
	
	ScreenChemicalGrid parent;
	Slider slider;
	
	public ClientChemicalGrid(ScreenChemicalGrid parent)
	{
		this.parent = parent;
		slider = new Slider(1, 1, 174, 19);
	}

	public void sortGrid()
	{
		System.out.println(chemicals);
		chemicals.removeIf((a) -> a == null || a.isEmpty());
		
		int sorting_type = TileEntityChemicalGrid.SORTING_TYPE.getValue();
		int sorting_direction = TileEntityChemicalGrid.SORTING_DIRECTION.getValue();
		
		if(sorting_type == 0)//Quantity
		{
			chemicals.sort((left , right) ->
			{
				long leftSize = left.getAmount();
				long rightSize = right.getAmount();
				
				if (leftSize != rightSize)
				{
		            if (sorting_direction == 0)
		                return (leftSize > rightSize) ? 1 : -1;
		            else
		                return (rightSize > leftSize) ? 1 : -1;
		        }
				
				return 0;
			});
		}
		else if(sorting_type == 1)//Name
		{
			chemicals.sort((left , right) ->
			{
				String leftName = left.getTextComponent().getString();
		        String rightName = right.getTextComponent().getString();
		        
		            if (sorting_direction == 0)
		                return leftName.compareTo(rightName);
		            else
		                return rightName.compareTo(leftName);
			});
		}
		else if(sorting_type == 2)//Type
		{
			chemicals.sort((left , right) ->
			{
				String leftName = left.getType().getClass().getSimpleName();
		        String rightName = right.getType().getClass().getSimpleName();
		        
		            if (sorting_direction == 0)
		                return leftName.compareTo(rightName);
		            else
		                return rightName.compareTo(leftName);
			});
		}
		
		filter();
	}

	public void initalize(List<ChemicalStack<?>> _chemicals, String _search)
	{
		chemicals = _chemicals;
		search = _search;
		sortGrid();
	}

	public void filter()
	{
		int view_filter = TileEntityChemicalGrid.VIEW_CHEMICAL_TYPE.getValue();
		
		view.clear();
		chemicals.forEach((v) ->
		{
			if(view_filter == -1)
				view.add(v);
			else if(view_filter+1 == MekanismUtils.getChemicalTypeId(v.getType()))
					view.add(v);
		});
		
		searchUpdate();
	}

	public void searchUpdate()
	{
		//int search_box_mode = TileEntityChemicalGrid.SEARCH_BOX_MODE.getValue();
		if(search != null && search.length() > 0)
		{
			if(search.startsWith("@"))
			{
				if(search.length() > 1)
				{
					String modname = search.substring(1);
					/*TreeMap<Chemical<?>, Long>*/List<ChemicalStack<?>> tempView = new ArrayList<>();
					
					view.forEach((v) ->
					{
						if(StringUtils.containsIgnoreCase(v.getTypeRegistryName().getNamespace(), modname))
							tempView.add(v);
					});
					
					view = tempView;
				}
			}
			else
			{
				/*TreeMap<Chemical<?>, Long>*/List<ChemicalStack<?>> tempView = new ArrayList<>();
				view.forEach((v) ->
				{
					if(StringUtils.containsIgnoreCase(v.getTextComponent().getString(), search))
						tempView.add(v);
				});
				
				view = tempView;
			}
		}
		float viewElements = view.size()/9f;
		if(viewElements > ((int)viewElements))
			++viewElements;
		
		slider.recalculate((int) viewElements, true);
	}

	public void updateStack(Chemical<?> chemical, long amnt)
	{
		if(amnt == 0)
		{
			boolean isprensent = false;
			int index = 0;
			
			for(ChemicalStack<?> stack : chemicals)
			{
				if(stack.getType().equals(chemical))
				{
					isprensent = true;
					break;
				}
				++index;
			}
			
			if(isprensent)
			{
				chemicals.remove(index);
			}
		}
		else
		{
			boolean isprensent = false;
			int index = 0;
			
			for(ChemicalStack<?> stack : chemicals)
			{
				if(stack.getType().equals(chemical))
				{
					isprensent = true;
					break;
				}
				++index;
			}
			
			if(isprensent)
			{
				chemicals.get(index).setAmount(amnt);
			}
			else
			{
				chemicals.add(chemical.getStack(amnt));
			}
		}
		
		sortGrid();
	}

	public void setSearch(String v)
	{
		search = v;
		searchUpdate();
	}

	public int getVisibleRows()
	{
		switch (TileEntityChemicalGrid.SIZE.getValue())
		{
        case IGrid.SIZE_STRETCH:
            int screenSpaceAvailable = parent.height - 118;
            return Math.max(3, Math.min((screenSpaceAvailable / 18) - 3, RS.CLIENT_CONFIG.getGrid().getMaxRowsStretch()));
        case IGrid.SIZE_SMALL:
            return 3;
        case IGrid.SIZE_MEDIUM:
            return 5;
        case IGrid.SIZE_LARGE:
            return 8;
        default:
            return 3;
		}
	}
	
	public void guiInit(int yPlayerInventory)
	{
		int sliderVisibleRows = getVisibleRows();
		
		slider.elementsDisplayable = sliderVisibleRows;
		slider.sliderHeight = (sliderVisibleRows*18)-2;
		
		float viewElements = view.size()/9f;
		if(viewElements > ((int)viewElements))
			++viewElements;
		
		slider.recalculate((int) viewElements, false);
	}
	
	public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
	{
		int index = (int)slider.index;
		int YVR = getVisibleRows();
		
		renderGrid(matrixStack, x, y, mouseX, mouseY, YVR, index);
	}

	public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		slider.render(parent, matrixStack);
		
		int index = (int)slider.index;
		int YVR = getVisibleRows();
		
		int xSlot = (mouseX-7);
		int ySlot = (mouseY-18);
		if(xSlot >= 0 && ySlot >= 0)
		{
			int xSlotId = xSlot / 18;
			if(xSlotId < 9)
			{
				int ySlotId = ySlot /18;
				if(ySlotId < YVR)
				{
					int slotId = xSlotId + ySlotId*9 + index*9;
					if(slotId < view.size())
					{
						ChemicalStack<?> stack = view.get(slotId);
						List<ITextProperties> textLines = new ArrayList<>();
						List<String> textLinesSmall = new ArrayList<>();
						
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
						//textLines.add(new StringTextComponent(stack.getAmount()+" mB total").func_230530_a_(Styles.GRAY));
						textLinesSmall.add(stack.getAmount()+" mB total");
						
						IModIdHelper modIdHelper = Internal.getHelpers().getModIdHelper();
						if (modIdHelper.isDisplayingModNameEnabled())
						{
							String modName = modIdHelper.getFormattedModNameForModId(stack.getTypeRegistryName().getNamespace());
							textLinesSmall.add(modName);
						}
						
						//TooltipRenderer.drawHoveringText(textLines, mouseX, mouseY, matrixStack);
						RenderUtils.drawTooltipWithSmallText(matrixStack, textLines, textLinesSmall, true, ItemStack.EMPTY, mouseX, mouseY, parent.width, parent.height, parent.getMinecraft().fontRenderer);
					}
				}
			}
		}
		
	}
	
	public void renderGrid(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, int YVR, int index)
	{
		int color = RenderSettings.INSTANCE.getSecondaryColor();
		for(int yOffset = 0; yOffset < YVR; ++yOffset)
		{
			for(int xOffset = 0; xOffset < 9; ++xOffset)
			{
				int chemicalIndex = index*9 + yOffset*9 + xOffset;
				if(chemicalIndex < view.size())
				{
					ChemicalStack<?> stack = view.get(chemicalIndex);
					drawStack(matrixStack, 8 + xOffset*18 + x, 19 + yOffset*18 + y, stack);
					parent.renderQuantity(matrixStack,  8 + xOffset*18 + x, 19 + yOffset*18 + y,
							API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero((int) stack.getAmount()), color);
				}
				else
					return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void drawStack(MatrixStack matrixStack, int x, int y, ChemicalStack<?> stack)
    {
    	if(stack != null && !stack.isEmpty())
		{
    		int tint = stack.getType().getChemical().getTint();
    		
    	    float red = (float)(tint >> 16 & 255) / 255.0F;
    	    float green = (float)(tint >> 8 & 255) / 255.0F;
    	    float blue = (float)(tint & 255) / 255.0F;
    	    
    		Minecraft.getInstance().getTextureManager().bindTexture(ResourceLocationRegister.mekanism_gas_texture);
    		RenderSystem.color4f(red, green, blue, 1);
    		Screen.blit(matrixStack, x, y, 0, ((System.currentTimeMillis()/100)%32)*16, 16, 16, 16, 512);
		}
    }
	
	public void mouseDragged(double x2, double y2, int b, double dx, double dy)
	{
		slider.mouseDragged(x2 - parent.getGuiLeft(), y2 - parent.getGuiTop(), b, dx, dy);
	}

	public void mouseReleased(double x2, double y2, int button)
	{
		slider.mouseReleased(x2 - parent.getGuiLeft(), y2 - parent.getGuiTop(), button);
	}

	public void mouseClicked(double xm, double ym, int button)
	{
		slider.mouseClicked(xm - parent.getGuiLeft(), ym - parent.getGuiTop(), button);
	}

	public void mouseScrolled(double xm, double ym, double value)
	{
		slider.mouseScrolled(xm - parent.getGuiLeft(), ym - parent.getGuiTop(), value);
	}
	
	public static class Slider
	{
		boolean canDrag = false;
		
		boolean canUse = false;
		int elementsDisplayable;
		int sliderHeight;
		int maxvalue;
		int maxIndex;
		float index;
		float step;
		int x,y;
		
		public Slider(int _elementsDisplayable, int _sliderHeight, int _x, int _y)
		{
			elementsDisplayable = _elementsDisplayable;
			sliderHeight = _sliderHeight;
			x = _x;
			y = _y;
		}
		
		public void mouseDragged(double x2, double y2, int b, double dx, double dy)
		{
			if(canDrag)
			{
				float val = (float) (index + dy*(maxIndex/(sliderHeight-15f)));
				index = Math.max(0f, Math.min(maxIndex, val));
			}
		}

		public void mouseReleased(double x2, double y2, int button)
		{
			if(button == 0)
				canDrag = false;
		}

		public void mouseClicked(double xm, double ym, int button)
		{
			canDrag = (xm > x && xm <= x + 12 && ym > y && ym <= y + sliderHeight) && button == 0;
		}

		public void mouseScrolled(double xm, double ym, double value)
		{
			if(xm > x && xm <= x + 12 && ym > y && ym <= y + sliderHeight)
			{
				index = (float) Math.max(0, Math.min(maxIndex, index - value));
			}
		}
		
		public void render(ScreenChemicalGrid screen, MatrixStack matrixIn)
		{
			screen.getMinecraft().getTextureManager().bindTexture(ScreenChemicalGrid.GUI_SPRITE);
			
			if(canUse)
				screen.blit(matrixIn, 174, (int) (19 + ((sliderHeight-15)*index)/maxIndex), 228, 0, 12, 15);
		}

		public void recalculate(int _maxvalue, boolean tryToKeepIndex)
		{
			canDrag = false;
			maxvalue = _maxvalue;
			maxIndex = Math.max(0, maxvalue - elementsDisplayable);
			if(tryToKeepIndex)
			{
				index = Math.min(index, maxIndex);
			}
			else
			{
				index = 0;
			}
			canUse = maxIndex > 0;
			if(maxIndex > 0)
				step = (sliderHeight - 15)/maxIndex;
			else
				step = 0;
		}
	}

	public void slotClick(int id, int dragType, ClickType clickType, PlayerEntity player, VirtualSlot slotTarget, ItemStack stackHeld)
	{
		// dragtype 0 = put , 1 = take
		if(stackHeld != null)
		{
			boolean shift = clickType == ClickType.QUICK_MOVE;
			int index = slotTarget.getSlotIndex() + ((int)slider.index)*9;
			
			if(index < view.size())
			{
				if(dragType == 2)
					return;
				
				boolean putInGrid = dragType == 0;
				StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketChemicalGridHeldStack(view.get(index).getType(), putInGrid, shift));
			}
			else
			{
				StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketChemicalGridHeldStack(null, true, shift));
			}
		}
		//System.out.println("\n***********************************\nId: "+id+"\nDragType: "+ dragType+ "\nClickType: "+clickType+"\nPlayer: "+player+ "\nMouse Stack: "+stackHeld +"\nSlot Target: "+ slotTarget.getSlotIndex()+"\n***********************************");
	}

}
