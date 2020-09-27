package javapower.storagetech.mekanism.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerFactory;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.container.ContainerGasDrive;
import javapower.storagetech.mekanism.container.ContainerGasExporter;
import javapower.storagetech.mekanism.container.ContainerGasImporter;
import javapower.storagetech.mekanism.node.NetworkNodeGasDrive;
import javapower.storagetech.mekanism.node.NetworkNodeGasExporter;
import javapower.storagetech.mekanism.node.NetworkNodeGasImporter;
import javapower.storagetech.mekanism.tileentity.TileEntityGasDrive;
import javapower.storagetech.mekanism.tileentity.TileEntityGasExporter;
import javapower.storagetech.mekanism.tileentity.TileEntityGasImporter;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.IForgeRegistry;

public class MKBlocks
{
	public static final BlockGasDrive blockGasDrive = new BlockGasDrive();
	public static final BlockGasImporter blockGasImporter = new BlockGasImporter();
	public static final BlockGasExporter blockGasExporter = new BlockGasExporter();
	
	public static void registerBlocks(IForgeRegistry<Block> registry)
	{
		registry.register(blockGasDrive.getBlock());
		registry.register(blockGasImporter.getBlock());
		registry.register(blockGasExporter.getBlock());
		
	}

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(blockGasDrive.getItem());
		registry.register(blockGasImporter.getItem());
		registry.register(blockGasExporter.getItem());
		
	}

	public static void registerTiles(IForgeRegistry<TileEntityType<?>> registry)
	{
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityGasDrive(), blockGasDrive).build(null).setRegistryName(StorageTech.MODID, BlockGasDrive.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityGasImporter(), blockGasImporter).build(null).setRegistryName(StorageTech.MODID, BlockGasImporter.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityGasExporter(), blockGasExporter).build(null).setRegistryName(StorageTech.MODID, BlockGasExporter.raw_name)));
		
	}

	public static void registerContainers(IForgeRegistry<ContainerType<?>> registry)
	{
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerGasDrive, TileEntityGasDrive>((windowId, inv, tile) -> new ContainerGasDrive(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockGasDrive.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerGasImporter, TileEntityGasImporter>((windowId, inv, tile) -> new ContainerGasImporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockGasImporter.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerGasExporter, TileEntityGasExporter>((windowId, inv, tile) -> new ContainerGasExporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockGasExporter.raw_name));
		
	}

	public static void registerNodes(INetworkNodeRegistry registry)
	{
		registry.add(NetworkNodeGasDrive.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeGasDrive(world, pos)));
		registry.add(NetworkNodeGasImporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeGasImporter(world, pos)));
		registry.add(NetworkNodeGasExporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeGasExporter(world, pos)));
		
	}
	
	private static INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node)
	{
        node.read(tag);
        return node;
    }
	
	private static <T extends TileEntity> TileEntityType<T> registerTileDataParameters(TileEntityType<T> t)
	{
        BaseTile tile = (BaseTile) t.create();
        tile.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        return t;
    }
}
