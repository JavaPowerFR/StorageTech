package javapower.storagetech.core;

import java.util.UUID;

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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientDiskOverlay
{
	public static Minecraft minecraft = Minecraft.getInstance();
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
    public static void drawTooltip(RenderTooltipEvent.PostBackground event)
    {
		if(ClientConfig.Value_overlayEnable)
		{
	    	ItemStack itemstack = event.getStack();
	    	if(itemstack != null)
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
		    			if(data.getCapacity() != -1)
		    			{
			    			float size = data.getStored()/(float)data.getCapacity();
			    			int color = size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded;
			    			minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
			    			GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
			    			drawRect(event.getX(), event.getY() - 18, event.getX() + 60, event.getY() - 10, 0xff444444);
			    			if(size > 0)
			    				drawRect(event.getX(), event.getY() - 18, event.getX() + (int)(60*size), event.getY() - 10, color);
			    			
			    			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
			    			MatrixStack textStack = new MatrixStack();
			                textStack.translate(0.0D, 0.0D, 300D);
			                Matrix4f textLocation = textStack.getLast().getMatrix();
			    			minecraft.fontRenderer.renderString(((int) (size*100))+"%", event.getX() + 62, event.getY() - 17, color, false, textLocation, renderType, false, 0, 15728880);
			    			renderType.finish();
		    			}
		    			else
		    			{
		    				minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
		    				GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
			    			//minecraft.fontRenderer.drawString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded);
			    			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
			    			MatrixStack textStack = new MatrixStack();
			                textStack.translate(0.0D, 0.0D, 300D);
			                Matrix4f textLocation = textStack.getLast().getMatrix();
			    			minecraft.fontRenderer.renderString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded, false, textLocation, renderType, false, 0, 15728880);
			    			renderType.finish();
		    			}
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
	    		    			if(data.getCapacity() != -1)
	    		    			{
	    			    			float size = data.getStored()/(float)data.getCapacity();
	    			    			int color = size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded;
	    			    			minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
	    			    			GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
	    			    			drawRect(event.getX(), event.getY() - 18, event.getX() + 60, event.getY() - 10, 0xff444444);
	    			    			if(size > 0)
	    			    				drawRect(event.getX(), event.getY() - 18, event.getX() + (int)(60*size), event.getY() - 10, color);
	    			    			
	    			    			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
	    			    			MatrixStack textStack = new MatrixStack();
	    			                textStack.translate(0.0D, 0.0D, 300D);
	    			                Matrix4f textLocation = textStack.getLast().getMatrix();
	    			    			minecraft.fontRenderer.renderString(((int) (size*100))+"%", event.getX() + 62, event.getY() - 17, color, false, textLocation, renderType, false, 0, 15728880);
	    			    			renderType.finish();
	    		    			}
	    		    			else
	    		    			{
	    		    				minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
	    		    				GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
	    			    			//minecraft.fontRenderer.drawString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded);
	    			    			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
	    			    			MatrixStack textStack = new MatrixStack();
	    			                textStack.translate(0.0D, 0.0D, 300D);
	    			                Matrix4f textLocation = textStack.getLast().getMatrix();
	    			    			minecraft.fontRenderer.renderString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded, false, textLocation, renderType, false, 0, 15728880);
	    			    			renderType.finish();
	    		    			}
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
	    		    			if(data.getCapacity() != -1)
	    		    			{
	    			    			float size = data.getStored()/(float)data.getCapacity();
	    			    			int color = size >= 0.75f ? size >= 1 ? 0xffff0000 : 0xffffd800 : 0xff00eded;
	    			    			minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
	    			    			GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
	    			    			drawRect(event.getX(), event.getY() - 18, event.getX() + 60, event.getY() - 10, 0xff444444);
	    			    			if(size > 0)
	    			    				drawRect(event.getX(), event.getY() - 18, event.getX() + (int)(60*size), event.getY() - 10, color);
	    			    			
	    			    			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
	    			    			MatrixStack textStack = new MatrixStack();
	    			                textStack.translate(0.0D, 0.0D, 300D);
	    			                Matrix4f textLocation = textStack.getLast().getMatrix();
	    			    			minecraft.fontRenderer.renderString(((int) (size*100))+"%", event.getX() + 62, event.getY() - 17, color, false, textLocation, renderType, false, 0, 15728880);
	    			    			renderType.finish();
	    		    			}
	    		    			else
	    		    			{
	    		    				minecraft.textureManager.bindTexture(ResourceLocationRegister.overlay);
	    		    				GuiUtils.drawTexturedModalRect(event.getX() - 4, event.getY() - 24, 0, 0, 93, 20, 0.1f);
	    			    			//minecraft.fontRenderer.drawString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded);
	    			    			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
	    			    			MatrixStack textStack = new MatrixStack();
	    			                textStack.translate(0.0D, 0.0D, 300D);
	    			                Matrix4f textLocation = textStack.getLast().getMatrix();
	    			    			minecraft.fontRenderer.renderString("Infinite", event.getX() + 25, event.getY() - 17, 0xff00eded, false, textLocation, renderType, false, 0, 15728880);
	    			    			renderType.finish();
	    		    			}
	    		    		}
	    				}
	    			}
	    		}
	    	}
		}
    }
	
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
}
