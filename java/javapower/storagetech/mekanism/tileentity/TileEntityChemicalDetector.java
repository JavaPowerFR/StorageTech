package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalDetector;
import javapower.storagetech.mekanism.screen.ScreenChemicalDetector;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityChemicalDetector extends NetworkNodeTile<NetworkNodeChemicalDetector>
{
	@ObjectHolder(StorageTech.MODID+":chemicaldetector")
	public static final TileEntityType<TileEntityChemicalExporter> CURRENT_TILE = null;
	
	public static final TileDataParameter<Integer, TileEntityChemicalDetector> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileEntityChemicalDetector> MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getMode(), (t, v) ->
    {
        if (v == DetectorNetworkNode.MODE_UNDER || v == DetectorNetworkNode.MODE_EQUAL || v == DetectorNetworkNode.MODE_ABOVE)
        {
            t.getNode().setMode(v);
            t.getNode().markDirty();
        }
    });
    public static final TileDataParameter<Long, TileEntityChemicalDetector> AMOUNT = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getAmount(), (t, v) ->
    {
        t.getNode().setAmount(v);
        t.getNode().markDirty();
    },
    (initial, value) -> BaseScreen.executeLater(ScreenChemicalDetector.class, detectorScreen -> detectorScreen.updateAmountField(value)));
    
	public TileEntityChemicalDetector()
	{
		super(CURRENT_TILE);
		
		dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(AMOUNT);
	}
	
	@Override
    public void readUpdate(CompoundNBT tag)
	{
        getNode().setPowered(tag.getBoolean("Powered"));

        super.readUpdate(tag);
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag)
    {
        super.writeUpdate(tag);

        tag.putBoolean("Powered", getNode().isPowered());

        return tag;
    }

	@Override
    @Nonnull
	public NetworkNodeChemicalDetector createNode(World world, BlockPos pos) 
	{
		return new NetworkNodeChemicalDetector(world, pos);
	}

}
