package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GridViewChemicalTypeSideButton extends SideButton
{
	TileDataParameter<Integer, ?> parameter;
	
	public GridViewChemicalTypeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> _parameter)
	{
		super(screen);
		parameter = _parameter;
	}

	@Override
	public String getTooltip()
	{
		return I18n.format("sidebutton.storagetech.viewchemicaltype") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.storagetech.viewchemicaltype." + (parameter.getValue()+1));
	}

	@Override
	protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
	{
		screen.bindTexture(StorageTech.MODID, "icons.png");
        screen.blit(matrixStack, x, y, (parameter.getValue()+1) * 16, 64, 16, 16);
	}
	
	@Override
    public void onPress()
    {
    	int v = parameter.getValue();
    	if(v == 2)
    		v = -1;
    	else
    		++v;
        TileDataManager.setParameter(parameter, v);
    }

}
