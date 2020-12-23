package javapower.storagetech.mekanism.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerFactory;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.container.ContainerChemicalDetector;
import javapower.storagetech.mekanism.container.ContainerChemicalDrive;
import javapower.storagetech.mekanism.container.ContainerChemicalExporter;
import javapower.storagetech.mekanism.container.ContainerChemicalGrid;
import javapower.storagetech.mekanism.container.ContainerChemicalImporter;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalDetector;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalDrive;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalExporter;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalGrid;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalImporter;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalDetector;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalDrive;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalExporter;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalGrid;
import javapower.storagetech.mekanism.tileentity.TileEntityChemicalImporter;
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
	public static final BlockChemicalDrive blockChemicalDrive = new BlockChemicalDrive();
	public static final BlockChemicalImporter blockChemicalImporter = new BlockChemicalImporter();
	public static final BlockChemicalExporter blockChemicalExporter = new BlockChemicalExporter();
	public static final BlockChemicalGrid blockChemicalGrid = new BlockChemicalGrid();
	public static final BlockChemicalDetector blockChemicalDetector = new BlockChemicalDetector();
	
	public static void registerBlocks(IForgeRegistry<Block> registry)
	{
		registry.register(blockChemicalDrive.getBlock());
		registry.register(blockChemicalImporter.getBlock());
		registry.register(blockChemicalExporter.getBlock());
		registry.register(blockChemicalGrid.getBlock());
		registry.register(blockChemicalDetector.getBlock());
	}

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(blockChemicalDrive.getItem());
		registry.register(blockChemicalImporter.getItem());
		registry.register(blockChemicalExporter.getItem());
		registry.register(blockChemicalGrid.getItem());
		registry.register(blockChemicalDetector.getItem());
		
	}

	public static void registerTiles(IForgeRegistry<TileEntityType<?>> registry)
	{
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityChemicalDrive(), blockChemicalDrive).build(null).setRegistryName(StorageTech.MODID, BlockChemicalDrive.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityChemicalImporter(), blockChemicalImporter).build(null).setRegistryName(StorageTech.MODID, BlockChemicalImporter.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityChemicalExporter(), blockChemicalExporter).build(null).setRegistryName(StorageTech.MODID, BlockChemicalExporter.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityChemicalGrid(), blockChemicalGrid).build(null).setRegistryName(StorageTech.MODID, BlockChemicalGrid.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityChemicalDetector(), blockChemicalDetector).build(null).setRegistryName(StorageTech.MODID, BlockChemicalDetector.raw_name)));
		
	}

	public static void registerContainers(IForgeRegistry<ContainerType<?>> registry)
	{
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerChemicalDrive, TileEntityChemicalDrive>((windowId, inv, tile) -> new ContainerChemicalDrive(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockChemicalDrive.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerChemicalImporter, TileEntityChemicalImporter>((windowId, inv, tile) -> new ContainerChemicalImporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockChemicalImporter.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerChemicalExporter, TileEntityChemicalExporter>((windowId, inv, tile) -> new ContainerChemicalExporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockChemicalExporter.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerChemicalGrid, TileEntityChemicalGrid>((windowId, inv, tile) -> new ContainerChemicalGrid(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockChemicalGrid.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerChemicalDetector, TileEntityChemicalDetector>((windowId, inv, tile) -> new ContainerChemicalDetector(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockChemicalDetector.raw_name));
		
	}

	public static void registerNodes(INetworkNodeRegistry registry)
	{
		registry.add(NetworkNodeChemicalDrive.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeChemicalDrive(world, pos)));
		registry.add(NetworkNodeChemicalImporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeChemicalImporter(world, pos)));
		registry.add(NetworkNodeChemicalExporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeChemicalExporter(world, pos)));
		registry.add(NetworkNodeChemicalGrid.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeChemicalGrid(world, pos)));
		registry.add(NetworkNodeChemicalDetector.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeChemicalDetector(world, pos)));
		
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
