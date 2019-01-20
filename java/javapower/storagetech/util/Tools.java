package javapower.storagetech.util;

import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Mouse;

import javapower.storagetech.proxy.ClientProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Tools
{
	public static boolean isClient()
	{
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
	
	public static boolean isServer()
	{
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
	
	public static AxisAlignedBB getBounds(int fromX, int fromY, int fromZ, int toX, int toY, int toZ)
	{
        return new AxisAlignedBB((float) fromX / 16F, (float) fromY / 16F, (float) fromZ / 16F, (float) toX / 16F, (float) toY / 16F, (float) toZ / 16F);
    }
	
	// --
	
	private static class AdvancedRayTraceResultBase<T extends RayTraceResult>
	{
        public final AxisAlignedBB bounds;
        public final T hit;

        public AdvancedRayTraceResultBase(T mop, AxisAlignedBB bounds)
        {

            this.hit = mop;
            this.bounds = bounds;
        }

        public boolean valid()
        {
            return hit != null && bounds != null;
        }

        public double squareDistanceTo(Vec3d vec)
        {
            return hit.hitVec.squareDistanceTo(vec);
        }
    }
	
	public static class AdvancedRayTraceResult extends AdvancedRayTraceResultBase<RayTraceResult>
	{
        public AdvancedRayTraceResult(RayTraceResult mop, AxisAlignedBB bounds)
        {
            super(mop, bounds);
        }
    }
	
	public static AdvancedRayTraceResult collisionRayTrace(BlockPos pos, Vec3d start, Vec3d end, Collection<AxisAlignedBB> boxes)
	{
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult hit = null;
        int i = -1;

        for (AxisAlignedBB aabb : boxes)
        {
            AdvancedRayTraceResult result = aabb == null ? null : collisionRayTrace(pos, start, end, aabb, i, null);

            if (result != null)
            {
                double d = result.squareDistanceTo(start);
                if (d < minDistance)
                {
                    minDistance = d;
                    hit = result;
                }
            }

            i++;
        }

        return hit;
    }

    public static AdvancedRayTraceResult collisionRayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB bounds, int subHit, Object hitInfo)
    {
    	RayTraceResult result = bounds.offset(pos).calculateIntercept(start, end);

        if (result == null)
        {
            return null;
        }
        
        result = new RayTraceResult(RayTraceResult.Type.BLOCK, result.hitVec, result.sideHit, pos);
        result.subHit = subHit;
        result.hitInfo = hitInfo;

        return new AdvancedRayTraceResult(result, bounds);
    }
    
    public static boolean PlayerIsOnList(List<EntityPlayerMP> entityPlayers, EntityPlayerMP entityPlayer)
    {
    	if(entityPlayers != null && !entityPlayers.isEmpty())
    		for(EntityPlayer pl : entityPlayers)
    		{
    			if(pl.getUniqueID() == entityPlayer.getPersistentID())
    				return true;
    		}
    	return false;
    }
    
    public static void RemovePlayerOnList(List<EntityPlayerMP> entityPlayers, EntityPlayerMP entityPlayer)
    {
    	if(entityPlayers != null && !entityPlayers.isEmpty())
    	{
    		boolean isPresent = false;
    		int id = 0;
    		for(EntityPlayer pl : entityPlayers)
    		{
    			if(pl.getUniqueID() == entityPlayer.getPersistentID())
    			{
    				isPresent = true;
    				break;
    			}
    			++id;
    		}
    		
    		if(isPresent)
    			entityPlayers.remove(id);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public static void GetMouseLocalisation(Vector2 mouspos, int width, int height, int xSize, int ySize)
	{
		int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        
        float guiMPX = (float)ClientProxy.minecraft.displayWidth/ (float)width;
        float guiMPY = (float)ClientProxy.minecraft.getMinecraft().displayHeight/ (float)height;
        float guiMP = 0;
        if(guiMPX < guiMPY)guiMP = guiMPX;
        else guiMP = guiMPY;
        
        mouspos.x = (int) ((Mouse.getX()-x*guiMP)/guiMP);
        mouspos.y = (int) ((ClientProxy.minecraft.displayHeight-Mouse.getY()-y*guiMP)/guiMP);
	}
    
    public static void GetMouseLocalisationNoMC(Vector2 mouspos, int vect, boolean middleZero)
	{
    	if(middleZero)
    	{
    		mouspos.x = Mouse.getX()/vect - ClientProxy.minecraft.displayWidth/(2*vect);
			mouspos.y = (ClientProxy.minecraft.displayHeight-Mouse.getY())/vect - ClientProxy.minecraft.displayHeight/(2*vect);
    	}
    	else
    	{
			mouspos.x = Mouse.getX()/vect;
			mouspos.y = (ClientProxy.minecraft.displayHeight-Mouse.getY())/vect;
    	}
	}
    
    public static String longFormatToString(long bytes)
	{
	    if (bytes < 1000) return ""+bytes;
	    int exp = (int) (Math.log(bytes) / Math.log(1000));
	    String pre = ""+ "kMGTPE".charAt(exp-1);
	    return String.format("%.1f %s", bytes / Math.pow(1000, exp), pre);
	}
    
    public static boolean isJavaPower(EntityPlayer player)
    {
    	return player.getUniqueID().toString().equalsIgnoreCase("6d89ae8c-01f7-498c-bb36-3f76ff9dfdc9");
    }
    
    public static int moyInteger(int[] in)
    {
    	if(in == null || in.length <= 0)
    		return 0;
    	
    	int out = 0;
    	for(int i : in)
    		out += i;
    	
    	return out/in.length;
    }
    
    public static EnumFacing getFacingForm2Blocks(BlockPos core, BlockPos satellit)
    {
    	return EnumFacing.getFacingFromVector(satellit.getX() - core.getX(), satellit.getY() - core.getY(), satellit.getZ() - core.getZ());
    }
    
    public static <E> void AddOnListIfNotPresent(List<E> list, E element)
    {
    	for(E e: list)
    		if(e.equals(element))
    			return;
    	list.add(element);
    }
}
