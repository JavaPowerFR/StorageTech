/***************************************************
*						 ^
* 						/ \
* 					   /   \
*                     /  |  \
*                    /   .   \
*                   ___________
*                   
*   			[ W A R N I N G ! ]
*
****************************************************
*
*	All class belonging to the mod Storage Tech is developed by Cyril GENIN (Java_Power).
*	All rights reserved to Cyril GENIN.
*	it is strictly forbidden to copy or recopy!
*	These rules apply to all class, scripts, textures, configs and all file of this project.
*
*		author: Cyril GENIN (Java_Power)
*		email: cyrilgenintravail@gmail.com
*		creation date: 04/08/2017 (dd/mm/yyyy)
*		recreation date: 09/10/2020 (dd/mm/yyyy)
*		creat at: Montigny Le Bretonneux France
*		last modification: 23/11/2020 (dd/mm/yyyy)
*		comment: 16.1.3-R1.3
*		
***************************************************/
package javapower.storagetech.core;

import javapower.storagetech.block.STBlocks;
import javapower.storagetech.packet.RegisterPacket;
import javapower.storagetech.setup.ClientSetup;
import javapower.storagetech.setup.CommonSetup;
import javapower.storagetech.setup.ServerSetup;
import javapower.storagetech.util.IdDistributor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(StorageTech.MODID)
public class StorageTech
{
	public static final String MODID = "storagetech";
	public static final ItemGroup creativeTab = new ItemGroup(MODID)
	{

		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(STBlocks.blockPartsCombiner);
		}

	};
	
	private static final String PROTOCOL_VERSION = "2";
	public static final SimpleChannel INSTANCE_CHANNEL = NetworkRegistry.newSimpleChannel
			(
			new ResourceLocation(MODID, "general"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
		    PROTOCOL_VERSION::equals
			);
	
	public static final CommonConfig COMMON_CONFIG = new CommonConfig();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();
    
    public static boolean MOD_MEKANISM_IS_LOADED = false;
	
    // ---------------- DEBUG ----------------
    public static final boolean DEBUG = false;
	
	@SuppressWarnings("deprecation")
	public StorageTech()
	{
		try
		{
			Class.forName("mekanism.common.Mekanism");
			MOD_MEKANISM_IS_LOADED = true;
		}
		catch (ClassNotFoundException e){}
		
		DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientSetup::new);
		
        MinecraftForge.EVENT_BUS.register(new ServerSetup());
        MinecraftForge.EVENT_BUS.register(new CommonSetup.Events());
        
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG.getSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        CommonSetup commonSetup = new CommonSetup();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(commonSetup::onCommonSetup);
        
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, commonSetup::onRegisterBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, commonSetup::onRegisterTiles);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, commonSetup::onRegisterItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, commonSetup::onRegisterRecipeSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, commonSetup::onRegisterContainers);
        
        IdDistributor id = new IdDistributor();
        RegisterPacket.register(INSTANCE_CHANNEL, id);
        
        if(MOD_MEKANISM_IS_LOADED)
        	javapower.storagetech.mekanism.packet.RegisterPacket.register(INSTANCE_CHANNEL, id);
	}
	
	public static void sendTo(ServerPlayerEntity player, Object message)
	{
        if (!(player instanceof FakePlayer))
        {
            INSTANCE_CHANNEL.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
	
	@OnlyIn(Dist.CLIENT)
	public static boolean isShowInformation()
	{
		return ClientSetup.ShowInfo;
	}
	
}
