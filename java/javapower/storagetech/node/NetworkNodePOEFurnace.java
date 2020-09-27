package javapower.storagetech.node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;

import javapower.storagetech.api.IEnergyStorageNode;
import javapower.storagetech.api.STAPI;
import javapower.storagetech.block.BlockPOEFurnace;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.util.EnergyBuffer;
import javapower.storagetech.util.VirtualInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class NetworkNodePOEFurnace extends NetworkNode implements ICraftingPatternContainer, IItemHandler
{
	public static final ResourceLocation NETWORK_NODE_ID = new ResourceLocation(StorageTech.MODID, BlockPOEFurnace.raw_name);
	
	public static final int TICK_UPDATE_DELAY = 5;
	private static final int ENERGY_USAGE = 4;
	
	private static final ITextComponent DEFAULT_NAME = new TranslationTextComponent("gui.storagetech.poe_furnace");
	
	private static final String NBT_UUID = "CrafterUuid";
	public static final String NBT_INV_FURNACE = "InvFurnace";
	
	private STData stData = null;
	private int tickUpdate;
	
	private BaseItemHandler inputInventory = new BaseItemHandler(2).addListener(new NetworkNodeInventoryListener(this));
	private BaseItemHandler outputInventory = new BaseItemHandler(2).addListener(new NetworkNodeInventoryListener(this));
	
	private boolean flagOn = false;
	
    private BaseItemHandler patternsInventory = new BaseItemHandler(9)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }
    }
        .addValidator(new PatternItemValidator(world))
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) ->
        {
            if (!reading)
            {
                if (!world.isRemote)
                {
                    invalidate();
                }

                if (network != null)
                {
                    network.getCraftingManager().invalidate();
                }
            }
        });
        
    private List<ICraftingPattern> patterns = new ArrayList<>();
    
    private UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED)
            .addListener(new NetworkNodeInventoryListener(this));
    
    //private ItemStack[] inputs = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    //private ItemStack[] outputs = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    
    private VirtualInventory[] inventorys = new VirtualInventory[]
    		{
    				new VirtualInventory(() -> inputInventory.getStackInSlot(0)),
    				new VirtualInventory(() -> inputInventory.getStackInSlot(1)),
    		};
    
    private FurnaceRecipe[] recipes = new FurnaceRecipe[2];
	private int[] recipes_progress = new int[2];
	
	private EnergyBuffer energyBuffer = new EnergyBuffer(200000, 20000).setEvent(() -> markDirty());
	
	@Nullable
    private UUID uuid = null;
	
	public NetworkNodePOEFurnace(World world, BlockPos pos)
	{
		super(world, pos);
	}
	
	private void invalidate()
	{
        patterns.clear();

        for (int i = 0; i < patternsInventory.getSlots(); ++i)
        {
            ItemStack patternStack = patternsInventory.getStackInSlot(i);

            if (!patternStack.isEmpty())
            {
                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(world, patternStack, this);

                if (pattern.isValid())
                    patterns.add(pattern);
            }
        }
    }

	@Override
	public int getEnergyUsage()
	{
        return RS.SERVER_CONFIG.getCrafter().getUsage() + upgrades.getEnergyUsage() + (RS.SERVER_CONFIG.getCrafter().getPatternUsage() * patterns.size());
    }

	@Override
	public ResourceLocation getId()
	{
		return NETWORK_NODE_ID;
	}
	
	@Override
    protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
	{
        super.onConnectedStateChange(network, state, cause);

        network.getCraftingManager().invalidate();
        if(state)
			stData = STAPI.getNetworkManager((ServerWorld) world).getStData(network);
		else
			stData = null;
    }

    @Override
    public void onDisconnected(INetwork network)
    {
        super.onDisconnected(network);

        network.getCraftingManager().getTasks().stream()
            .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
            .forEach(task -> network.getCraftingManager().cancel(task.getId()));
    }
	
	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		
		StackUtils.readItems(patternsInventory, 0, tag);

        invalidate();

        StackUtils.readItems(upgrades, 1, tag);
        StackUtils.readItems(inputInventory, 2, tag);
        StackUtils.readItems(outputInventory, 3, tag);
        
		if (tag.hasUniqueId(NBT_UUID))
            uuid = tag.getUniqueId(NBT_UUID);
		
		energyBuffer.ReadFromNBT(tag);
		
		for(int i = 0; i < 2; ++i)
			updateRecipe(i);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		
		StackUtils.writeItems(patternsInventory, 0, tag);
		StackUtils.writeItems(upgrades, 1, tag);
		StackUtils.writeItems(inputInventory, 2, tag);
		StackUtils.writeItems(outputInventory, 3, tag);
		
		if (uuid != null)
            tag.putUniqueId(NBT_UUID, uuid);
		
		energyBuffer.WriteToNBT(tag);
		
		return tag;
	}
	
	@Override
    public int getUpdateInterval()
	{
        switch (upgrades.getUpgradeCount(UpgradeItem.Type.SPEED))
        {
            case 0:
                return 10;
            case 1:
                return 8;
            case 2:
                return 6;
            case 3:
                return 4;
            case 4:
                return 2;
            default:
                return 0;
        }
    }
	
	@Override
	public void update()
	{
		super.update();
		
		if(!world.isRemote && canUpdate())
		{
			if(tickUpdate > TICK_UPDATE_DELAY)
			{
				tickUpdate = 0;
				if(stData != null)
				{
					if(energyBuffer.haveSpace())
					{
						for(IEnergyStorageNode energy_node : stData.getEnergyStorages())
						{
							if(energy_node != null)
							{
								long space = energy_node.getEnergyStored();
								if(space > 0)
								{
									int insertedEnergy = energyBuffer.receiveEnergy((int) Math.min(space, energy_node.getIOCapacity()), false); 
									energy_node.extractEnergy(insertedEnergy, false);
									break;
								}
							}
						}
					}
				}
			}
			else
				++tickUpdate;
			
			for(int recipeId = 0; recipeId < 2; ++recipeId)
			{
				if(recipes[recipeId] != null)
				{
					if(inputInventory.getStackInSlot(recipeId).isEmpty())
					{
						recipes[recipeId] = null;
						markDirty();
					}
					else
					{
						//update progress
						if(recipes_progress[recipeId] >= recipes[recipeId].getCookTime())
						{
							//finish
							
							if(canPutToOutput(recipes[recipeId].getRecipeOutput(), recipeId))
							{
								NonNullList<Ingredient> ings = recipes[recipeId].getIngredients();
								if(ings.size() > 0)
								{
									ItemStack[] matchings = ings.get(0).getMatchingStacks();
									for(ItemStack match : matchings)
									{
										if(inputInventory.getStackInSlot(recipeId).isItemEqual(match))
										{
											if(inputInventory.getStackInSlot(recipeId).getCount() >= match.getCount())
											{
												if(inputInventory.getStackInSlot(recipeId).getCount() == match.getCount())
												{
													inputInventory.setStackInSlot(recipeId, ItemStack.EMPTY);
												}
												else
												{
													inputInventory.getStackInSlot(recipeId).shrink(recipes[recipeId].getRecipeOutput().getCount());
												}
													if(outputInventory.getStackInSlot(recipeId).isEmpty())
														outputInventory.setStackInSlot(recipeId, recipes[recipeId].getRecipeOutput());
													else
														outputInventory.getStackInSlot(recipeId).grow(recipes[recipeId].getRecipeOutput().getCount());
													
													if(inputInventory.getStackInSlot(recipeId).equals(ItemStack.EMPTY))
														recipes[recipeId] = null;
											}
											break;
										}
									}
								}
								
								recipes_progress[recipeId] = 0;
								markDirty();
							}
						}
						else
						{
							int incrm = 1 + (int)Math.pow(4, upgrades.getUpgradeCount(UpgradeItem.Type.SPEED));
							if(energyBuffer.canExtractLevel(incrm*ENERGY_USAGE))
							{
								recipes_progress[recipeId] += incrm;
								energyBuffer.energy -= incrm*ENERGY_USAGE;
								markDirty();
							}
						}
					}
				}
				
				//try to push output to netowrk
				if(!outputInventory.getStackInSlot(recipeId).isEmpty())
				{
					outputInventory.setStackInSlot(recipeId, network.insertItemTracked(outputInventory.getStackInSlot(recipeId), outputInventory.getStackInSlot(recipeId).getCount()));
				}
			}
		}
		
		if(flagOn != isOn())
		{
			flagOn = isOn();
			UpdateLitBlockState();
		}
	}

	@Override
	public IFluidHandler getConnectedFluidInventory()
	{
		return null;
	}

	@Override
	public IItemHandler getConnectedInventory()
	{
		return this;
	}

	@Override
	public TileEntity getConnectedTile()
	{
		return null;
	}

	@Override
	public ITextComponent getName()
	{
		return DEFAULT_NAME;
	}

	@Override
	public IItemHandlerModifiable getPatternInventory()
	{
		return patternsInventory;
	}

	@Override
	public List<ICraftingPattern> getPatterns()
	{
		return patterns;
	}

	@Override
	public BlockPos getPosition()
	{
		return pos;
	}

	@Override
	public ICraftingPatternContainer getRootContainer()
	{
		return this;
	}

	@Override
	public UUID getUuid()
	{
		if (uuid == null)
		{
            uuid = UUID.randomUUID();

            markDirty();
        }

        return uuid;
	}

	@Override
	public void unlock()
	{
		
	}
	
	public IItemHandler getUpgrades()
	{
        return upgrades;
    }

    @Override
    public IItemHandler getDrops()
    {
        return new CombinedInvWrapper(patternsInventory, upgrades, inputInventory, outputInventory);
    }
    
    public IItemHandler getPatternItems()
    {
		return patternsInventory;
	}
    
    @Override
	public int getSlots()
	{
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if(inputInventory.getStackInSlot(slot).isEmpty())
		{
			if(!simulate)
			{
				inputInventory.setStackInSlot(slot, stack.copy());
				updateRecipe(slot);
				markDirty();
			}
			return ItemStack.EMPTY;
		}
		else
		{
			if(inputInventory.getStackInSlot(slot).isItemEqual(stack))
			{
				int space = inputInventory.getStackInSlot(slot).getMaxStackSize() - inputInventory.getStackInSlot(slot).getCount();
				if(space >= stack.getCount())
				{
					if(!simulate)
					{
						inputInventory.getStackInSlot(slot).grow(stack.getCount());
						markDirty();
					}
					return ItemStack.EMPTY;
				}
				else if(space > 0)
				{
					if(!simulate)
					{
						inputInventory.getStackInSlot(slot).setCount(inputInventory.getStackInSlot(slot).getMaxStackSize());
						markDirty();
					}
					
					ItemStack stack_return = stack.copy();
					stack_return.shrink(space);
					return stack_return;
				}
			}
		}
		
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		if(inputInventory.getStackInSlot(slot).isEmpty())
			return 64;
		
		return inputInventory.getStackInSlot(slot).getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return inputInventory.getStackInSlot(slot).isEmpty() && canPutIn(stack, slot);
	}
	
	public FurnaceRecipe getRecipe(int furnaceId)
	{
		return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, inventorys[furnaceId], world).orElse(null);
	}
	
	public FurnaceRecipe getRecipeForStack(ItemStack stack, int furnaceId)
	{
		return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new VirtualInventory(() -> stack), world).orElse(null);
	}
	
	public boolean canPutIn(ItemStack stack, int furnaceId)
	{
		if(inputInventory.getStackInSlot(furnaceId).isItemEqual(stack))
			return true;
		
		FurnaceRecipe recipe = getRecipeForStack(stack, furnaceId);
		
		if(recipe == null)
			return false;
		
		if(recipe.getRecipeOutput().isEmpty())
			return false;
		
		return true;
	}
	
	public boolean canPutToOutput(ItemStack stack, int furnaceId)
	{
		if(stack == null || stack.isEmpty())
			return false;
		
		if(outputInventory.getStackInSlot(furnaceId).isEmpty())
			return true;
		
		if(outputInventory.getStackInSlot(furnaceId).isItemEqual(stack))
		{
			int space = outputInventory.getStackInSlot(furnaceId).getMaxStackSize() - outputInventory.getStackInSlot(furnaceId).getCount();
			if(space >= stack.getCount())
				return true;
		}
		
		return false;
	}
	
	public void updateRecipe(int furnaceId)
	{
		FurnaceRecipe recipe = getRecipe(furnaceId);
		if(recipe != recipes[furnaceId])
		{
			recipes_progress[furnaceId] = 0;
			recipes[furnaceId] = recipe;
		}
	}

	public int getProcessing(int i)
	{
		if(recipes[i] != null)
		{
			return (recipes_progress[i]*100)/recipes[i].getCookTime();
		}
		return 0;
	}

	public EnergyBuffer getEnergyBuffer()
	{
		return energyBuffer;
	}

	public BaseItemHandler getInputs()
	{
		return inputInventory;
	}
	
	private boolean isOn()
	{
	      return recipes_progress[0] > 0 || recipes_progress[1] > 0;
	}
	
	public void UpdateLitBlockState()
	{
		world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BlockPOEFurnace.LIT, Boolean.valueOf(this.isOn())), 3);
	}
}
