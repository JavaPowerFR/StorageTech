package javapower.storagetech.screen.widget;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import javapower.storagetech.util.Variable;
import javapower.storagetech.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;

public class ButtonElementToggle extends AbstractButton
{
	ButtonDefiner buttonDefOff, buttonDefOn;
	protected Variable<Boolean> onPress;
	private Vector2i mouse;
	
	public ButtonElementToggle(int x, int y, ButtonDefiner _buttonDefOff, ButtonDefiner _buttonDefOn, Variable<Boolean> _onPress, Vector2i _mouse)
	{
		super(x, y, _buttonDefOff.width, _buttonDefOff.height, null);
		
		buttonDefOff = _buttonDefOff;
		buttonDefOn = _buttonDefOn;
		
		onPress = _onPress;
		mouse = _mouse;
	}
	
	@Override
	public void renderButton(MatrixStack matrix, int _x, int _y, float tick)
	{
		ButtonDefiner buttonDef = onPress.get() ? buttonDefOn : buttonDefOff;
		
		isHovered = mouse.x >= x + _x &&
				mouse.y >= y + _y &&
				mouse.x < x + _x + width &&
				mouse.y < y + _y + height;
		
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bindTexture(buttonDef.rl);
		
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
		onPress.set(!onPress.get());
		if(onPress.get())
		{
			width = buttonDefOn.width;
			height = buttonDefOn.height;
		}
		else
		{
			width = buttonDefOff.width;
			height = buttonDefOff.height;
		}
	}
	
}
