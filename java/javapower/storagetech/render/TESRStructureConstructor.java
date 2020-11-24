package javapower.storagetech.render;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.node.NetworkNodeStructureConstructor;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import javapower.storagetech.util.BlockLocalPos;
import javapower.storagetech.util.SCElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class TESRStructureConstructor extends TileEntityRenderer<TileEntityStructureConstructor>
{
	long tickRender = 0;
	public TESRStructureConstructor(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(TileEntityStructureConstructor tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		if(tileEntityIn != null && tileEntityIn.show_hologram.get())
		{
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
			++tickRender;
			BlockLocalPos bpl_strt = new BlockLocalPos(NetworkNodeStructureConstructor.MIN_IJ, NetworkNodeStructureConstructor.MIN_IJ, NetworkNodeStructureConstructor.MIN_K);
			BlockLocalPos bpl_end = new BlockLocalPos(NetworkNodeStructureConstructor.MAX_IJ, NetworkNodeStructureConstructor.MAX_IJ, NetworkNodeStructureConstructor.MAX_K);
			
			Direction dir = tileEntityIn.getBlockState().get(STBlocks.blockStructureConstructor.getDirection().getProperty());
			
			//Vector3i dirvec = dir.getDirectionVec();
			
			BlockPos blockpos = tileEntityIn.getPos();
			
			BlockPos bp_strt = bpl_strt.getBlockPos(blockpos, dir);
			BlockPos bp_end = bpl_end.getBlockPos(blockpos, dir);
			
			AxisAlignedBB shape = new AxisAlignedBB(
					bp_strt.getX() > bp_end.getX() ? bp_strt.getX() + 1 : bp_strt.getX(),
					bp_strt.getY() > bp_end.getY() ? bp_strt.getY() + 1 : bp_strt.getY(),
					bp_strt.getZ() > bp_end.getZ() ? bp_strt.getZ() + 1 : bp_strt.getZ(),
							
					bp_end.getX() > bp_strt.getX() ? bp_end.getX() + 1 : bp_end.getX(),
					bp_end.getY() > bp_strt.getY() ? bp_end.getY() + 1 : bp_end.getY(),
					bp_end.getZ() > bp_strt.getZ() ? bp_end.getZ() + 1 : bp_end.getZ()
									);
			
	        RenderSystem.enableBlend();
	        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	        RenderSystem.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().getMainWindow().getFramebufferWidth() / 1920.0F * 2.5F));
	        RenderSystem.disableTexture();
	        RenderSystem.pushMatrix();
	        
	        Color color = new Color(255, 255, 255);
	        RenderHelper.disableStandardItemLighting();
	        RenderSystem.enableDepthTest();
	        RenderSystem.depthFunc(515);
	        RenderSystem.depthMask(true);
	        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getLines());
	        
	        WorldRenderer.drawBoundingBox(matrixStackIn, builder, shape.offset((double) -blockpos.getX(), (double) -blockpos.getY(), (double) -blockpos.getZ()), (float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 0.5F);
	        
	        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
	        
	        matrix4f.mul(Vector3f.YN.rotationDegrees(dir.getHorizontalAngle()));
	        
	        if(dir == Direction.EAST)
	        	matrixStackIn.translate(-1, 0, 1);
	        else if(dir == Direction.NORTH)
	        	matrixStackIn.translate(-1, 0, 0);
	        else if(dir == Direction.SOUTH)
	        	matrixStackIn.translate(0, 0, 1);
	        
	        builder.pos(matrix4f, 0, 0, 0).color(0, 255, 0, 255).endVertex();
	        builder.pos(matrix4f, 0, 1, 0).color(0, 255, 0, 255).endVertex();
	        
	        builder.pos(matrix4f, 0, 0, 0).color(255, 0, 0, 255).endVertex();
	        builder.pos(matrix4f, 1, 0, 0).color(255, 0, 0, 255).endVertex();
	        
	        builder.pos(matrix4f, 0, 0, 0).color(0, 0, 255, 255).endVertex();
	        builder.pos(matrix4f, 0, 0, 1).color(0, 0, 255, 255).endVertex();
	        
	        RenderSystem.popMatrix();
	        RenderSystem.enableTexture();
	        RenderSystem.disableBlend();
	        
	       if(tileEntityIn.elements_client != null)
	       {
		        for(SCElement.Client element : tileEntityIn.elements_client)
		        {
		        	
		        	if(element.drop)
		        	{
		        		if(element.stack != null)
		        		{
		        			int i = element.localPos.getI();
				        	int j = element.localPos.getJ();
				        	int k = element.localPos.getK() -1;
				        	
			        		double mouv = Math.abs(Math.sin((tickRender % 720)*Math.PI/360))/4;
			    	        float rot = tickRender % 360f;
			    	        
			    	        matrixStackIn.translate(0.5f + i, 0.1f + mouv + j, 0.5f + k);
			    	        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rot));
			    	        
			    	        Minecraft.getInstance().getItemRenderer().renderItem(element.stack, ItemCameraTransforms.TransformType.GROUND, 0x0000f0, 0xffffff, matrixStackIn, bufferIn);
			    	       
			    	        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-rot));
			    	        matrixStackIn.translate(-0.5f  - i, (-0.1f - mouv) - j, -0.5f - k);
		        		}
		        	}
		        	else
		        	{
		        		if(element.stack != null)
		        		{
		        			Item itemIn = element.stack.getItem();
		        			
			        		if(itemIn != null && itemIn instanceof BlockItem)
			    			{
					        	int i = element.localPos.getI();
					        	int j = element.localPos.getJ();
					        	int k = element.localPos.getK();
					        	
						        matrixStackIn.translate(0.25d + i, 0.25d + j, 0.25d + (k-1));
						        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
						        blockrendererdispatcher.renderBlock(((BlockItem)itemIn).getBlock().getDefaultState(), matrixStackIn, bufferIn, 0x0000f0, 0xffffff);
						        
						        matrixStackIn.scale(2f, 2f, 2f);
						        matrixStackIn.translate(-0.25d - i, -0.25d - j, -0.25d - (k-1));
			    			}
		        		}
		        	}
		        }
	       }
	        
		}
	}
}
