package javapower.storagetech.mekanism.grid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.util.RenderUtils;

import javapower.storagetech.api.TooltipRenderer;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ClientChemicalGrid
{
	List<ChemicalStack<?>> chemicals = new ArrayList<>();
	List<ChemicalStack<?>> view = new ArrayList<>();
	
	FilterTab[] filters = new FilterTab[4];
	int tabId = 0;
	
	TextFieldWidget searchField;
	
	ScreenChemicalGrid parent;
	Slider slider;
	
	public ClientChemicalGrid(ScreenChemicalGrid parent)
	{
		this.parent = parent;
		slider = new Slider(1, 1, 174, 19);
		searchField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 80, 6, 88, 10, new StringTextComponent(""));
		searchField.setEnableBackgroundDrawing(false);
		searchField.setResponder((s) ->
		{
			TileDataManager.setParameter(TileEntityChemicalGrid.SEARCH_STRING, s);
		});
		searchField.setFocused2(TileEntityChemicalGrid.SEARCH_BOX_MODE.getValue() == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED);
	}

	public void sortGrid()
	{
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
		filters = parent.getContainer().getFilter();
		chemicals = _chemicals;
		searchField.setText(_search);
		sortGrid();
	}

	public void filter()
	{
		if(tabId == -1)
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
			
			searchUpdate(false);
		}
		else
		{
			FilterTab tab = filters[tabId];
			if(tab != null)
			{
				view.clear();
				chemicals.forEach((v) ->
				{
					if(tab.whiteList)
					{
						if(tabContain(tab, v.getType()))
							view.add(v);
					}
					else
					{
						if(!tabContain(tab, v.getType()))
							view.add(v);
					}
				});
			}
			
			searchUpdate(true);
		}
	}
	
	private boolean tabContain(FilterTab tab, Chemical<?> ch)
	{
		for(Chemical<?> c : tab.filters)
			if(c.equals(ch))
				return true;
		return false;
	}

	public void searchUpdate(boolean byPass)
	{
		if(!byPass)
		{
			String search = searchField.getText();
			if(search != null && search.length() > 0)
			{
				if(search.startsWith("@"))
				{
					if(search.length() > 1)
					{
						String modname = search.substring(1);
						List<ChemicalStack<?>> tempView = new ArrayList<>();
						
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
					List<ChemicalStack<?>> tempView = new ArrayList<>();
					view.forEach((v) ->
					{
						if(StringUtils.containsIgnoreCase(v.getTextComponent().getString(), search))
							tempView.add(v);
					});
					
					view = tempView;
				}
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
		//System.out.println("txt: "+v);
		searchField.setText(v);
		filter();
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
	
	public void tick()
	{
		searchField.tick();
	}
	
	public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		int index = (int)slider.index;
		int YVR = getVisibleRows();
		
		renderGrid(matrixStack, x, y, mouseX, mouseY, YVR, index);
		
		int offset = 0;
		for(FilterTab filter : filters)
		{
			if(filter != null)
			{
				filter.drawTab(minecraft, parent, matrixStack, x + offset*30 + 4, y - 28, mouseX, mouseY, offset == tabId);
			}
			++offset;
		}
	}

	public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		slider.render(parent, matrixStack);
		searchField.render(matrixStack, mouseX, mouseY, 1);
		
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
						textLinesSmall.add(stack.getAmount()+" mB total");
						
						RenderUtils.drawTooltipWithSmallText(matrixStack, textLines, textLinesSmall, true, ItemStack.EMPTY, mouseX, mouseY, parent.width, parent.height, parent.getMinecraft().fontRenderer);
					}
				}
			}
		}
		
		if(StorageTech.isShowInformation())
		{
			List<ITextProperties> textLines = new ArrayList<>();
			textLines .add(new TranslationTextComponent("info.storagetech.chemicalgrid.l0"));
			textLines.add(new TranslationTextComponent("info.storagetech.chemicalgrid.l1"));
			textLines.add(new TranslationTextComponent("info.storagetech.chemicalgrid.l2"));
			TooltipRenderer.drawHoveringText(textLines, -20, 35, 227, matrixStack);
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
	
	public static void drawStack(MatrixStack matrixStack, int x, int y, ChemicalStack<?> stack)
    {
    	if(stack != null && !stack.isEmpty())
		{
    		int tint = stack.getType().getChemical().getTint();
    		
    	    float red = (float)(tint >> 16 & 255) / 255.0F;
    	    float green = (float)(tint >> 8 & 255) / 255.0F;
    	    float blue = (float)(tint & 255) / 255.0F;
    	    
    		Minecraft.getInstance().getTextureManager().bindTexture(ResourceLocationRegister.mekanism_gas_texture);
    		GL11.glColor4f(red, green, blue, 1);
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
		
		searchField.mouseReleased(x2 - parent.getGuiLeft(), y2 - parent.getGuiTop(), button);
		
		if(TileEntityChemicalGrid.SEARCH_BOX_MODE.getValue() == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED)
			searchField.setFocused2(true);
	}

	public void mouseClicked(double xm, double ym, int button)
	{
		if(button == 0)
		{
			int _tabId = 0;
			for(FilterTab tab : filters)
			{
				if(tab != null)
				{
					if(tab.isOnTab(parent.getGuiLeft() + _tabId*30 + 4, parent.getGuiTop() - 26, (int)xm, (int)ym))
					{
						if(_tabId == tabId)
							TileDataManager.setParameter(TileEntityChemicalGrid.TAB_ID, -1);
						else
							TileDataManager.setParameter(TileEntityChemicalGrid.TAB_ID, _tabId);
					}
				}
				++_tabId;
			}
		}
		
		slider.mouseClicked(xm - parent.getGuiLeft(), ym - parent.getGuiTop(), button);
		
		searchField.mouseClicked(xm - parent.getGuiLeft(), ym - parent.getGuiTop(), button);
		
		if(TileEntityChemicalGrid.SEARCH_BOX_MODE.getValue() == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED)
			searchField.setFocused2(true);
	}

	public void mouseScrolled(double xm, double ym, double value)
	{
		slider.mouseScrolled(xm - parent.getGuiLeft(), ym - parent.getGuiTop(), value);
	}
	
	public void charTyped(char c, int id)
	{
		searchField.charTyped(c, id);
	}
	
	public void keyPressed(int a, int b, int c)
	{
		searchField.keyPressed(a, b, c);
	}
	
	public void slotClick(int id, int dragType, ClickType clickType, PlayerEntity player, VirtualSlot slotTarget, ItemStack stackHeld)
	{
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
	}

	public void updateSlotFilter()
	{
		filters = parent.getContainer().getFilter();
		setTab();
	}
	
	public void setTab()
	{
		tabId = TileEntityChemicalGrid.TAB_ID.getValue();
		if(tabId != -1 && filters[tabId] == null)
			TileDataManager.setParameter(TileEntityChemicalGrid.TAB_ID, -1);
		filter();
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
}
