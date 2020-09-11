package javapower.storagetech.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;

import javapower.storagetech.container.ContainerPOEExporter;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPOEExporter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

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
        /*renderString(matrixStack, 7, 18, capacitySupplier.get() == -1 ?
            I18n.format("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get())) :
            I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get()), API.instance().getQuantityFormatter().formatWithUnits(capacitySupplier.get()))
        );*/
    }
}