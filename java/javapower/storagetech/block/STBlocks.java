package javapower.storagetech.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerFactory;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.container.ContainerCustomFluidStorage;
import javapower.storagetech.container.ContainerCustomStorage;
import javapower.storagetech.container.ContainerDiskWorkbench;
import javapower.storagetech.container.ContainerFluidDiskWorkbench;
import javapower.storagetech.container.ContainerPOEDrive;
import javapower.storagetech.container.ContainerPOEExporter;
import javapower.storagetech.container.ContainerPOEImporter;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodeCustomFluidStorage;
import javapower.storagetech.node.NetworkNodeCustomStorage;
import javapower.storagetech.node.NetworkNodePOEDrive;
import javapower.storagetech.node.NetworkNodePOEExporter;
import javapower.storagetech.node.NetworkNodePOEImporter;
import javapower.storagetech.tileentity.TileEntityCustomFluidStorage;
import javapower.storagetech.tileentity.TileEntityCustomStorage;
import javapower.storagetech.tileentity.TileEntityDiskWorkbench;
import javapower.storagetech.tileentity.TileEntityFluidDiskWorkbench;
import javapower.storagetech.tileentity.TileEntityPOEDrive;
import javapower.storagetech.tileentity.TileEntityPOEExporter;
import javapower.storagetech.tileentity.TileEntityPOEImporter;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.IForgeRegistry;

public class STBlocks
{
	public static final Block.Properties DEFAULT_BLOCK_PROPERTIES = Block.Properties.create(Material.IRON).hardnessAndResistance(2.5F).sound(SoundType.METAL);
    
	public static final BlockDiskWorkbench blockDiskWorkbench = new BlockDiskWorkbench();
	public static final BlockFluidDiskWorkbench blockFluidDiskWorkbench = new BlockFluidDiskWorkbench();
	
	public static final BlockPOEDrive blockPOEDrive = new BlockPOEDrive();
	public static final BlockPOEImporter blockPOEImporter = new BlockPOEImporter();
	public static final BlockPOEExporter blockPOEExporter = new BlockPOEExporter();
	
	public static final BlockCustomStorage blockCustomStorage = new BlockCustomStorage();
	public static final BlockCustomFluidStorage blockCustomFluidStorage = new BlockCustomFluidStorage();
	
	public static final BlockGenerator blockGenerator = new BlockGenerator();
	
	public static void registerBlocks(IForgeRegistry<Block> registry)
	{
		registry.register(blockDiskWorkbench.getBlock());
		registry.register(blockFluidDiskWorkbench.getBlock());
		
		registry.register(blockPOEDrive.getBlock());
		registry.register(blockPOEImporter.getBlock());
		registry.register(blockPOEExporter.getBlock());
		
		registry.register(blockCustomStorage.getBlock());
		registry.register(blockCustomFluidStorage.getBlock());
		
		if(StorageTech.DEBUG)
			registry.register(blockGenerator.getBlock());
	}

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(blockDiskWorkbench.getItem());
		registry.register(blockFluidDiskWorkbench.getItem());
		
		registry.register(blockPOEDrive.getItem());
		registry.register(blockPOEImporter.getItem());
		registry.register(blockPOEExporter.getItem());
		
		registry.register(blockCustomStorage.getItem());
		registry.register(blockCustomFluidStorage.getItem());
		
		if(StorageTech.DEBUG)
			registry.register(blockGenerator.getItem());
	}

	public static void registerTiles(IForgeRegistry<TileEntityType<?>> registry)
	{
		registry.register(TileEntityType.Builder.create(() -> new TileEntityDiskWorkbench(), blockDiskWorkbench).build(null).setRegistryName(StorageTech.MODID, blockDiskWorkbench.name));
		registry.register(TileEntityType.Builder.create(() -> new TileEntityFluidDiskWorkbench(), blockFluidDiskWorkbench).build(null).setRegistryName(StorageTech.MODID, blockFluidDiskWorkbench.name));
		
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEDrive(), blockPOEDrive).build(null).setRegistryName(StorageTech.MODID, BlockPOEDrive.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEImporter(), blockPOEImporter).build(null).setRegistryName(StorageTech.MODID, BlockPOEImporter.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEExporter(), blockPOEExporter).build(null).setRegistryName(StorageTech.MODID, BlockPOEExporter.raw_name)));
		
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityCustomStorage(), blockCustomStorage).build(null).setRegistryName(StorageTech.MODID, BlockCustomStorage.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityCustomFluidStorage(), blockCustomFluidStorage).build(null).setRegistryName(StorageTech.MODID, BlockCustomFluidStorage.raw_name)));
		
		if(StorageTech.DEBUG)
			registry.register(TileEntityType.Builder.create(() -> new BlockGenerator.TileEntityGenerator(), blockGenerator).build(null).setRegistryName(StorageTech.MODID, blockGenerator.name));
	}

	public static void registerContainers(IForgeRegistry<ContainerType<?>> registry)
	{
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerDiskWorkbench, TileEntityDiskWorkbench>((windowId, inv, tile) -> new ContainerDiskWorkbench(windowId, tile, inv))).setRegistryName(StorageTech.MODID, blockDiskWorkbench.name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerFluidDiskWorkbench, TileEntityFluidDiskWorkbench>((windowId, inv, tile) -> new ContainerFluidDiskWorkbench(windowId, tile, inv))).setRegistryName(StorageTech.MODID, blockFluidDiskWorkbench.name));
		
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEDrive, TileEntityPOEDrive>((windowId, inv, tile) -> new ContainerPOEDrive(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEDrive.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEImporter, TileEntityPOEImporter>((windowId, inv, tile) -> new ContainerPOEImporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEImporter.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEExporter, TileEntityPOEExporter>((windowId, inv, tile) -> new ContainerPOEExporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEExporter.raw_name));
		
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerCustomStorage, TileEntityCustomStorage>((windowId, inv, tile) -> new ContainerCustomStorage(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockCustomStorage.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerCustomFluidStorage, TileEntityCustomFluidStorage>((windowId, inv, tile) -> new ContainerCustomFluidStorage(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockCustomFluidStorage.raw_name));
		
	}

	public static void registerNodes(INetworkNodeRegistry registry)
	{
		registry.add(NetworkNodePOEDrive.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEDrive(world, pos)));
		registry.add(NetworkNodePOEImporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEImporter(world, pos)));
		registry.add(NetworkNodePOEExporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEExporter(world, pos)));
		
		registry.add(NetworkNodeCustomStorage.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeCustomStorage(world, pos)));
		registry.add(NetworkNodeCustomFluidStorage.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeCustomFluidStorage(world, pos)));
		
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
