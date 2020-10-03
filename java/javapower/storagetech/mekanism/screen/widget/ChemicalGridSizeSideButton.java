package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ChemicalGridSizeSideButton extends SideButton
{
private final TileDataParameter<Integer, ?> parameter;
	
    public ChemicalGridSizeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter)
    {
        super(screen);
        
        this.parameter = parameter;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.refinedstorage.grid.size") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.size." + parameter.getValue());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
    	int size = parameter.getValue();
    	int tx = 0;

        if (size == IGrid.SIZE_STRETCH)
        {
            tx = 48;
        }
        else if (size == IGrid.SIZE_SMALL)
        {
            tx = 0;
        }
        else if (size == IGrid.SIZE_MEDIUM)
        {
            tx = 16;
        }
        else if (size == IGrid.SIZE_LARGE)
        {
            tx = 32;
        }

        screen.blit(matrixStack, x, y, 64 + tx, 64, 16, 16);
    }

    @Override
    public void onPress()
    {
        int size = parameter.getValue();
        
        if (size == IGrid.SIZE_STRETCH)
        {
            size = IGrid.SIZE_SMALL;
        }
        else if (size == IGrid.SIZE_SMALL)
        {
            size = IGrid.SIZE_MEDIUM;
        }
        else if (size == IGrid.SIZE_MEDIUM)
        {
            size = IGrid.SIZE_LARGE;
        }
        else if (size == IGrid.SIZE_LARGE)
        {
            size = IGrid.SIZE_STRETCH;
        }
        TileDataManager.setParameter(parameter, size);
    }
}
