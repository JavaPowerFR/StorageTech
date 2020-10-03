package javapower.storagetech.mekanism.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.container.ContainerChemicalExporter;
import javapower.storagetech.mekanism.screen.widget.ChemicalTypeModeSideButton;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalExporter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenChemicalExporter extends BaseScreen2<ContainerChemicalExporter>
{
    private final String texture;

    public ScreenChemicalExporter(ContainerChemicalExporter container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 210, 137, inventory, title);
        this.texture = "guis/chemical_importer.png";
    }

    @Override
    public void onPostInit(int x, int y)
    {
    	addSideButton(new RedstoneModeSideButton(this, TileEntityChemicalExporter.REDSTONE_MODE));
    	addSideButton(new ChemicalTypeModeSideButton(this, TileEntityChemicalExporter.CHEMICAL_TYPE));
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
    
	@Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"));
        
        super.renderForeground(matrixStack, mouseX, mouseY);
    }
}