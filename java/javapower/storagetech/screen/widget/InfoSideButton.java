package javapower.storagetech.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.setup.ClientSetup;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class InfoSideButton extends SideButton
{
    public InfoSideButton(BaseScreen<?> screen)
    {
        super(screen);
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.storagetech.info") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.storagetech.info." + ClientSetup.ShowInfo);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
    	screen.bindTexture(StorageTech.MODID, "icons.png");
        screen.blit(matrixStack, x, y, 32, 0, 16, 16);
    }

    @Override
    public void onPress()
    {
    	ClientSetup.ShowInfo = !ClientSetup.ShowInfo;
    }
}