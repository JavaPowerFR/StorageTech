package javapower.storagetech.item;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.IItemRegister;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemDebug extends Item implements IItemRegister
{
	public ItemDebug()
	{
		setCreativeTab(StorageTech.creativeTab);
		setUnlocalizedName("itemdebug");
		setRegistryName("itemdebug");
		setMaxStackSize(1);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		IBlockState bs = worldIn.getBlockState(pos);
		if(bs != null)
		{
			player.sendMessage(new TextComponentString("IBlockState:"));
			player.sendMessage(new TextComponentString("  -"+bs));
			player.sendMessage(new TextComponentString("  -"+bs.getClass()));
			player.sendMessage(new TextComponentString("  -"+bs.getClass().getName()));
			Block block = bs.getBlock();
			if(block != null)
			{
				player.sendMessage(new TextComponentString("Block:"));
				player.sendMessage(new TextComponentString(" -"+block));
				player.sendMessage(new TextComponentString(" -"+block.getClass()));
				player.sendMessage(new TextComponentString(" -"+block.getClass().getName()));
				player.sendMessage(new TextComponentString(" -UN: "+block.getUnlocalizedName()));
				player.sendMessage(new TextComponentString(" -LN: "+block.getLocalizedName()));
				player.sendMessage(new TextComponentString(" -Metadata: "+block.getMetaFromState(bs)));
				
			}
		}
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if(tileEntity != null)
		{
			player.sendMessage(new TextComponentString("TileEntity:"));
			player.sendMessage(new TextComponentString(" -"+tileEntity));
			player.sendMessage(new TextComponentString(" -"+tileEntity.getClass()));
			player.sendMessage(new TextComponentString(" -"+tileEntity.getClass().getName()));
			
			player.sendMessage(new TextComponentString(" ->Interface:"));
			Class<?>[] classints = tileEntity.getClass().getInterfaces();
			for(Class<?> c : classints)
				player.sendMessage(new TextComponentString("   -"+c.getName()));
			
			player.sendMessage(new TextComponentString(" ->NBT:"));
			player.sendMessage(new TextComponentString("   -"+tileEntity.writeToNBT(new NBTTagCompound())));
		}
		
		
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public Item getItem()
	{
		return this;
	}
}
