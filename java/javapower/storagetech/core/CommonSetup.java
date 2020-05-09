package javapower.storagetech.core;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.item.STItems;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup
{
	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent e)
	{
		CommonConfig.loadConfig();
		//API.instance().getNetworkNodeRegistry().add(CustomStorageNetworkNode.NETWORK_NODE_ID, (tag, world, pos) -> readAndReturn(null, new CustomStorageNetworkNode(world, pos, (CustomStorageTile) world.getTileEntity(pos))));
	}
	
	/*private INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node)
	{
        node.read(tag);

        return node;
    }*/

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> register)
    {
    	
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> register)
    {
    	STBlocks.registerBlocks(register.getRegistry());
    }
    
    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> register)
    {
    	STBlocks.registerItems(register.getRegistry());
    	STItems.registerItems(register.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> register)
    {
    	STBlocks.registerTiles(register.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> register)
    {
    	STBlocks.registerContainers(register.getRegistry());
    }
}
