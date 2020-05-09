package javapower.storagetech.block;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.raoulvdberge.refinedstorage.block.NetworkNodeBlock;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.item.blockitem.BaseBlockItem;
import com.raoulvdberge.refinedstorage.render.Styles;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;

import javapower.storagetech.container.CustomStorageContainer;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.item.ItemMemoryItem;
import javapower.storagetech.node.CustomStorageNetworkNode;
import javapower.storagetech.tileentity.CustomStorageTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class BlockCustomStorage extends NetworkNodeBlock
{
	protected Item thisItem;
	public static String raw_name = "customstorageblock";
	
	public BlockCustomStorage()
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
			            if (data != null) {
			                if (data.getCapacity() == -1) {
			                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored", API.instance().getQuantityFormatter().format(data.getStored())).setStyle(Styles.GRAY));
			                } else {
			                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())).setStyle(Styles.GRAY));
			                }
			            }

			            if (flag.isAdvanced()) {
			                tooltip.add(new StringTextComponent(id.toString()).setStyle(Styles.GRAY));
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

			            if (isValid(storageStack)) {
			                diskId = getId(storageStack);
			                disk = API.instance().getStorageDiskManager((ServerWorld) world).get(diskId);
			            }

			            // Newly created storages won't have a tag yet, so allow invalid disks as well.
			            if (disk == null || disk.getStored() == 0)
			            {
			            	ItemStack storagePart = ItemMemoryItem.createItem(disk.getCapacity());

			                if (!player.inventory.addItemStackToInventory(storagePart.copy()))
			                {
			                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
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
            CustomStorageNetworkNode storage = ((CustomStorageTile) world.getTileEntity(pos)).getNode();

            if (stack.hasTag() && stack.getTag().hasUniqueId(CustomStorageNetworkNode.NBT_ID))
            {
                storage.setStorageId(stack.getTag().getUniqueId(CustomStorageNetworkNode.NBT_ID));
            }

            storage.loadStorage();
        }
        
        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new CustomStorageTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!world.isRemote)
        {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui((ServerPlayerEntity) player, new PositionalTileContainerProvider<CustomStorageTile>(
                ((CustomStorageTile) world.getTileEntity(pos)).getNode().getTitle(),
                (tile, windowId, inventory, p) -> new CustomStorageContainer(tile, player, windowId),
                pos
            ), pos));
        }

        return ActionResultType.SUCCESS;
    }

}
