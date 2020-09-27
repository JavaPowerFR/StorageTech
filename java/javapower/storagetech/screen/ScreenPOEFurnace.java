package javapower.storagetech.screen;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;

import javapower.storagetech.container.ContainerPOEFurnace;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.tileentity.TileEntityPOEFurnace;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenPOEFurnace extends BaseScreen<ContainerPOEFurnace>
{
    private final String texture;
    private final Supplier<Integer>[] processing;
    
    private static final int BAR_X = 7;
    private static final int BAR_Y = 6;
    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 70;

    @SuppressWarnings("unchecked")
	public ScreenPOEFurnace(ContainerPOEFurnace container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 233, 188, inventory, title);
        this.texture = "guis/poe_furnace.png";
        processing = new Supplier[]
        		{
        				TileEntityPOEFurnace.FURNACE1_PROCESSING::getValue,
        				TileEntityPOEFurnace.FURNACE2_PROCESSING::getValue,
        				//TileEntityPOEFurnace.FURNACE3_PROCESSING::getValue,
        				//TileEntityPOEFurnace.FURNACE4_PROCESSING::getValue,
        		};
    }

    @Override
    public void onPostInit(int x, int y)
    {
    	addSideButton(new RedstoneModeSideButton(this, TileEntityPOEFurnace.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y)
    {
    	
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        bindTexture(StorageTech.MODID, texture);

        blit(matrixStack, x + 23, y, 0, 0, xSize - 23, ySize);
        blit(matrixStack, x, y, 211, 0, 27, 82);
        
        int barHeightNew = (int) ((float) TileEntityPOEFurnace.ENERGY_STORED.getValue() / (float) TileEntityPOEFurnace.ENERGY_CAPACITY.getValue() * (float) BAR_HEIGHT);

        blit(matrixStack, x + BAR_X, y + BAR_Y + BAR_HEIGHT - barHeightNew, 238, BAR_HEIGHT - barHeightNew, BAR_WIDTH, barHeightNew);
        
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderString(matrixStack, 30, 7, title.getString());
        renderString(matrixStack, 30, 95, I18n.format("container.inventory"));
        
        //renderString(matrixStack, 32, 78, processing[0].get()+" %");
        renderString(matrixStack, 74, 78, processing[0].get()+" %");
        renderString(matrixStack, 116, 78, processing[1].get()+" %");
        //renderString(matrixStack, 158, 78, processing[3].get()+" %");
        
        if (RenderUtils.inBounds(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY))
        {
            renderTooltip(matrixStack, mouseX, mouseY, I18n.format("misc.storagetech.storage.energy.stored",
            		API.instance().getQuantityFormatter().format(TileEntityPOEFurnace.ENERGY_STORED.getValue()), API.instance().getQuantityFormatter().format(TileEntityPOEFurnace.ENERGY_CAPACITY.getValue())));
        }
    }
}