package javapower.storagetech.util;

public class Zone2D
{
	public Vector2 start, end;
	
	public Zone2D(int _XStart, int _YStart, int _XEnd, int _YEnd)
	{
		start = new Vector2(_XStart, _YStart);
		end = new Vector2(_XEnd, _YEnd);
	}
	
	public Zone2D(Vector2 _start, Vector2 _end)
	{
		start = _start;
		end = _end;
	}
	
	public Zone2D(Vector2 _start, Vector2 _end, boolean _static)
	{
		if(_static)
		{
			start = _start.copy();
			end = _end.copy();
		}
		else
		{
			start = _start;
			end = _end;
		}
	}
	
	public boolean MouseIsOnArea(Vector2 mousepos)
	{
		if(mousepos.x >= start.x && mousepos.x <= end.x && mousepos.y >= start.y && mousepos.y <= end.y)return true;
		return false;
	}
	
	public boolean MouseIsOnArea(int x, int y)
	{
		if(x >= start.x && x <= end.x && y >= start.y && y <= end.y)return true;
		return false;
	}
	
	public boolean MouseIsOnArea_X(int x)
	{
		if(x >= start.x && x <= end.x)return true;
		return false;
	}
	
	public boolean MouseIsOnArea_Y(int y)
	{
		if(y >= start.y && y <= end.y)return true;
		return false;
	}
	
}
