package javapower.storagetech.mekanism.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;

import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.container.ContainerGasImporter;
import javapower.storagetech.mekanism.inventory.GasFilterSlot;
import javapower.storagetech.mekanism.tileentity.TileEntityGasImporter;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

public class ScreenGasImporter extends BaseScreen<ContainerGasImporter>
{
    private final String texture;

    public ScreenGasImporter(ContainerGasImporter container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 210, 137, inventory, title);
        this.texture = "guis/gas_importer.png";
    }

    @Override
    public void onPostInit(int x, int y)
    {
    	addSideButton(new RedstoneModeSideButton(this, TileEntityGasImporter.REDSTONE_MODE));
    	addSideButton(new WhitelistBlacklistSideButton(this, TileEntityGasImporter.WHITELIST_BLACKLIST));
    }

    @Override
    public void tick(int x, int y)
    {
    	
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(StorageTech.MODID, texture);

        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"));
        for(Slot slot : container.inventorySlots)
        {
        	if(slot != null && slot instanceof GasFilterSlot)
        	{
        		GasFilterSlot gfs = (GasFilterSlot) slot;
        		GasStack stack = gfs.getGasInSlot();
        		if(stack != null && !stack.isEmpty())
        		{
	        		Gas gas = stack.getType();
	        		int tint = gas.getChemical().getTint();
	        		
	        		float alpha = (float)(tint >> 24 & 255) / 255.0F;
	        	    float red = (float)(tint >> 16 & 255) / 255.0F;
	        	    float green = (float)(tint >> 8 & 255) / 255.0F;
	        	    float blue = (float)(tint & 255) / 255.0F;
	        	    
	        		minecraft.getTextureManager().bindTexture(ResourceLocationRegister.mekanism_gas_texture);
	        		RenderSystem.color4f(red, green, blue, alpha);
	        		blit(matrixStack, slot.xPos, slot.yPos, 0, ((System.currentTimeMillis()/100)%32)*16, 16, 16, 16, 512);
        		}
        	}
        }
        
        if(hoveredSlot != null && hoveredSlot instanceof GasFilterSlot)
        {
        	GasFilterSlot gfs = (GasFilterSlot) hoveredSlot;
    		GasStack stack = gfs.getGasInSlot();
    		if(stack != null && !stack.isEmpty())
    		{
    			renderTooltip(matrixStack, stack.getTextComponent(), mouseX, mouseY);
    		}
        }
    }
}