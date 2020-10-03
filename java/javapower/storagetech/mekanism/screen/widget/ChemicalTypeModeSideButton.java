package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ChemicalTypeModeSideButton extends SideButton
{
    private final TileDataParameter<Integer, ?> parameter;

    public ChemicalTypeModeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter)
    {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.storagetech.chemicaltype") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.storagetech.chemicaltype." + parameter.getValue());
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
    		v = 0;
    	else
    		++v;
        TileDataManager.setParameter(parameter, v);
    }
}