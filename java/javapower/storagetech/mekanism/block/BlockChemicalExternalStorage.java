package javapower.storagetech.mekanism.block;

import java.util.List;

import javax.annotation.Nullable;

import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.CableBlock;
import com.refinedmods.refinedstorage.block.shape.ShapeCache;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.CollisionUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalExternalStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockChemicalExternalStorage extends CableBlock
{
	protected Item thisItem;
	public static String raw_name = "chemicalexternalstorage";
	
	private static final VoxelShape HEAD_NORTH = VoxelShapes.or(makeCuboidShape(3, 3, 0, 13, 13, 2), HOLDER_NORTH);
    private static final VoxelShape HEAD_EAST = VoxelShapes.or(makeCuboidShape(14, 3, 3, 16, 13, 13), HOLDER_EAST);
    private static final VoxelShape HEAD_SOUTH = VoxelShapes.or(makeCuboidShape(3, 3, 14, 13, 13, 16), HOLDER_SOUTH);
    private static final VoxelShape HEAD_WEST = VoxelShapes.or(makeCuboidShape(0, 3, 3, 2, 13, 13), HOLDER_WEST);
    private static final VoxelShape HEAD_UP = VoxelShapes.or(makeCuboidShape(3, 14, 3, 13, 16, 13), HOLDER_UP);
    private static final VoxelShape HEAD_DOWN = VoxelShapes.or(makeCuboidShape(3, 0, 3, 13, 2, 13), HOLDER_DOWN);

    public BlockChemicalExternalStorage()
    {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);

        this.setRegistryName(StorageTech.MODID, raw_name);
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
			    }

			    @Override
			    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
			    {
			        ItemStack stack = player.getHeldItem(hand);
			        return new ActionResult<>(ActionResultType.PASS, stack);
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
    public BlockDirection getDirection()
    {
        return BlockDirection.ANY;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return ShapeCache.getOrCreate(state, s ->
        {
            VoxelShape shape = getCableShape(s);

            shape = VoxelShapes.or(shape, getLineShape(s));

            return shape;
        });
    }

    private VoxelShape getLineShape(BlockState state)
    {
        Direction direction = state.get(getDirection().getProperty());

        if (direction == Direction.NORTH) return HEAD_NORTH;
        if (direction == Direction.EAST) return HEAD_EAST;
        if (direction == Direction.SOUTH) return HEAD_SOUTH;
        if (direction == Direction.WEST) return HEAD_WEST;
        if (direction == Direction.UP) return HEAD_UP;
        if (direction == Direction.DOWN) return HEAD_DOWN;

        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileEntityChemicalExternalStorage();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        /*if (!world.isRemote && CollisionUtils.isInBounds(getLineShape(state), pos, hit.getHitVec()))
        {
            return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayerEntity) player,
                    new PositionalTileContainerProvider<TileEntityChemicalExternalStorage>(
                        new TranslationTextComponent("gui.storagetech.chemical_external_storage"),
                        (tile, windowId, inventory, p) -> new ContainerChemicalExternalStorage(tile, player, windowId),
                        pos
                    ),
                    pos
                ));
        }*/

        return ActionResultType.SUCCESS;
    }
}