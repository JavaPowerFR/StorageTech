package javapower.storagetech.screen;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.RenderUtils;

import javapower.storagetech.core.StorageTech;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScreenNodeEnergyStorage <T extends Container> extends BaseScreen<T>
{
    private static final int BAR_X = 8;
    private static final int BAR_Y = 29;
    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 70;

    private final String texture;
    private final Supplier<Long> storedSupplier;
    private final Supplier<Long> capacitySupplier;
    private final Supplier<Long> ioCapacitySupplier;
	private TileDataParameter<Integer, ?> redstonemode;

    public ScreenNodeEnergyStorage(T container, PlayerInventory inventory, ITextComponent title, String _texture, TileDataParameter<Integer, ?> _redstonemode, Supplier<Long> _storedSupplier, Supplier<Long> _capacitySupplier, Supplier<Long> _ioCapacitySupplier)
    {
        super(container, 176, 207, inventory, title);

        texture = _texture;
        redstonemode = _redstonemode;
        storedSupplier = _storedSupplier;
        capacitySupplier = _capacitySupplier;
        ioCapacitySupplier = _ioCapacitySupplier;
    }

    @Override
    public void onPostInit(int x, int y)
    {
    	addSideButton(new RedstoneModeSideButton(this, redstonemode));
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

        int barHeightNew = (int) ((float) storedSupplier.get() / (float) capacitySupplier.get() * (float) BAR_HEIGHT);

        blit(matrixStack, x + BAR_X, y + BAR_Y + BAR_HEIGHT - barHeightNew, 179, BAR_HEIGHT - barHeightNew, BAR_WIDTH, barHeightNew);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 18, capacitySupplier.get() == -1 ?
            I18n.format("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get())) :
            I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get()), API.instance().getQuantityFormatter().formatWithUnits(capacitySupplier.get()))
            + " | "+I18n.format("misc.storagetech.storage.energy.io", API.instance().getQuantityFormatter().formatWithUnits(ioCapacitySupplier.get()))
        );
        
        renderString(matrixStack, 7, 112, I18n.format("container.inventory"));
        if (RenderUtils.inBounds(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY))
        {
            int full = 0;

            if (capacitySupplier.get() >= 0)
                full = (int) ((float) storedSupplier.get() / (float) capacitySupplier.get() * 100f);

            renderTooltip(matrixStack, mouseX, mouseY, (capacitySupplier.get() == -1 ?
                I18n.format("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().format(storedSupplier.get())) :
                I18n.format("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().format(storedSupplier.get()), API.instance().getQuantityFormatter().format(capacitySupplier.get()))
            ) + "\n" + TextFormatting.GRAY + I18n.format("misc.refinedstorage.storage.full", full));
        }
    }
}