package javapower.storagetech.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.Tools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemDiskCustom extends Item implements IStorageDiskProvider
{

	public ItemDiskCustom()
	{
		super(STItems.DEFAULT_PROPERTIES);
		setRegistryName(StorageTech.MODID,"customdisk");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected)
	{
        super.inventoryTick(stack, world, entityIn, itemSlot, isSelected);

        if(!world.isRemote && stack.hasTag() && stack.getTag().contains("s"))
		{
        	stack.getTag().putInt("st_cap", stack.getTag().getInt("s"));
            UUID id = UUID.randomUUID();
            
            API.instance().getStorageDiskManager((ServerWorld) world).set(id, API.instance().createDefaultItemDisk((ServerWorld) world, getCapacity(stack)));
            API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
            
            stack.getTag().remove("s");
            setId(stack, id);
        }
    }
	
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
                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored", API.instance().getQuantityFormatter().format(data.getStored())).func_230530_a_(Styles.GRAY));
                else
                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())).func_230530_a_(Styles.GRAY));
            }

            if (flag.isAdvanced())
                tooltip.add(new StringTextComponent(id.toString()).func_230530_a_(Styles.GRAY));
        }
    }
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains("st_cap"))
			return new TranslationTextComponent("item.storagetech.customdisk.val", Tools.longFormatToString(stack.getTag().getInt("st_cap")));
		
		return super.getDisplayName(stack);
	}
	
	@Override
    public int getEntityLifespan(ItemStack stack, World world)
	{
        return Integer.MAX_VALUE;
    }
	
	@Override
	public int getCapacity(ItemStack disk)
	{
		int cappacity = 1;
		if(disk != null && disk.getTag() != null && disk.getTag().contains("st_cap"))
			 cappacity = disk.getTag().getInt("st_cap");
		return cappacity;
	}

	@Override
	public UUID getId(ItemStack disk)
	{
		return disk.getTag().getUniqueId("Id");
	}
	
	@Override
    public void setId(ItemStack disk, UUID id)
	{
        disk.getTag().putUniqueId("Id", id);
    }

	@Override
	public StorageType getType()
	{
		return StorageType.ITEM;
	}

	@Override
    public boolean isValid(ItemStack disk)
	{
        return disk.hasTag() && disk.getTag().hasUniqueId("Id");
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		if(!worldIn.isRemote && playerIn.isCrouching())
		{
			ItemStack itemStack = playerIn.getHeldItem(handIn);
			if(itemStack != null && itemStack.getItem() instanceof ItemDiskCustom)
			{
				
				@SuppressWarnings("rawtypes")
				IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) worldIn).getByStack(itemStack);
				
				if (disk != null && disk.getStored() == 0)
				{
					int cap = itemStack.getTag().getInt("st_cap");
	            	ItemStack memory_item = ItemCustomStoragePart.createItem(cap);
	            	playerIn.setHeldItem(handIn, new ItemStack(RSItems.STORAGE_HOUSING, 1));
	            	if(cap > 0)
	            		playerIn.dropItem(memory_item, true);
				}
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
