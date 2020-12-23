package javapower.storagetech.mekanism.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.DetectorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.mekanism.container.ContainerChemicalDetector;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalDetector;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ChemicalDetectorModeSideButton extends SideButton
{

	public ChemicalDetectorModeSideButton(BaseScreen<ContainerChemicalDetector> screen)
	{
		super(screen);
	}

	@Override
	public String getTooltip()
	{
		return I18n.format("sidebutton.refinedstorage.detector.mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.detector.mode." + TileEntityChemicalDetector.MODE.getValue());
	}

	@Override
	protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
	{
		screen.blit(matrixStack, x, y, DetectorTile.MODE.getValue() * 16, 176, 16, 16);
	}
	
	@Override
    public void onPress()
	{
        int mode = TileEntityChemicalDetector.MODE.getValue();

        if (mode == DetectorNetworkNode.MODE_EQUAL)
        {
            mode = DetectorNetworkNode.MODE_ABOVE;
        }
        else if (mode == DetectorNetworkNode.MODE_ABOVE)
        {
            mode = DetectorNetworkNode.MODE_UNDER;
        }
        else if (mode == DetectorNetworkNode.MODE_UNDER)
        {
            mode = DetectorNetworkNode.MODE_EQUAL;
        }

        TileDataManager.setParameter(TileEntityChemicalDetector.MODE, mode);
    }

}
