package javapower.storagetech.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodeStructureConstructor;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import javapower.storagetech.util.SCElement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketStructureConstructor
{
	BlockPos pos;
	CompoundNBT nbt;
	int flag;
	
	public static final int UPDATE_CLIENT = 1;
	public static final int PUT_ALL_TO_SERVER = 2;
	public static final int DELETE_ELEMENT_AT_INDEX_SERVER = 3;
	public static final int UPDATE_ELEMENT_AT_INDEX_SERVER = 4;
	public static final int ADD_ELEMENT_SERVER = 5;
	
	
	public PacketStructureConstructor(int _flag, BlockPos _pos, CompoundNBT _nbt)
	{
		pos = _pos;
		nbt = _nbt;
		flag = _flag;
	}
	
	
	
	public static void encoder(PacketStructureConstructor msg, PacketBuffer packetBuffer)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(baos);
		try
		{
			CompressedStreamTools.write(msg.nbt, output);
			output.flush();
			
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES+(Integer.SIZE*2)+baos.size());
			
			buffer.putLong(msg.pos.toLong());
			buffer.putInt(msg.flag);
			buffer.putInt(baos.size());
			buffer.put(baos.toByteArray());
			
			packetBuffer.writeByteArray(buffer.array());
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static PacketStructureConstructor decoder(PacketBuffer packetBuffer)
	{
		ByteBuffer buffer = ByteBuffer.allocate(packetBuffer.capacity());
		buffer.put(packetBuffer.readByteArray());
		buffer.flip();
		
		BlockPos l_pos = BlockPos.fromLong(buffer.getLong());
		int flag = buffer.getInt();
		byte[] buffer_nbt = new byte[buffer.getInt()];
		
		for(int i = 0; i < buffer_nbt.length; ++i)
			buffer_nbt[i] = buffer.get();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer_nbt);
		DataInputStream input = new DataInputStream(bais);
		try
		{
			CompoundNBT l_nbt = CompressedStreamTools.read(input);
			return new PacketStructureConstructor(flag, l_pos, l_nbt);
		}
		catch (IOException e)
		{
			return new PacketStructureConstructor(flag, l_pos, new CompoundNBT());
		}
	}
	
	public static void handle(PacketStructureConstructor msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
			{
				PlayerEntity sender = ctx.get().getSender();
				
				if(sender.world instanceof ServerWorld)
				{
					TileEntity te = sender.world.getTileEntity(msg.pos);
					if(te instanceof TileEntityStructureConstructor)
					{
						NetworkNodeStructureConstructor node = ((TileEntityStructureConstructor)te).getNode();
						if(msg.flag == UPDATE_CLIENT)
						{
							sendPlayerUpdate(node, sender, msg);
						}
						else if(msg.flag == PUT_ALL_TO_SERVER)
						{
							if(msg.nbt.contains("Elements"))
							{
								node.elements.clear();
								
								ListNBT list_elements = msg.nbt.getList("Elements", Constants.NBT.TAG_COMPOUND);
						        
						        for (int i = 0; i < list_elements.size(); ++i)
						        {
						        	CompoundNBT nbt_element = list_elements.getCompound(i);
						        	SCElement element = SCElement.getFromNBT((TileEntityStructureConstructor) te, nbt_element);
						        	if(element != null)
						        		node.elements.add(element);
						        }
						        
						        node.markDirty();
							}
							
							sendPlayerUpdate(node, sender, msg);
						
						}
						else if(msg.flag == DELETE_ELEMENT_AT_INDEX_SERVER)
						{
							if(msg.nbt.contains("Elindx"))
							{
								int index = msg.nbt.getInt("Elindx");
								if(node.elements.size() > index)
								{
									node.elements.remove(index);
									node.markDirty();
								}
							}
							
							sendPlayerUpdate(node, sender, msg);
						}
						else if(msg.flag == UPDATE_ELEMENT_AT_INDEX_SERVER)
						{
							if(msg.nbt.contains("Elindx"))
							{
								int index = msg.nbt.getInt("Elindx");
								if(node.elements.size() > index && msg.nbt.contains("Element"))
								{
									CompoundNBT nbt_element = msg.nbt.getCompound("Element");
									SCElement element = SCElement.getFromNBT((TileEntityStructureConstructor) te, nbt_element);
									if(element != null)
									{
										node.elements.set(index, element);
										node.markDirty();
									}
								}
							}
							
							sendPlayerUpdate(node, sender, msg);
						}
						else if(msg.flag == ADD_ELEMENT_SERVER)
						{
							if(msg.nbt.contains("id"))
							{
								SCElement element = SCElement.getFromNBT((TileEntityStructureConstructor) te, msg.nbt);
								if(element != null)
								{
									node.elements.add(element);
									node.markDirty();
								}
							}
							
							sendPlayerUpdate(node, sender, msg);
						}
					}
				}
			}
			else if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			{
				clientHandle(msg, ctx);
			}
	    });
	    ctx.get().setPacketHandled(true);
	}
	
	private static void sendPlayerUpdate(NetworkNodeStructureConstructor node, PlayerEntity sender, PacketStructureConstructor msg)
	{
		CompoundNBT nbt_send = new CompoundNBT();
        
        ListNBT list_elements_send = new ListNBT();
        for (SCElement element : node.elements)
        {
        	if(element != null)
        	{
	        	CompoundNBT nbt_element = new CompoundNBT();
	        	element.writeToNBT(nbt_element);
	        	list_elements_send.add(nbt_element);
        	}
        }
        nbt_send.put("Elements", list_elements_send);
		
		StorageTech.INSTANCE_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> sender.world.getChunkAt(msg.pos)), new PacketStructureConstructor(0, msg.pos, nbt_send));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void clientHandle(PacketStructureConstructor msg, Supplier<NetworkEvent.Context> ctx)
	{
		net.minecraft.client.world.ClientWorld world = net.minecraft.client.Minecraft.getInstance().world;
		TileEntity te = world.getTileEntity(msg.pos);
		if(te instanceof TileEntityStructureConstructor)
		{
			TileEntityStructureConstructor tesc = (TileEntityStructureConstructor) te;
			if(tesc.elements_client == null)
				tesc.elements_client = new ArrayList<SCElement.Client>();
			else
				tesc.elements_client.clear();
			
			ListNBT list_elements = msg.nbt.getList("Elements", Constants.NBT.TAG_COMPOUND);
	        
	        for (int i = 0; i < list_elements.size(); ++i)
	        {
	        	CompoundNBT nbt_element = list_elements.getCompound(i);
	        	SCElement.Client element = SCElement.Client.getFromNBT(nbt_element);
	        	if(element != null)
	        		tesc.elements_client.add(element);
	        }
		}
		
		net.minecraft.client.gui.screen.Screen thisscreen = net.minecraft.client.Minecraft.getInstance().currentScreen;
		if(thisscreen instanceof javapower.storagetech.screen.ScreenStructureConstructor)
		{
			((javapower.storagetech.screen.ScreenStructureConstructor)thisscreen).updateElements();
		}
	}
	
	public static void toServer_requestClientUpdate(BlockPos _pos)
	{
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStructureConstructor(UPDATE_CLIENT, _pos, new CompoundNBT()));
	}
	
	public static void toServer_requestDeleteAtIndex(BlockPos _pos, int _index)
	{
		CompoundNBT nbt_toSend = new CompoundNBT();
		nbt_toSend.putInt("Elindx", _index);
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStructureConstructor(DELETE_ELEMENT_AT_INDEX_SERVER, _pos, nbt_toSend));
	}
	
	public static void toServer_requestUpdateAtIndex(BlockPos _pos, int _index, SCElement.Client _element)
	{
		CompoundNBT nbt_toSend = new CompoundNBT();
		nbt_toSend.putInt("Elindx", _index);
		
		CompoundNBT nbt_element = new CompoundNBT();
		_element.writeToNBT(nbt_element);
		nbt_toSend.put("Element", nbt_element);
		
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStructureConstructor(UPDATE_ELEMENT_AT_INDEX_SERVER, _pos, nbt_toSend));
	}
	
	public static void toServer_requestAdd(BlockPos _pos, SCElement.Client _element)
	{
		CompoundNBT nbt_toSend = new CompoundNBT();
		_element.writeToNBT(nbt_toSend);
		
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStructureConstructor(ADD_ELEMENT_SERVER, _pos, nbt_toSend));
	}
	
	public static void toServer_requestUpdateAll(BlockPos _pos, List<SCElement.Client> _elements)
	{
		CompoundNBT nbt_toSend = new CompoundNBT();
		ListNBT list_elements_send = new ListNBT();
		for (SCElement.Client element : _elements)
		{
			if(element != null)
			{
		        	CompoundNBT nbt_element = new CompoundNBT();
		        	element.writeToNBT(nbt_element);
		        	list_elements_send.add(nbt_element);
		    }
		}
		nbt_toSend.put("Elements", list_elements_send);
		
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketStructureConstructor(PUT_ALL_TO_SERVER, _pos, nbt_toSend));
	}
	
	
}
