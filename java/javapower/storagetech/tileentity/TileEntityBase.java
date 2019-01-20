package javapower.storagetech.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TileEntityBase extends TileEntity
{
	public NBTTagCompound write(NBTTagCompound tag)
	{
        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag)
    {
        return tag;
    }

    public void read(NBTTagCompound tag)
    {
    	
    }

    public void readUpdate(NBTTagCompound tag)
    {
    	
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeUpdate(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        readUpdate(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        readUpdate(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        read(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        return write(super.writeToNBT(tag));
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }
    
    @Override
    public int hashCode()
    {
        int result = pos.hashCode();
        result = 31 * result + world.provider.getDimension();
        return result;
    }
    
    public void UpdateCkunk()
	{
		if(world != null)
		{
			Chunk ch = world.getChunkFromBlockCoords(pos);
			if(ch != null)
				ch.markDirty();
		}
	}
}
