package javapower.storagetech.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import javapower.storagetech.container.ContainerPartsCombiner;
import javapower.storagetech.core.ClientConfig;
import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.setup.ClientSetup;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class ScreenPartsCombiner extends ContainerScreen<ContainerPartsCombiner>
{
	public ScreenPartsCombiner(ContainerPartsCombiner _screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(_screenContainer, inv, titleIn);
		
		ClientConfig.loadConfig();
	    this.passEvents = false;
	}
	
	@Override
	public void init()
	{
		this.xSize = 210;
	    this.ySize = 178;
		super.init();
		
		playerInventoryTitleY = ySize - 94;
	}
	
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
	    minecraft.getTextureManager().bindTexture(ResourceLocationRegister.parts_combiner);
	    blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);
	    
	    // render progress bar (0 -> 22)
	    int value = container.tile.getProcessUpdate();
	    
	    if(value > 0)
	    blit(matrix, guiLeft + 97, guiTop + 42, 1, 239, value, 17);
	    
	    // render energy bar (0 -> 59)
	    
	    blit(matrix, guiLeft + 7, guiTop + 20, 0, 178, 18, 61);
	    
	    int val = (container.tile.getEnergy().energy*59) / container.tile.getEnergy().capacity;
	    
	    if(val > 0)
	    {
	    	blit(matrix, guiLeft + 8, guiTop + 21 + (59 - val), 18, 178, 16, val);
	    }
	    
	    if(isOnAera(guiLeft + 8, guiTop + 21, mouseX, mouseY, 16, 59))
	    {
	    	List<ITextComponent> list = new ArrayList<ITextComponent>();
        	list.add(new TranslationTextComponent(ClientSetup.formatter.format(container.tile.getEnergy().energy)+" FE /"+ClientSetup.formatter.format(container.tile.getEnergy().capacity)+" FE"));
        	GuiUtils.drawHoveringText(matrix, list, mouseX, mouseY, minecraft.currentScreen.width, minecraft.currentScreen.height, 200, font);
	    }
	    
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
	{
		super.drawGuiContainerForegroundLayer(matrixStack, x, y);
	}
	
	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(matrix);
	    super.render(matrix, mouseX, mouseY, partialTicks);
	    this.renderHoveredTooltip(matrix, mouseX, mouseY);
	    //this.func_230459_a_(matrix, mouseX, mouseY);
	}
	
	private boolean isOnAera(int x, int y, int mouseX, int mouseY, int width, int height)
	{
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
