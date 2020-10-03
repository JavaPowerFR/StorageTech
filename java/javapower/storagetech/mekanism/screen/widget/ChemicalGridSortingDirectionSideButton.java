package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ChemicalGridSortingDirectionSideButton extends SideButton
{
	private final TileDataParameter<Integer, ?> parameter;
	
    public ChemicalGridSortingDirectionSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter)
    {
        super(screen);
        
        this.parameter = parameter;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.refinedstorage.grid.sorting.direction") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.sorting.direction." + parameter.getValue());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
        screen.blit(matrixStack, x, y, parameter.getValue() * 16, 16, 16, 16);
    }

    @Override
    public void onPress()
    {
        int dir = parameter.getValue();

        if (dir == IGrid.SORTING_DIRECTION_ASCENDING)
        {
            dir = IGrid.SORTING_DIRECTION_DESCENDING;
        }
        else if (dir == IGrid.SORTING_DIRECTION_DESCENDING)
        {
            dir = IGrid.SORTING_DIRECTION_ASCENDING;
        }
        TileDataManager.setParameter(parameter, dir);
    }
}