package javapower.storagetech.screen.widget;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import javapower.storagetech.eventio.IEventVoid;
import javapower.storagetech.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;

public class ButtonElement extends AbstractButton
{
	private ButtonDefiner buttonDef;
	private IEventVoid onPress;
	private Vector2i mouse;
	
	public ButtonElement(int x, int y, ButtonDefiner _buttonDef, IEventVoid _onPress, Vector2i _mouse)
	{
		super(x, y, _buttonDef.width, _buttonDef.height, null);
		buttonDef = _buttonDef;
		onPress = _onPress;
		mouse = _mouse;
	}
	
	@Override
	public void renderButton(MatrixStack matrix, int _x, int _y, float tick)
	{
		isHovered = mouse.x >= x + _x &&
				mouse.y >= y + _y &&
				mouse.x < x + _x + width &&
				mouse.y < y + _y + height;
		
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bindTexture(buttonDef.rl);
		
		RenderSystem.enableDepthTest();
		blit(matrix, x + _x, y + _y, buttonDef.sprite_x, buttonDef.sprite_y, this.width, this.height);
		if(isHovered)
		{
			GL11.glEnable(GL11.GL_BLEND);
			blit(matrix, x + _x, y + _y, buttonDef.overlay_x, buttonDef.overlay_y, this.width, this.height);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@Override
	public void onPress()
	{
		onPress.event();
	}
	
}
