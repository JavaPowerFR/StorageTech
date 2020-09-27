package javapower.storagetech.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.eventio.IEventVoid;
import net.minecraft.client.resources.I18n;

public class ZeroSideButton extends SideButton
{
    private IEventVoid onPress;

    public ZeroSideButton(BaseScreen<?> screen, IEventVoid _onPress)
    {
        super(screen);
        onPress = _onPress;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.storagetech.zero");
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
    	screen.bindTexture(StorageTech.MODID, "icons.png");
        screen.blit(matrixStack, x, y, 32, 48, 16, 16);
    }

    @Override
    public void onPress()
    {
    	onPress.event();
    }
}