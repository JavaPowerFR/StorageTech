package javapower.storagetech.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityBase extends TileEntity
{
	
	public TileEntityBase(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		
		readFromNBT(tag);
	}
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		return writeToNBT(tag);
	}
	
	// ---------------- abstract ----------------
	public abstract void readFromNBT(CompoundNBT tag);
	
	public abstract CompoundNBT writeToNBT(CompoundNBT tag);

	protected abstract void readFromServer(CompoundNBT tag);
	
	protected abstract CompoundNBT writeToClient(CompoundNBT tag);
	// ---------------- end abstract ----------------
	
	
	// ---------------- NBT SYNC ----------------
	// NBT Synchronizing on chunk load
	// send to client packet
	@Override
	public CompoundNBT getUpdateTag()
	{
		CompoundNBT nbtTag = new CompoundNBT();
		super.write(nbtTag);
	    writeToClient(nbtTag);
		return nbtTag;
	}
	//recive from server packet
	@Override
	public void handleUpdateTag(CompoundNBT nbt)
	{
		super.read(nbt);
		readFromServer(nbt);
	}
	
	// NBT Synchronizing on block update
	// send to client packet
	public SUpdateTileEntityPacket getUpdatePacket()
	{
	    CompoundNBT nbtTag = new CompoundNBT();
	    super.write(nbtTag);
	    writeToClient(nbtTag);
	    return new SUpdateTileEntityPacket(getPos(), -1, nbtTag);
	}
	//recive from server packet
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		CompoundNBT nbt = pkt.getNbtCompound();
		super.read(nbt);
		readFromServer(nbt);
	}
	// ---------------- END NBT SYNC ----------------
	
	@Override
    public void markDirty()
	{
        if (world != null)
            world.markChunkDirty(pos, this);
    }

}