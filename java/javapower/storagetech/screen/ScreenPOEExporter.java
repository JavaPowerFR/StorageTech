package javapower.storagetech.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;

import javapower.storagetech.container.ContainerPOEExporter;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.screen.widget.EnergyRestrictionModeSideButton;
import javapower.storagetech.tileentity.TileEntityPOEExporter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenPOEExporter extends BaseScreen<ContainerPOEExporter>
{
    private final String texture;

    public ScreenPOEExporter(ContainerPOEExporter container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 176, 190, inventory, title);
        this.texture = "guis/poe_importer.png";
    }

    @Override
    public void onPostInit(int x, int y)
    {
    	addSideButton(new RedstoneModeSideButton(this, TileEntityPOEExporter.REDSTONE_MODE));
    	addSideButton(new EnergyRestrictionModeSideButton(this, TileEntityPOEExporter.ENERGY_RESTRICTION));
    	
    	int buttonWidth = 10 + font.getStringWidth(I18n.format("misc.storagetech.energylimiter"));
        addButton(x + 169 - buttonWidth, y + 65, buttonWidth, 20, new TranslationTextComponent("misc.storagetech.energylimiter"), true, true, btn -> minecraft.displayGuiScreen(new ScreenEnergyLimiter(this, TileEntityPOEExporter.ENERGY_RESTRICTION_VALUE, playerInventory)));
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
        renderString(matrixStack, 7, 95, I18n.format("container.inventory"));
        
        boolean islimited = TileEntityPOEExporter.ENERGY_RESTRICTION.getValue();
        renderString(matrixStack, 7, 70, I18n.format("sidebutton.storagetech.energy_restriction."+islimited) + (islimited ? ": "+API.instance().getQuantityFormatter().formatWithUnits(TileEntityPOEExporter.ENERGY_RESTRICTION_VALUE.getValue())+" FE/t" : ""));
    }
}