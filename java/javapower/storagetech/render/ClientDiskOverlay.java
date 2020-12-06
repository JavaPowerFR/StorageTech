package javapower.storagetech.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.item.blockitem.FluidStorageBlockItem;
import com.refinedmods.refinedstorage.item.blockitem.StorageBlockItem;

import javapower.storagetech.api.IItemProgressBarOverlay;
import javapower.storagetech.core.ClientConfig;
import javapower.storagetech.core.ResourceLocationRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientDiskOverlay
{
	public static Minecraft minecraft = Minecraft.getInstance();
	
	private static int tooltipX = 0;
	private static int tooltipY = 0;
	
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void drawTooltip(GuiContainerEvent.DrawForeground event)
    {
		if(ClientConfig.Value_overlayEnable)
		{
	    	
			Slot slot = event.getGuiContainer().getSlotUnderMouse();
			if(slot == null)
				return;
	    	ItemStack itemstack = slot.getStack();
	    	
	    	//tool tips pos
	    	initToolTipsPos(itemstack, getTooltipFromItem(itemstack), event.getMouseX(), event.getMouseY(),  event.getGuiContainer().width,  event.getGuiContainer().height, minecraft.fontRenderer);
	    	//end tool tips pos
	    	
	    	int xTB = - event.getGuiContainer().getGuiLeft() + tooltipX;
	    	int yTB = - event.getGuiContainer().getGuiTop() + tooltipY;
	    	
	    	if(itemstack != null && !itemstack.isEmpty())
	    	{
	    		if(itemstack.getItem() instanceof IStorageDiskProvider)
	    		{
		    		if(!((IStorageDiskProvider)itemstack.getItem()).isValid(itemstack))
		    			return;
		    		
		    		UUID uuid = ((IStorageDiskProvider)itemstack.getItem()).getId(itemstack);
		    		if(uuid == null)
		    			return;
		    		
		    		StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(uuid);
		    		if(data != null)
		    		{
		    			int capacity = data.getCapacity();
		    			float size = data.getStored()/(float)data.getCapacity();
		    			drawOverlay(xTB, yTB, capacity == -1 ? -1 : size, size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded);
		    		}
	    		}
	    		else if(itemstack.getItem() instanceof BlockItem)
	    		{
	    			if(itemstack.getItem() instanceof StorageBlockItem)
	    			{
	    				if(itemstack.hasTag() && itemstack.getTag().hasUniqueId(StorageNetworkNode.NBT_ID))
	    				{
	    					UUID uuid = itemstack.getTag().getUniqueId(StorageNetworkNode.NBT_ID);
	    					StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(uuid);
	    					
	    					if(data != null)
	    		    		{
	    		    			int capacity = data.getCapacity();
	    		    			float size = data.getStored()/(float)data.getCapacity();
	    		    			drawOverlay(xTB, yTB, capacity == -1 ? -1 : size, size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded);
	    		    		}
	    				}
	    			}
	    			else if(itemstack.getItem() instanceof FluidStorageBlockItem)
	    			{
	    				if(itemstack.hasTag() && itemstack.getTag().hasUniqueId(FluidStorageNetworkNode.NBT_ID))
	    				{
	    					UUID uuid = itemstack.getTag().getUniqueId(FluidStorageNetworkNode.NBT_ID);
	    					StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(uuid);
	    					
	    					if(data != null)
	    		    		{
	    		    			int capacity = data.getCapacity();
	    		    			float size = data.getStored()/(float)data.getCapacity();
	    		    			drawOverlay(xTB, yTB, capacity == -1 ? -1 : size, size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded);
	    		    		}
	    				}
	    			}
	    		}
	    		else if(itemstack.getItem() instanceof IItemProgressBarOverlay)
	    		{
	    			IItemProgressBarOverlay itemOverlay = (IItemProgressBarOverlay) itemstack.getItem();
	    			float size = itemOverlay.getOverlayBarValue(itemstack);
	    			
	    			if(size >= 0 || size == -1)
	    			{
			    		drawOverlay(xTB, yTB, size, itemOverlay.getOverlayBarColor(itemstack, size));
	    			}
	    		}
	    	}
		}
    }

	/**
	 * 
	 * @param x
	 * @param y
	 * @param size (-1 infinite show if size >= 0)
	 * @param color (0xff00eded)
	 */
	@SuppressWarnings("deprecation")
	private static void drawOverlay(int x, int y, float size, int color)
	{
		RenderSystem.disableDepthTest();
		GL11.glEnable(GL11.GL_BLEND);
		
		if(size >= 0)
		{
			minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
			GuiUtils.drawTexturedModalRect(x - 4, y - 24, 0, 0, 93, 20, 0.1f);
			GuiUtils.drawTexturedModalRect(x, y - 18, 0, 28, 60, 8, 0.1f);
			if(size > 0)
			{
				float red = (float)(color >> 16 & 255) / 255.0F;
	    	    float green = (float)(color >> 8 & 255) / 255.0F;
	    	    float blue = (float)(color & 255) / 255.0F;
				RenderSystem.color3f(red, green, blue);
				GuiUtils.drawTexturedModalRect(x, y - 18, 0, 20, (int)(60*size), 8, 0.1f);
			}
			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
			MatrixStack textStack = new MatrixStack();
            textStack.translate(0.0D, 0.0D, 300D);
            Matrix4f textLocation = textStack.getLast().getMatrix();
			minecraft.fontRenderer.renderString(((int) (size*100))+"%", x + 62, y - 17, color, false, textLocation, renderType, false, 0, 15728880);
			renderType.finish();
		}
		else if(size == -1)
		{
			minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
			GuiUtils.drawTexturedModalRect(x - 4, y - 24, 0, 0, 93, 20, 0.1f);
			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
			MatrixStack textStack = new MatrixStack();
            textStack.translate(0.0D, 0.0D, 300D);
            Matrix4f textLocation = textStack.getLast().getMatrix();
			minecraft.fontRenderer.renderString("Infinite", x + 25, y - 17, color, false, textLocation, renderType, false, 0, 15728880);
			renderType.finish();
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		RenderSystem.enableDepthTest();
	}
	
	@SuppressWarnings("deprecation")
	public static void drawRect(int left, int top, int right, int bottom, int color)
    {
        float startAlpha = (float)(color >> 24 & 255) / 255.0F;
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right,    top, 300).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left,    top, 300).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left, bottom, 300).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(right, bottom, 300).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
	
	public static List<ITextComponent> getTooltipFromItem(ItemStack itemStack)
	{
	      return itemStack.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
	}
	
	public static void initToolTipsPos(@Nonnull final ItemStack stack, List<? extends ITextProperties> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, FontRenderer font)
	{
		int tooltipTextWidth = 0;

        for (ITextProperties textLine : textLines)
        {
            int textLineWidth = font.getStringPropertyWidth(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }
        
        boolean needsWrap = false;
        int titleLinesCount = 1;
        
		tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth)
        {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }
        
        if (needsWrap)
        {
            int wrappedTooltipWidth = 0;
            List<ITextProperties> wrappedTextLines = new ArrayList<>();
            for (int i = 0; i < textLines.size(); i++)
            {
                ITextProperties textLine = textLines.get(i);
                List<ITextProperties> wrappedLine = font.getCharacterManager().func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY);
                if (i == 0)
                    titleLinesCount = wrappedLine.size();

                for (ITextProperties line : wrappedLine)
                {
                    int lineWidth = font.getStringPropertyWidth(line);
                    if (lineWidth > wrappedTooltipWidth)
                        wrappedTooltipWidth = lineWidth;
                    wrappedTextLines.add(line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (mouseX > screenWidth / 2)
                tooltipX = mouseX - 16 - tooltipTextWidth;
            else
                tooltipX = mouseX + 12;
        }
        
        tooltipY = mouseY - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1)
        {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount)
                tooltipHeight += 2;
        }

        if (tooltipY < 4)
            tooltipY = 4;
        else if (tooltipY + tooltipHeight + 4 > screenHeight)
            tooltipY = screenHeight - tooltipHeight - 4;
	}
}
