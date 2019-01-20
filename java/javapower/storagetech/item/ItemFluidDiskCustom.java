package javapower.storagetech.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.IItemRegister;
import javapower.storagetech.util.IRenderItemRegister;
import javapower.storagetech.util.ItemRenderCast;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFluidDiskCustom extends Item implements IItemRegister, IStorageDiskProvider, IRenderItemRegister
{
	public ItemFluidDiskCustom()
	{
		setRegistryName("fluidcustomdisk");
		setUnlocalizedName("fluidcustomdisk");
		setCreativeTab(StorageTech.creativeTab);
		setMaxStackSize(1);
	}
	
	@Override
	public Item getItem()
	{
		return this;
	}
	
	/*@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound())
        {
        	StorageTech.RS_API.getDefaultStorageDiskBehavior().initDisk(StorageDiskType.FLUIDS, stack);
        }
    }*/
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
        super.onUpdate(stack, world, entity, slot, selected);

        if (!world.isRemote)
        {
            if (!isValid(stack))
            {
                API.instance().getOneSixMigrationHelper().migrateDisk(world, stack);
            }

            if (!stack.hasTagCompound())
            {
                UUID id = UUID.randomUUID();

                API.instance().getStorageDiskManager(world).set(id, API.instance().createDefaultFluidDisk(world, getCapacity(stack)));
                API.instance().getStorageDiskManager(world).markForSaving();

                setId(stack, id);
            }
        }
    }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
        super.addInformation(stack, world, tooltip, flag);

        if (isValid(stack))
        {
            UUID id = getId(stack);

            API.instance().getStorageDiskSync().sendRequest(id);

            IStorageDiskSyncData data = API.instance().getStorageDiskSync().getData(id);
            if (data != null)
            {
            	tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())));
            }

            if (flag.isAdvanced())
            {
                tooltip.add(id.toString());
            }
        }
    }
    /*public void addInformation(ItemStack disk, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
        IStorageDisk storage = create(disk);

        if (storage.isValid(disk))
        {
            tooltip.add("Stored: " + storage.getStored() + "/" + storage.getCapacity());
        }
    }*/
	
	/*@Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
	{
        super.onCreated(stack, world, player);

        StorageTech.RS_API.getDefaultStorageDiskBehavior().initDisk(StorageDiskType.FLUIDS, stack);
    }
	
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack)
	{
		NBTTagCompound nbt = StorageTech.RS_API.getDefaultStorageDiskBehavior().getShareTag(StorageDiskType.FLUIDS, stack);
		if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("st_cap"))
		{
			nbt.setInteger("st_cap", stack.getTagCompound().getInteger("st_cap"));
		}
		return nbt;
	}*/

	/*@Override
	public IStorageDisk<FluidStack> create(ItemStack disk)
	{
		
		int cappacity = 1;
		if(disk != null && disk.getTagCompound() != null && disk.getTagCompound().hasKey("st_cap"))
			 cappacity = disk.getTagCompound().getInteger("st_cap");
		return StorageTech.RS_API.getDefaultStorageDiskBehavior().createFluidStorage(disk.getTagCompound(), cappacity);
	}*/
	
	@Override
    public int getEntityLifespan(ItemStack stack, World world)
	{
        return Integer.MAX_VALUE;
    }

	@Override
	public ItemRenderCast[] getItemsRender()
	{
		return new ItemRenderCast[]
				{
						new ItemRenderCast(0, "fluidcustomdisk")
				};
	}

	@Override
	public int getCapacity(ItemStack disk)
	{
		int cappacity = 1;
		if(disk != null && disk.getTagCompound() != null && disk.getTagCompound().hasKey("st_cap"))
			 cappacity = disk.getTagCompound().getInteger("st_cap");
		return cappacity;
	}

	@Override
	public UUID getId(ItemStack disk)
	{
		return disk.getTagCompound().getUniqueId("Id");
	}

	@Override
	public StorageType getType()
	{
		return StorageType.FLUID;
	}

	@Override
	public boolean isValid(ItemStack disk)
	{
		return disk.hasTagCompound() && disk.getTagCompound().hasUniqueId("Id") && disk.getTagCompound().hasKey("st_cap");
	}

	@Override
	public void setId(ItemStack disk, UUID id)
	{
		disk.setTagCompound(new NBTTagCompound());
        disk.getTagCompound().setUniqueId("Id", id);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote && playerIn.isSneaking())
		{
			ItemStack itemStack = playerIn.getHeldItem(handIn);
			if(itemStack != null && itemStack.getItem() instanceof ItemFluidDiskCustom)
			{
				UUID id = getId(itemStack);
	
	            API.instance().getStorageDiskSync().sendRequest(id);
	            IStorageDiskSyncData storageData = API.instance().getStorageDiskSync().getData(id);
	            if(storageData == null || storageData.getStored() <= 0)
	            {
	            	int cap = itemStack.getTagCompound().getInteger("st_cap");
	            	ItemStack memory_item = ItemMemory.createItem(cap, true);
	            	playerIn.setHeldItem(handIn, new ItemStack(RSItems.STORAGE_HOUSING, 1));
	            	if(cap > 0)
	            		playerIn.dropItem(memory_item, true);
	            }
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
