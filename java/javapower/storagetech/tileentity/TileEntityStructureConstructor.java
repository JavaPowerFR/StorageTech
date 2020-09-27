package javapower.storagetech.tileentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.node.NetworkNodeStructureConstructor;
import javapower.storagetech.util.SCElement;
import javapower.storagetech.util.Variable;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityStructureConstructor extends NetworkNodeTile<NetworkNodeStructureConstructor>
{
	@ObjectHolder(StorageTech.MODID+":structureconstructor")
	public static final TileEntityType<TileEntityStructureConstructor> CURRENT_TILE = null;
	
	public static final TileDataParameter<Boolean, TileEntityStructureConstructor> PROCESSING_MODE = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().getProcessingMode(), (t,v) -> t.getNode().setProcessingMode(v));
	public static final TileDataParameter<Boolean, TileEntityStructureConstructor> WORKING = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isWorking(), (t,v) -> t.getNode().setWorking(v));
	public static final TileDataParameter<Integer, TileEntityStructureConstructor> INDEX_PROCESS = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getIndex(), (t,v) -> t.getNode().resetIndex());
	
	
	//side only client ! dont save
	private boolean p_show_hologram = false;
	public Variable<Boolean> show_hologram = new Variable<Boolean>()
	{
		@Override
		public void set(Boolean v)
		{
			p_show_hologram = v;
		}
		
		@Override
		public Boolean get()
		{
			return p_show_hologram;
		}
	};
	
	public List<SCElement.Client> elements_client = new ArrayList<SCElement.Client>();
    
    public TileEntityStructureConstructor()
    {
        super(CURRENT_TILE);
        dataManager.addWatchedParameter(PROCESSING_MODE);
        dataManager.addWatchedParameter(WORKING);
        dataManager.addWatchedParameter(INDEX_PROCESS);
    }

    @Override
    @Nonnull
    public NetworkNodeStructureConstructor createNode(World world, BlockPos pos)
    {
        return new NetworkNodeStructureConstructor(world, pos);
    }   

}