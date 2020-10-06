package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ChemicalSearchBoxModeSideButton extends SideButton
{
	private final TileDataParameter<Integer, ?> parameter;

    public ChemicalSearchBoxModeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter)
    {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.refinedstorage.grid.search_box_mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.search_box_mode." + parameter.getValue());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
        int mode = parameter.getValue();
        screen.blit(matrixStack, x, y, mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ? 16 : 0, 96, 16, 16);
    }

    @Override
    public void onPress()
    {
    	int mode = parameter.getValue();
    	

        /*if (mode == IGrid.SEARCH_BOX_MODE_NORMAL)
        {
            mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED;
        }
        else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED)
        {
            if (JeiIntegration.isLoaded())
            {
                mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED;
            }
            else
            {
                mode = IGrid.SEARCH_BOX_MODE_NORMAL;
            }
        } else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED)
        {
            mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
        }
        else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)
        {
            mode = IGrid.SEARCH_BOX_MODE_NORMAL;
        }*/
        
        TileDataManager.setParameter(parameter, mode == 0 ? 1 : 0);
    }
}
