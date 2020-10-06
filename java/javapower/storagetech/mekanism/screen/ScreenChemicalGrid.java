package javapower.storagetech.mekanism.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;

import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.mekanism.container.ContainerChemicalGrid;
import javapower.storagetech.mekanism.container.VirtualSlot;
import javapower.storagetech.mekanism.grid.ClientChemicalGrid;
import javapower.storagetech.mekanism.screen.widget.ChemicalGridSizeSideButton;
import javapower.storagetech.mekanism.screen.widget.ChemicalGridSortingDirectionSideButton;
import javapower.storagetech.mekanism.screen.widget.ChemicalGridSortingTypeSideButton;
import javapower.storagetech.mekanism.screen.widget.ChemicalSearchBoxModeSideButton;
import javapower.storagetech.mekanism.screen.widget.GridViewChemicalTypeSideButton;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalGrid;
import javapower.storagetech.packet.IGenericMessage;
import javapower.storagetech.screen.widget.InfoSideButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenChemicalGrid extends BaseScreen<ContainerChemicalGrid> implements IGenericMessage
{
	public static final ResourceLocation GUI_SPRITE = ResourceLocationRegister.resource("textures/guis/grid.png");
	
	private ClientChemicalGrid client = new ClientChemicalGrid(this);

    public ScreenChemicalGrid(ContainerChemicalGrid container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 227, 0, inventory, title);
    }
    @Override
    protected void onPreInit()
    {
    	super.onPreInit();
    	
    	this.ySize = 118 + (client.getVisibleRows() * 18);
    	client.guiInit(getYPlayerInventory());
    }

	@Override
	public void onPostInit(int x, int y)
	{
		container.initSlot(this);
		addSideButton(new RedstoneModeSideButton(this, TileEntityChemicalGrid.REDSTONE_MODE));
		addSideButton(new GridViewChemicalTypeSideButton(this, TileEntityChemicalGrid.VIEW_CHEMICAL_TYPE));
		addSideButton(new ChemicalGridSortingDirectionSideButton(this, TileEntityChemicalGrid.SORTING_DIRECTION));
		addSideButton(new ChemicalGridSortingTypeSideButton(this, TileEntityChemicalGrid.SORTING_TYPE));
		addSideButton(new ChemicalSearchBoxModeSideButton(this, TileEntityChemicalGrid.SEARCH_BOX_MODE));
		addSideButton(new ChemicalGridSizeSideButton(this, TileEntityChemicalGrid.SIZE));
		addSideButton(new InfoSideButton(this));
		
	}

	@Override
	public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
	{
		minecraft.getTextureManager().bindTexture(GUI_SPRITE);
		
		int yy = y;
		
		blit(matrixStack, x, yy, 0, 0, xSize - 34, 19);
		
        blit(matrixStack, x + xSize - 34 + 4, y, 197, 0, 30, 82);

        int rows = client.getVisibleRows();

        for (int i = 0; i < rows; ++i)
        {
            yy += 18;

            blit(matrixStack, x, yy, 0, 19 + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), xSize - 34, 18);
        }
        
        yy += 18;

        blit(matrixStack, x, yy, 0, 73, xSize - 34, 99);
        
        client.renderBackground(matrixStack, x, y, mouseX, mouseY);
	}

	@Override
	public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		renderString(matrixStack, 7, 7, title.getString());
		renderString(matrixStack, 7, client.getVisibleRows()*18 + 22, I18n.format("container.inventory"));
		
		client.renderForeground(matrixStack, mouseX, mouseY);
	}
	
	@Override
	public boolean mouseDragged(double x2, double y2, int b, double dx, double dy)
	{
		client.mouseDragged(x2, y2, b, dx, dy);
		
		return super.mouseDragged(x2, y2, b, dx, dy);
	}
	
	@Override
	public boolean mouseClicked(double xm, double ym, int button)
	{
		client.mouseClicked(xm, ym, button);
		
		return super.mouseClicked(xm, ym, button);
	}
	
	@Override
	public boolean mouseReleased(double x2, double y2, int button)
	{
		client.mouseReleased(x2, y2, button);
		
		return super.mouseReleased(x2, y2, button);
	}
	
	@Override
	public boolean mouseScrolled(double xm, double ym, double value)
	{
		client.mouseScrolled(xm, ym, value);
		
		return super.mouseScrolled(xm, ym, value);
	}

	@Override
	public void tick(int x, int y)
	{
		client.tick();
	}
	
	@Override
	public boolean charTyped(char c, int id)
	{
		client.charTyped(c, id);
		return super.charTyped(c, id);
	}
	
	@Override
	public boolean keyPressed(int a, int b, int c)
	{
		client.keyPressed(a, b, c);
		return super.keyPressed(a, b, c);
	}
	
	public ClientChemicalGrid getGrid()
	{
		return client;
	}
	
	public int getYPlayerInventory()
	{
        return 35 + (client.getVisibleRows() * 18);
	}
	
	public ClientChemicalGrid getClient()
	{
		return client;
	}
	
	public void slotClick(int id, int dragType, ClickType clickType, PlayerEntity player, Slot slotTarget)
	{
		if(slotTarget instanceof VirtualSlot)
			client.slotClick(id, dragType, clickType, player, ((VirtualSlot)slotTarget), player.inventory.getItemStack());
	}
	
	@Override
	public void recivePacket(CompoundNBT nbt)
	{
		if(nbt.contains("updateFilters"))
		{
			client.updateSlotFilter();
		}
		else if(nbt.contains("heldItem"))
		{
			container.getPlayer().inventory.setItemStack(ItemStack.read(nbt.getCompound("heldItem")));
		}
	}
}