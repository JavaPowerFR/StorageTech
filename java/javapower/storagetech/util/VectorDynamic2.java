package javapower.storagetech.util;

public class VectorDynamic2
{
	public VectorScreen screen;
	EScreenAnchor anchor;
	Vector2 vector;
	public Vector2 pos;
	
	public VectorDynamic2(VectorScreen _screen, EScreenAnchor _anchor, Vector2 _vector)
	{
		screen = _screen;
		anchor = _anchor;
		vector = _vector;
		reCalculate();
	}
	
	public void reCalculate()
	{
		pos = calculate();
	}
	
	private Vector2 calculate()
	{
		switch(anchor)
		{
			case TOP_LEFT:
				return vector.copy();
			case TOP_MIDDLE:
				return vector.copyAndAdd(screen.screensize.x/2, 0);
			case TOP_RIGHT:
				return vector.copyAndAdd(screen.screensize.x, 0);
			case MIDDLE_LEFT:
				return vector.copyAndAdd(0, screen.screensize.y/2);
			case MIDDLE:
				return vector.copyAndAdd(screen.screensize.x/2, screen.screensize.y/2);
			case MIDDLE_RIGHT:
				return vector.copyAndAdd(screen.screensize.x, screen.screensize.y/2);
			case BOTTOM_LEFT:
				return vector.copyAndAdd(0, screen.screensize.y);
			case BOTTOM_MIDDLE:
				return vector.copyAndAdd(screen.screensize.x/2 , screen.screensize.y);
			case BOTTOM_RIGHT:
				return vector.copyAndAdd(screen.screensize.x , screen.screensize.y);
			default:
				return vector.copy();
		}
	}
	
	public Vector2 getPos()
	{
		return pos;
	}
	
	public void setAnchor(EScreenAnchor _anchor)
	{
		anchor = _anchor;
		reCalculate();
	}
	
	public void setVector(Vector2 _vector)
	{
		vector = _vector;
		reCalculate();
	}
	
	public void setScreen(VectorScreen _screen)
	{
		screen = _screen;
		reCalculate();
	}
}
