package javapower.storagetech.block;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.util.NetworkUtils;

import javapower.storagetech.container.ContainerCustomFluidStorage;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.ItemMemoryFluid;
import javapower.storagetech.node.NetworkNodeCustomFluidStorage;
import javapower.storagetech.tileentity.TileEntityCustomFluidStorage;
import javapower.storagetech.util.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockCustomFluidStorage extends NetworkNodeBlock
{
	protected Item thisItem;
	public static String raw_name = "customfluidstorageblock";
	
	public BlockCustomFluidStorage()
	{
		super(STBlocks.DEFAULT_BLOCK_PROPERTIES);
		setRegistryName(StorageTech.MODID, raw_name);
	}
	
	public Item getItem()
	{
		if(thisItem == null)
		{
			thisItem = new BaseBlockItem(this, new Item.Properties().group(StorageTech.creativeTab))
			{
				@Override
			    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
				{
			        super.addInformation(stack, world, tooltip, flag);

			        if (isValid(stack))
			        {
			            UUID id = getId(stack);

			            API.instance().getStorageDiskSync().sendRequest(id);

			            StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(id);
			            if (data != null)
			            {
			                if (data.getCapacity() == -1)
			                {
			                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored", API.instance().getQuantityFormatter().format(data.getStored())).func_230530_a_(Styles.GRAY));
			                } else {
			                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())).func_230530_a_(Styles.GRAY));
			                }
			            }

			            if (flag.isAdvanced())
			            {
			                tooltip.add(new StringTextComponent(id.toString()).func_230530_a_(Styles.GRAY));
			            }
			        }
			    }

			    @Override
			    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
			    {
			        ItemStack storageStack = player.getHeldItem(hand);

			        if (!world.isRemote && player.isCrouching())
			        {
			            UUID diskId = null;
			            IStorageDisk<?> disk = null;

			            if (isValid(storageStack))
			            {
			                diskId = getId(storageStack);
			                disk = API.instance().getStorageDiskManager((ServerWorld) world).get(diskId);
			            }

			            // Newly created storages won't have a tag yet, so allow invalid disks as well.
			            if (disk == null || disk.getStored() == 0)
			            {
			            	ItemStack storagePart = ItemMemoryFluid.createItem(disk.getCapacity());

			                if (!player.inventory.addItemStackToInventory(storagePart.copy()))
			                {
			                    InventoryHelper.spawnItemStack(world, player.getPosX(), player.getPosY(), player.getPosZ(), storagePart);
			                }

			                if (disk != null)
			                {
			                    API.instance().getStorageDiskManager((ServerWorld) world).remove(diskId);
			                    API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
			                }

			                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSBlocks.MACHINE_CASING));
			            }
			        }

			        return new ActionResult<>(ActionResultType.PASS, storageStack);
			    }

			    @Override
			    public int getEntityLifespan(ItemStack stack, World world)
			    {
			        return Integer.MAX_VALUE;
			    }

			    private UUID getId(ItemStack disk)
			    {
			        return disk.getTag().getUniqueId(StorageNetworkNode.NBT_ID);
			    }

			    private boolean isValid(ItemStack disk)
			    {
			        return disk.hasTag() && disk.getTag().hasUniqueId(StorageNetworkNode.NBT_ID);
			    }
			    
			    @Override
				public ITextComponent getDisplayName(ItemStack stack)
				{
					if(stack.hasTag() && stack.getTag().contains("capacity"))
						return new TranslationTextComponent("block.storagetech.customfluidstorageblock.val", Tools.longFormatToString(stack.getTag().getInt("capacity")));
					
					return super.getDisplayName(stack);
				}
			    
			};
			
			thisItem.setRegistryName(StorageTech.MODID,raw_name);
		}
		return thisItem;
	}
	
	@Override
	public Item asItem()
	{
		return getItem();
	}

	@Override
	public Block getBlock()
	{
		return this;
	}
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
	{
        if (!world.isRemote)
        {
            NetworkNodeCustomFluidStorage storage = ((TileEntityCustomFluidStorage) world.getTileEntity(pos)).getNode();
            if(stack.hasTag())
            {
	            storage.storageCapacity = stack.getTag().getInt("capacity");
	            
	            if (stack.getTag().hasUniqueId(NetworkNodeCustomFluidStorage.NBT_ID))
	            {
	                storage.setStorageId(stack.getTag().getUniqueId(NetworkNodeCustomFluidStorage.NBT_ID));
	            }
            }

            storage.loadStorage();
        }
        
        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileEntityCustomFluidStorage();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!world.isRemote)
        {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui((ServerPlayerEntity) player, new PositionalTileContainerProvider<TileEntityCustomFluidStorage>(
                ((TileEntityCustomFluidStorage) world.getTileEntity(pos)).getNode().getTitle(),
                (tile, windowId, inventory, p) -> new ContainerCustomFluidStorage(tile, player, windowId),
                pos
            ), pos));
        }

        return ActionResultType.SUCCESS;
    }
    
    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
    	ItemStack stack = new ItemStack(this);
    	TileEntity te = worldIn.getTileEntity(pos);
    	if(te instanceof TileEntityCustomFluidStorage)
    	{
    		((TileEntityCustomFluidStorage)te).fillItemStackDrop(stack, false);
    	}
    	return stack;
    }
    
    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
    	super.onBlockHarvested(worldIn, pos, state, player);
    	if (worldIn instanceof ServerWorld)
        {
    		ItemStack stack = new ItemStack(this);
        	TileEntity te = worldIn.getTileEntity(pos);
        	if(te instanceof TileEntityCustomFluidStorage)
        	{
        		((TileEntityCustomFluidStorage)te).fillItemStackDrop(stack, true);
        	}
            spawnAsEntity(worldIn, pos, stack);
        }
    }
    
    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack)
    {
    	player.addStat(Stats.BLOCK_MINED.get(this));
        player.addExhaustion(0.005F);
    }
    
    public static ItemStack createItemBlock(int value)
    {
    	ItemStack stack = new ItemStack(STBlocks.blockCustomFluidStorage.getItem());
    	if(!stack.hasTag())
    		stack.setTag(new CompoundNBT());
    	
		stack.getTag().putInt("capacity", value);
    	return stack;
    }

}
