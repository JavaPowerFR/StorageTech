package javapower.storagetech.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.Variable;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class HologramSideButton extends SideButton
{
    private Variable<Boolean> state;

    public HologramSideButton(BaseScreen<?> screen, Variable<Boolean> _state)
    {
        super(screen);
        state = _state;
    }

    @Override
    public String getTooltip()
    {
        return I18n.format("sidebutton.storagetech.hologram") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.storagetech.hologram." + state.get());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y)
    {
    	screen.bindTexture(StorageTech.MODID, "icons.png");
        screen.blit(matrixStack, x, y, state.get() ? 16 : 0, 16, 16, 16);
    }

    @Override
    public void onPress()
    {
    	state.set(!state.get());
    }
}