package javapower.storagetech.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javapower.storagetech.container.ContainerDiskWorkbench;
import javapower.storagetech.core.ConfigClient;
import javapower.storagetech.proxy.ResourceLocationRegister;
import javapower.storagetech.tileentity.TileEntityDiskWorkbench;
import javapower.storagetech.util.IGUITileSync;
import javapower.storagetech.util.NetworkUtils;
import javapower.storagetech.util.Tools;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiContainerDiskWorkbench extends GuiContainer implements IGUITileSync
{
	boolean showHelp, update;
	long memory = 0l;
	boolean init = false;
	int disk_size = 1000;
	int disk_maxsize = 0;
	
	boolean enable_costdisk = true;
	
	float prossesTime = 0.0f;
	int energy = 0;
	int capacity = 0;
	boolean prosses = false;
	int animaite = 0;
	
	GuiTextField textField_size;
	GuiButton button_create = new GuiButton(1, 28, 17, 40, 20, I18n.format("storagetech.gui.create"));
	byte slot = -1;
	TileEntityDiskWorkbench tileentity;
	
	private String[] i18nBuffer;
	//private byte update_mouse = 0;
	
	public GuiContainerDiskWorkbench(TileEntityDiskWorkbench tile, EntityPlayer player)
	{
		super(new ContainerDiskWorkbench(tile, player));
		tileentity = tile;
		
		xSize = 176;
		ySize = 189;
		NetworkUtils.sendToServerPlayerAsOpenGUI(tile, this);
		
		//NBTTagCompound nbt = new NBTTagCompound();
		//nbt.setByte("up", (byte) 1);
		//sendInfo(nbt);
		
		i18nBuffer = new String[]
				{
					I18n.format("storagetech.gui.creation"),
					I18n.format("storagetech.gui.availablespace"),
					I18n.format("storagetech.gui.disksizein"),
					I18n.format("storagetech.gui.info"),
					I18n.format("storagetech.gui.insertstoragepart"),
					I18n.format("storagetech.gui.insertstoragehousing"),
					I18n.format("storagetech.gui.for")
				};
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		showHelp = ConfigClient.getShowHelp();
		
		if(!init)
		{
			init = true;
			textField_size = new GuiTextField(0, fontRenderer, 28, 17, 70, 20);
			textField_size.setMaxStringLength(10);
			textField_size.setText(""+disk_size);
		}
		
		buttonList.clear();
		buttonList.add(button_create);
		//Mouse.setGrabbed(false);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(ResourceLocationRegister.gui_diskwb);
		
		int x = (width - xSize) /2;
        int y = (height - ySize) /2;
        
        textField_size.x = x + 28;
        textField_size.y = y + 57;
        
		button_create.x = x + 100;
		button_create.y = y + 57;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		this.drawTexturedModalRect(x-24, y+10, 210, 0, 27, 82);
        
        if(!update)
			return;
        
        if(enable_costdisk)
        {
	        this.drawTexturedModalRect(x+173, y, 176, 0, 22, 98);
	        
	        if(energy > 0)
	        {
	        	int yw = (int)(59*(energy/(float)capacity));
	        	//int yw = 59;
	        	this.drawTexturedModalRect(x+176, y+20+(59-yw), 198, 59-yw, 12, yw);
	        }
	        
	        if(prosses && prossesTime > 0.0f)
	        	this.drawTexturedModalRect(x+148, y+36, 176, 98, 15, Math.min((int)(22*prossesTime),22));
        }
        
        if(prosses && prossesTime > 0.0f)
        {
        	drawTexturedModalRect(x+26, y+26, 26, 19, 116, 5);
        	drawTexturedModalRect(x+27, y+16, 0 + animaite, 189, 114, 5);
        	drawTexturedModalRect(x+27, y+48, 0 + animaite, 189, 114, 5);
        	
        	 this.drawCenteredString(fontRenderer, i18nBuffer[0]+" "+(((int)(prossesTime*10000))/100f)+" %", x + 84, y + 30, 0xffffff);
	        if(animaite < 0)
	        	animaite = 10;
	        else
	        	--animaite;
        }
        else
        {
        
	        this.drawString(fontRenderer, i18nBuffer[1]+": ", x + 28, y + 6, 0xffffff);
	        this.drawString(fontRenderer, Tools.longFormatToString(memory)+" VIB", x + 28, y + 17, 0xffffff);
	        this.drawString(fontRenderer, i18nBuffer[2]+" VIB:", x + 28, y + 47, 0xffffff);
        
        }
        
        textField_size.drawTextBox();
        
        this.drawString(fontRenderer, "VIB: Virtual Item Box", x + 34, y + 87, 0xffffff);
        this.drawString(fontRenderer, "1 VIB = "+i18nBuffer[6]+" 1 item", x + 40, y + 97, 0xffffff);
        
        //if(slot == 0)this.drawString(fontRenderer, "insert storage part", x + 5, y + 4, 0xffffff);
        //else if(slot == 1)this.drawString(fontRenderer, "insert storage housing", x + 5, y + 4, 0xffffff);
        
        if(enable_costdisk)
        {
	        if(prosses && mouseX >= x+148 && mouseY >= y+36 && mouseX <= x+163 && mouseY <= y+58)
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add((((int)(prossesTime*10000))/100f)+" %");
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, mc.displayWidth, mc.displayHeight, 100, fontRenderer);
	        }
	        if(mouseX >= x+176 && mouseY >= y+20 && mouseX <= x+187 && mouseY <= y+79)
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add(energy+" RF /"+capacity+" RF");
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, mc.displayWidth, mc.displayHeight, 200, fontRenderer);
	        }
        }
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		
		if(showHelp)
		{
			if(slot == 0)
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add("§b(i) §f"+i18nBuffer[3]);
	        	list.add("§7"+i18nBuffer[4]);
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, mc.displayWidth, mc.displayHeight, 150, fontRenderer);
	        }
			else if(slot == 1)
			{
				List<String> list = new ArrayList<String>();
	        	list.add("§b(i) §f"+i18nBuffer[3]);
	        	list.add("§7"+i18nBuffer[5]);
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, mc.displayWidth, mc.displayHeight, 150, fontRenderer);
			}
		}
	}
	
	@Override
	public void updateScreen()
	{
		/*if(update_mouse < 4)
		{
			++update_mouse;
			Mouse.setGrabbed(false);
		}*/
		
		if(!update)
			return;
		super.updateScreen();
		textField_size.updateCursorCounter();
		Slot s = getSlotUnderMouse();
		if(s != null && s.getStack().isEmpty())
		{
			if(s.slotNumber == 36)//storage part
			{
				slot = 0;
			}
			else if(s.slotNumber == 37)//storage Housing
			{
				slot = 1;
			}
			else
			{
				slot = -1;
			}
		}
		else
			slot = -1;
		
		if(enable_costdisk)
			if(prosses)
			{
				if(button_create.enabled)
					button_create.enabled = false;
			}
			else
			{
				if(!button_create.enabled)
					button_create.enabled = true;
			}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		textField_size.mouseClicked(mouseX, mouseY, mouseButton);
		updateTFInteger();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		
		if("0123456789".indexOf(typedChar) != -1 || keyCode == 14 || keyCode == 203 || keyCode == 205)
		{
			textField_size.textboxKeyTyped(typedChar, keyCode);
			updateTFInteger();
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(button.id == 1)
		{
			if(disk_size < 1)
			{
				disk_size = 1;
				textField_size.setText(""+disk_size);
			}
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("startCreateDisk", disk_size);
			sendInfo(nbt);
		}
	}

	@Override
	public Class<? extends TileEntity> tileEntityLink()
	{
		return TileEntityDiskWorkbench.class;
	}

	@Override
	public void reciveDataFromServer(NBTTagCompound nbt)
	{
		update = true;
		if(nbt.hasKey("memory"))
			memory = nbt.getLong("memory");
		
		if(nbt.hasKey("max"))
			disk_maxsize = nbt.getInteger("max");
		
		if(nbt.hasKey("prosses"))
			prosses = nbt.getBoolean("prosses");
		
		if(nbt.hasKey("prossestime"))
			prossesTime = nbt.getFloat("prossestime");
		
		if(nbt.hasKey("energy"))
			energy = nbt.getInteger("energy");
		
		if(nbt.hasKey("capacity"))
			capacity = nbt.getInteger("capacity");
		
		if(nbt.hasKey("encd"))
			enable_costdisk = nbt.getBoolean("encd");
	}
	
	private void updateTFInteger()
	{
		String text = textField_size.getText();
		boolean invalid = false;
		try
		{
			double disk_size_d = Double.parseDouble(text);
			if(disk_size_d > disk_maxsize)
				invalid = true;
			else
				disk_size = (int) disk_size_d;
		}
		catch (Exception e)
		{
			
		}
		
		if(invalid)
		{
			disk_size = disk_maxsize;
			textField_size.setText(""+disk_size);
		}
	}
	
	public void sendInfo(NBTTagCompound nbt)
	{
		NetworkUtils.sendToServerTheData(tileentity, this, nbt);
	}

}
