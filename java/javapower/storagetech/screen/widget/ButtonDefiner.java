package javapower.storagetech.screen.widget;

import net.minecraft.util.ResourceLocation;

public class ButtonDefiner
{
	public int sprite_x, sprite_y, width, height, overlay_x, overlay_y;
	public ResourceLocation rl;
	
	public ButtonDefiner(int _sprite_x, int _sprite_y, int _width, int _height, int _overlay_x, int _overlay_y, ResourceLocation _rl)
	{
		sprite_x = _sprite_x;
		sprite_y = _sprite_y;
		width = _width;
		height = _height;
		overlay_x = _overlay_x;
		overlay_y = _overlay_y;
		rl = _rl;
	}
}
