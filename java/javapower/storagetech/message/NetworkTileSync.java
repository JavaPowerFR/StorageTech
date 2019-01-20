package javapower.storagetech.message;

import io.netty.buffer.ByteBuf;
import javapower.storagetech.util.BlockPosDim;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NetworkTileSync implements IMessage
{
	public BlockPosDim env;
	public NBTTagCompound nbt;
	public NBTTagCompound nbt_inf;
	
	public NetworkTileSync()
	{
		
	}
	
	public NetworkTileSync(BlockPosDim _env, NBTTagCompound _nbt, NBTTagCompound _nbt_inf)
	{
		env = _env;
		nbt = _nbt;
		nbt_inf = _nbt_inf;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound in = ByteBufUtils.readTag(buf);
		if(in == null)
			return;
		
		env = new BlockPosDim(in, "e");
		nbt = in.getCompoundTag("d");
		nbt_inf = in.getCompoundTag("i");
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		NBTTagCompound out = new NBTTagCompound();
		if(env != null)
			env.WriteToNBT(out, "e");
		out.setTag("d", nbt);
		out.setTag("i", nbt_inf);
		
		ByteBufUtils.writeTag(buf, out);
	}

}
