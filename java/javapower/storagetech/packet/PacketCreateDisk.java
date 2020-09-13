package javapower.storagetech.packet;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import javapower.storagetech.tileentity.ICreateDisk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketCreateDisk
{
	BlockPos pos;
	int disksize;
	
	public PacketCreateDisk(BlockPos _pos, int _disksize)
	{
		pos = _pos;
		disksize = _disksize;
	}
	
	
	
	public static void encoder(PacketCreateDisk msg, PacketBuffer packetBuffer)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES+Integer.BYTES);
		buffer.putLong(msg.pos.toLong());
		buffer.putInt(msg.disksize);
		packetBuffer.writeByteArray(buffer.array());
	}
	
	public static PacketCreateDisk decoder(PacketBuffer packetBuffer)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES+Integer.BYTES);
		buffer.put(packetBuffer.readByteArray());
		buffer.flip();
		
		return new PacketCreateDisk(BlockPos.fromLong(buffer.getLong()), buffer.getInt());
	}
	
	public static void handle(PacketCreateDisk msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			PlayerEntity sender = ctx.get().getSender();
			
			if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
			{
				if(sender.world instanceof ServerWorld)
				{
					TileEntity te = sender.world.getTileEntity(msg.pos);
					if(te instanceof ICreateDisk)
					{
						((ICreateDisk)te).createDisk(msg.disksize);
						//System.out.println(te+"   "+msg.pos+"  "+msg.disksize);
					}
				}
			}
	    });
	    ctx.get().setPacketHandled(true);
	}
	
	
}
