package javapower.storagetech.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.container.ContainerStructureConstructor;
import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodeStructureConstructor;
import javapower.storagetech.packet.PacketStructureConstructor;
import javapower.storagetech.screen.widget.ButtonDefiner;
import javapower.storagetech.screen.widget.ButtonElement;
import javapower.storagetech.screen.widget.ButtonElementToggle;
import javapower.storagetech.screen.widget.HologramSideButton;
import javapower.storagetech.screen.widget.PauseSideButton;
import javapower.storagetech.screen.widget.ProcessingModeSideButton;
import javapower.storagetech.screen.widget.ZeroSideButton;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import javapower.storagetech.util.BlockLocalPos;
import javapower.storagetech.util.SCElement;
import javapower.storagetech.util.Variable;
import javapower.storagetech.util.Vector2i;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenStructureConstructor extends BaseScreen<ContainerStructureConstructor>
{
	public static final ResourceLocation GUI_SPRITE = ResourceLocationRegister.resource("textures/guis/structure_constructor.png");
	
	public static final ButtonDefiner BUTTONDEF_UP = new ButtonDefiner(171, 222, 16, 11, 203, 222, GUI_SPRITE);
	public static final ButtonDefiner BUTTONDEF_DOWN = new ButtonDefiner(171, 233, 16, 11, 203, 222, GUI_SPRITE);
	public static final ButtonDefiner BUTTONDEF_ADD = new ButtonDefiner(187, 222, 16, 10, 203, 222, GUI_SPRITE);
	public static final ButtonDefiner BUTTONDEF_SBS = new ButtonDefiner(187, 232, 16, 10, 203, 222, GUI_SPRITE);
	public static final ButtonDefiner BUTTONDEF_DEL = new ButtonDefiner(171, 244, 11, 11, 203, 222, GUI_SPRITE);
	public static final ButtonDefiner BUTTONDEF_DROP = new ButtonDefiner(182, 244, 11, 11, 203, 222, GUI_SPRITE);
	public static final ButtonDefiner BUTTONDEF_PLACE = new ButtonDefiner(193, 244, 11, 11, 203, 222, GUI_SPRITE);
	
	
	private final String texture;
	
	private Slider slider;
	private List<ElementWidget> elementsWidget = new ArrayList<ElementWidget>();
	protected ItemStack mouseStack = ItemStack.EMPTY;
	private Vector2i mouse_local = new Vector2i(0,0);
	
    public ScreenStructureConstructor(ContainerStructureConstructor container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 250, 222, inventory, title);
        texture = "guis/structure_constructor.png";
        slider = new Slider(4, 88, 196, 20);
    }
    
    @Override
    public void onPostInit(int x, int y)
    {
    	PacketStructureConstructor.toServer_requestClientUpdate(container.tileNode.getPos());
    	
    	addSideButton(new RedstoneModeSideButton(this, TileEntityStructureConstructor.REDSTONE_MODE));
    	addSideButton(new ProcessingModeSideButton(this, TileEntityStructureConstructor.PROCESSING_MODE));
    	addSideButton(new PauseSideButton(this, TileEntityStructureConstructor.WORKING));
    	addSideButton(new ZeroSideButton(this, () -> 
    	{
    		TileDataManager.setParameter(TileEntityStructureConstructor.INDEX_PROCESS, 0);
    	}));
    	addSideButton(new HologramSideButton(this, container.tileNode.show_hologram));
    	
    	addButton(new Button(169+x, 111+y, 40, 20, new TranslationTextComponent("widget.storagetech.save"), (b) ->
    	{
    		boolean dirty = false;
    		for(SCElement.Client e : container.tileNode.elements_client)
    		{
    			if(e.isClientDirty)
    			{
    				e.isClientDirty = false;
    				dirty = true;
    			}
    		}
    		
    		if(dirty)
    		{
    			PacketStructureConstructor.toServer_requestUpdateAll(container.tileNode.getPos(), container.tileNode.elements_client);
    		}
    	}));
    	
    	addButton(new Button(127+x, 111+y, 40, 20, new TranslationTextComponent("widget.storagetech.add"), (b) ->
    	{
    		SCElement.Client new_element = new SCElement.Client(new BlockLocalPos(0,0,1), false, ItemStack.EMPTY);
    		new_element.isClientDirty = true;
    		container.tileNode.elements_client.add(new_element);
    		updateElements();
    	}));
    	
    	updateElements();
    }

    @Override
    public void tick(int x, int y)
    {
    	mouseStack = minecraft.player.inventory.getItemStack();
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(StorageTech.MODID, texture);
        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
    	mouse_local.x = mouseX;
    	mouse_local.y = mouseY;
    	
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 27, 127, I18n.format("container.inventory"));
        
        renderString(matrixStack, 8, 110, I18n.format("misc.storagetech.step",TileEntityStructureConstructor.INDEX_PROCESS.getValue()+1,container.tileNode.elements_client.size()));
        
        slider.render(this, matrixStack);
        int yOffset = 0;
        for(int i = 0; i < 4; ++i)
        {
        	int internalIndex = i+((int)slider.index);
        	
        	if(internalIndex < elementsWidget.size())
        	{
        		elementsWidget.get(i).render(this, matrixStack, 8, 20+(22*yOffset), mouseX, mouseY, 0);
        		++yOffset;
        	}
        }
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double value)
    {
    	
    	slider.mouseScrolled(x - guiLeft, y - guiTop, value);
    	
    	return super.mouseScrolled(x, y, value);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button)
    {
    	slider.mouseClicked(x - guiLeft, y - guiTop, button);
    	
    	int yOffset = 0;
        for(int i = 0; i < 4; ++i)
        {
        	int internalIndex = i+((int)slider.index);
        	
        	if(internalIndex < elementsWidget.size())
        	{
        		double local_x = x - guiLeft - 8;
        		double local_y = (y - guiTop - 20) - 22*yOffset;
        		elementsWidget.get(i).mouseClicked(this, local_x, local_y, button);
        		++yOffset;
        	}
        }
    	
    	return super.mouseClicked(x, y, button);
    }
    
    @Override
    public boolean mouseReleased(double x, double y, int button)
    {
    	slider.mouseReleased(x - guiLeft, y - guiTop, button);
    	
    	return super.mouseReleased(x, y, button);
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int b, double dx, double dy)
    {
    	slider.mouseDragged(x - guiLeft, y - guiTop, b, dx, dy);
    	
    	return super.mouseDragged(x, y, b, dx, dy);
    }
    
    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_)
    {
    	
    	return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

	public void updateElements()
	{
		elementsWidget.clear();
		for(int index = 0; index < container.tileNode.elements_client.size(); ++index)
		{
			elementsWidget.add(new ElementWidget(this, container.tileNode.getPos(), container.tileNode.elements_client, container.tileNode.elements_client.get(index), index));
		}
		slider.recalculate(elementsWidget.size());
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
		
		public void render(ScreenStructureConstructor screen, MatrixStack matrixIn)
		{
			screen.minecraft.getTextureManager().bindTexture(GUI_SPRITE);
			if(canUse)
				screen.blit(matrixIn, 196, (int) (20 + ((sliderHeight-15)*index)/maxIndex), 219, 222, 12, 15);
		}

		public void recalculate(int _maxvalue)
		{
			canDrag = false;
			maxvalue = _maxvalue;
			index = 0;
			maxIndex = Math.max(0, maxvalue - elementsDisplayable);
			canUse = maxIndex > 0;
			if(maxIndex > 0)
				step = (sliderHeight - 15)/maxIndex;
			else
				step = 0;
		}
	}
	
	public static class ElementWidget
	{
		Widget[] widgets;
		SCElement.Client element;
		
		public ElementWidget(ScreenStructureConstructor screen, BlockPos masterPos, List<SCElement.Client> elements, SCElement.Client _element, int index)
		{
			element = _element;
			widgets = new Widget[]
					{
						new ButtonElement(0, 0, BUTTONDEF_UP, () ->
						{
							if(index > 0)
							{
								SCElement.Client clientElement = elements.remove(index);
								if(clientElement != null)
								{
									elements.add(index - 1, clientElement);
									clientElement.isClientDirty = true;
									screen.updateElements();
								}
							}
						}, screen.mouse_local),
						new ButtonElement(0, 11, BUTTONDEF_DOWN, () ->
						{
							if(index + 1 < elements.size())
							{
								SCElement.Client clientElement = elements.remove(index);
								if(clientElement != null)
								{
									elements.add(index + 1, clientElement);
									clientElement.isClientDirty = true;
									screen.updateElements();
								}
							}
						}, screen.mouse_local),
						
						//I POS
						new ButtonElement(38, 1, BUTTONDEF_ADD, () ->
						{
							element.localPos.setI(Math.min(element.localPos.getI()+1, NetworkNodeStructureConstructor.MAX_IJ));
							element.isClientDirty = true;
						}, screen.mouse_local),
						new ButtonElement(38, 11, BUTTONDEF_SBS, () ->
						{
							element.localPos.setI(Math.max(element.localPos.getI()-1, NetworkNodeStructureConstructor.MIN_IJ));
							element.isClientDirty = true;
						}, screen.mouse_local),
						
						//J POS
						new ButtonElement(84, 1, BUTTONDEF_ADD, () -> 
						{
							element.localPos.setJ(Math.min(element.localPos.getJ()+1, NetworkNodeStructureConstructor.MAX_IJ));
							element.isClientDirty = true;
						}, screen.mouse_local),
						new ButtonElement(84, 11, BUTTONDEF_SBS, () ->
						{
							element.localPos.setJ(Math.max(element.localPos.getJ()-1, NetworkNodeStructureConstructor.MIN_IJ));
							element.isClientDirty = true;
						}, screen.mouse_local),
						
						//K POS
						new ButtonElement(130, 1, BUTTONDEF_ADD, () ->
						{
							element.localPos.setK(Math.min(element.localPos.getK()+1, NetworkNodeStructureConstructor.MAX_K));
							element.isClientDirty = true;
						},screen.mouse_local),
						
						new ButtonElement(130, 11, BUTTONDEF_SBS, () ->
						{
							element.localPos.setK(Math.max(element.localPos.getK()-1, NetworkNodeStructureConstructor.MIN_K));
							element.isClientDirty = true;
						}, screen.mouse_local),
						
						//DELET
						new ButtonElement(176, 0, BUTTONDEF_DEL, () ->
						{
							elements.remove(index);
							if(elements.size() > 0)
							{
								elements.get(0).isClientDirty = true;
								screen.updateElements();
							}
							else
							{
								PacketStructureConstructor.toServer_requestUpdateAll(masterPos, elements);
							}
						}, screen.mouse_local),
						
						//MODE
						new ButtonElementToggle(176, 11, BUTTONDEF_PLACE, BUTTONDEF_DROP, new Variable<Boolean>()
						{
							
							@Override
							public void set(Boolean v)
							{
								element.drop = v;
								element.isClientDirty = true;
							}
							
							@Override
							public Boolean get()
							{
								return element.drop;
							}
						}, screen.mouse_local),
						
					};
		}
		
		public void render(ScreenStructureConstructor screen, MatrixStack matrixIn, int offsetX, int offsetY, int mouseX, int mouseY, float partialTick)
		{
			screen.minecraft.getTextureManager().bindTexture(GUI_SPRITE);
			
			//show element braket
	        screen.blit(matrixIn, offsetX+16, offsetY, 0, 222, 171, 22);
	        
	        screen.renderString(matrixIn, offsetX + 56, offsetY + 7, ""+element.localPos.getI());
	        screen.renderString(matrixIn, offsetX + 102, offsetY + 7, ""+element.localPos.getJ());
	        screen.renderString(matrixIn, offsetX + 148, offsetY + 7, ""+element.localPos.getK());
	        
			for(Widget w : widgets)
				w.render(matrixIn, offsetX, offsetY, partialTick);
			
			if(element.stack != null)
			{
				screen.minecraft.getItemRenderer().renderItemIntoGUI(element.stack, offsetX + 19, offsetY + 3);
				if(mouseX >= offsetX + 19 && mouseX <= offsetX + 35 && mouseY >= offsetY + 3 && mouseY <= offsetY + 19)
				{
					Screen.fill(matrixIn, offsetX + 19, offsetY + 3, offsetX + 35, offsetY + 19, 0x55ffffff);
				}
			}
		}
		
		public boolean mouseClicked(ScreenStructureConstructor screen, double x, double y, int b)
		{
			boolean returnResult = false;
			for(Widget w : widgets)
				if(w.mouseClicked(x, y, b))
					returnResult = true;
			
			if(x >= 19 && x <= 35 && y >= 3 && y <= 19)
			{
				screen.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				
				if(screen.mouseStack != null)
				{
					element.stack = screen.mouseStack.copy();
					element.stack.setCount(1);
					element.isClientDirty = true;
				}
				else
				{
					element.stack = ItemStack.EMPTY;
					element.isClientDirty = true;
				}
			}
			return returnResult;
		}
		
	}
}