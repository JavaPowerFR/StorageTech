package javapower.storagetech.mekanism.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.container.ContainerChemicalDetector;
import javapower.storagetech.mekanism.screen.widget.ChemicalDetectorModeSideButton;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalDetector;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ScreenChemicalDetector extends BaseScreen2<ContainerChemicalDetector>
{
    private final String texture;
    private TextFieldWidget amountField;

    public ScreenChemicalDetector(ContainerChemicalDetector container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 176, 137, inventory, title);
        this.texture = "guis/chemical_detector.png";
    }

    @Override
    public void onPostInit(int x, int y)
    {
    	addSideButton(new ChemicalDetectorModeSideButton(this));
        addSideButton(new ExactModeSideButton(this, TileEntityChemicalDetector.COMPARE));
        
        amountField = new TextFieldWidget(font, x + 41 + 1, y + 23 + 1, 50, font.FONT_HEIGHT, new StringTextComponent(""));
        amountField.setText(String.valueOf(TileEntityChemicalDetector.AMOUNT.getValue()));
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setCanLoseFocus(true);
        amountField.setFocused2(false);
        amountField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        amountField.setResponder(value ->
        {
            try
            {
                long result = Long.parseLong(value);

                TileDataManager.setParameter(TileEntityChemicalDetector.AMOUNT, result);
            }
            catch (NumberFormatException e)
            {
                // NO OP
            }
        });

        addButton(amountField);
    }
    
    public void updateAmountField(long amount)
    {
        amountField.setText(String.valueOf(amount));
    }

    @Override
    public void tick(int x, int y)
    {
    	
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(StorageTech.MODID, texture);

        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }
    
	@Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"));
        
        super.renderForeground(matrixStack, mouseX, mouseY);
    }
	
	@Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
	{
        if (key == GLFW.GLFW_KEY_ESCAPE)
        {
            minecraft.player.closeScreen();

            return true;
        }

        if (amountField.keyPressed(key, scanCode, modifiers) || amountField.canWrite())
        {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }
}