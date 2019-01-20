package javapower.storagetech.util;

public class VectorScreen
{
	public Vector2 screensize;
	public Vector2 mouse;
	
	public VectorScreen(Vector2 _screensize, Vector2 _mouse)
	{
		screensize = _screensize;
		mouse = _mouse;
	}
	
	public void onResizeScreen(int w, int h)
	{
		screensize.x = w;
		screensize.y = h;
	}
	
	public void update(int vect)
	{
		Tools.GetMouseLocalisationNoMC(mouse, vect, false);
	}
}
