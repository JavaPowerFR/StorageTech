package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ChemicalGridSortingTypeSideButton extends SideButton
{
	private final TileDataParameter<Integer, ?> parameter;
	
    public ChemicalGridSortingTypeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter)
    {
        super(screen);
        
        this.parameter = parameter;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.storagetech.sortingtype") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.storagetech.sortingtype." + parameter.getValue());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
        screen.blit(matrixStack, x, y, parameter.getValue() * 16, 32, 16, 16);
    }

    @Override
    public void onPress()
    {
    	int v = parameter.getValue();
    	if(v == 2)
    		v = 0;
    	else
    		++v;
        TileDataManager.setParameter(parameter, v);
    }
}