package javapower.storagetech.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerFactory;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import javapower.storagetech.container.ContainerPOEDrive;
import javapower.storagetech.container.ContainerPOEExporter;
import javapower.storagetech.container.ContainerPOEFurnace;
import javapower.storagetech.container.ContainerPOEImporter;
import javapower.storagetech.container.ContainerPartsCombiner;
import javapower.storagetech.container.ContainerStructureConstructor;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodePOEDrive;
import javapower.storagetech.node.NetworkNodePOEExporter;
import javapower.storagetech.node.NetworkNodePOEFurnace;
import javapower.storagetech.node.NetworkNodePOEImporter;
import javapower.storagetech.node.NetworkNodeStructureConstructor;
import javapower.storagetech.tileentity.TileEntityPOEDrive;
import javapower.storagetech.tileentity.TileEntityPOEExporter;
import javapower.storagetech.tileentity.TileEntityPOEFurnace;
import javapower.storagetech.tileentity.TileEntityPOEImporter;
import javapower.storagetech.tileentity.TileEntityPartsCombiner;
import javapower.storagetech.tileentity.TileEntityStructureConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.IForgeRegistry;

public class STBlocks
{
	public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
	public static final Block.Properties DEFAULT_BLOCK_PROPERTIES = Block.Properties.create(Material.IRON).hardnessAndResistance(2.5F).sound(SoundType.METAL);
	
	public static final BlockPartsCombiner blockPartsCombiner = new BlockPartsCombiner();
	
	public static final BlockPOEDrive blockPOEDrive = new BlockPOEDrive();
	public static final BlockPOEImporter blockPOEImporter = new BlockPOEImporter();
	public static final BlockPOEExporter blockPOEExporter = new BlockPOEExporter();
	public static final BlockPOEFurnace blockPOEFurnace = new BlockPOEFurnace();
	public static final BlockStructureConstructor blockStructureConstructor = new BlockStructureConstructor();
	
	//public static final BlockAdvancedExporter blockAdvancedExporter = new BlockAdvancedExporter();
	
	public static final BlockGenerator blockGenerator = new BlockGenerator();
	
	public static void registerBlocks(IForgeRegistry<Block> registry)
	{
		registry.register(blockPartsCombiner.getBlock());
		
		registry.register(blockPOEDrive.getBlock());
		registry.register(blockPOEImporter.getBlock());
		registry.register(blockPOEExporter.getBlock());
		registry.register(blockPOEFurnace.getBlock());
		registry.register(blockStructureConstructor.getBlock());
		//registry.register(blockAdvancedExporter.getBlock());
		
		if(StorageTech.DEBUG)
			registry.register(blockGenerator.getBlock());
	}

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(blockPartsCombiner.getItem());
		
		registry.register(blockPOEDrive.getItem());
		registry.register(blockPOEImporter.getItem());
		registry.register(blockPOEExporter.getItem());
		registry.register(blockPOEFurnace.getItem());
		registry.register(blockStructureConstructor.getItem());
		//registry.register(blockAdvancedExporter.getItem());
		
		if(StorageTech.DEBUG)
			registry.register(blockGenerator.getItem());
	}

	public static void registerTiles(IForgeRegistry<TileEntityType<?>> registry)
	{
		registry.register(TileEntityType.Builder.create(() -> new TileEntityPartsCombiner(), blockPartsCombiner).build(null).setRegistryName(StorageTech.MODID, blockPartsCombiner.name));
		
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEDrive(), blockPOEDrive).build(null).setRegistryName(StorageTech.MODID, BlockPOEDrive.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEImporter(), blockPOEImporter).build(null).setRegistryName(StorageTech.MODID, BlockPOEImporter.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEExporter(), blockPOEExporter).build(null).setRegistryName(StorageTech.MODID, BlockPOEExporter.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityPOEFurnace(), blockPOEFurnace).build(null).setRegistryName(StorageTech.MODID, BlockPOEFurnace.raw_name)));
		registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityStructureConstructor(), blockStructureConstructor).build(null).setRegistryName(StorageTech.MODID, BlockStructureConstructor.raw_name)));
		//registry.register(registerTileDataParameters(TileEntityType.Builder.create(() -> new TileEntityAdvancedExporter(), blockAdvancedExporter).build(null).setRegistryName(StorageTech.MODID, BlockAdvancedExporter.raw_name)));
		
		if(StorageTech.DEBUG)
			registry.register(TileEntityType.Builder.create(() -> new BlockGenerator.TileEntityGenerator(), blockGenerator).build(null).setRegistryName(StorageTech.MODID, blockGenerator.name));
	}

	public static void registerContainers(IForgeRegistry<ContainerType<?>> registry)
	{
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPartsCombiner, TileEntityPartsCombiner>((windowId, inv, tile) -> new ContainerPartsCombiner(windowId, tile, inv))).setRegistryName(StorageTech.MODID, blockPartsCombiner.name));
		
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEDrive, TileEntityPOEDrive>((windowId, inv, tile) -> new ContainerPOEDrive(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEDrive.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEImporter, TileEntityPOEImporter>((windowId, inv, tile) -> new ContainerPOEImporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEImporter.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEExporter, TileEntityPOEExporter>((windowId, inv, tile) -> new ContainerPOEExporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEExporter.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerPOEFurnace, TileEntityPOEFurnace>((windowId, inv, tile) -> new ContainerPOEFurnace(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockPOEFurnace.raw_name));
		registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerStructureConstructor, TileEntityStructureConstructor>((windowId, inv, tile) -> new ContainerStructureConstructor(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockStructureConstructor.raw_name));
		//registry.register(IForgeContainerType.create(new PositionalTileContainerFactory<ContainerAdvancedExporter, TileEntityAdvancedExporter>((windowId, inv, tile) -> new ContainerAdvancedExporter(tile, inv.player, windowId))).setRegistryName(StorageTech.MODID, BlockAdvancedExporter.raw_name));
		
	}

	public static void registerNodes(INetworkNodeRegistry registry)
	{
		registry.add(NetworkNodePOEDrive.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEDrive(world, pos)));
		registry.add(NetworkNodePOEImporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEImporter(world, pos)));
		registry.add(NetworkNodePOEExporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEExporter(world, pos)));
		registry.add(NetworkNodePOEFurnace.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodePOEFurnace(world, pos)));
		registry.add(NetworkNodeStructureConstructor.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeStructureConstructor(world, pos)));
		//registry.add(NetworkNodeAdvancedExporter.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(tag, new NetworkNodeAdvancedExporter(world, pos)));
		
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
