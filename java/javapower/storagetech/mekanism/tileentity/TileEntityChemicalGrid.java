package javapower.storagetech.mekanism.tileentity;

import javax.annotation.Nonnull;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.mekanism.node.NetworkNodeChemicalGrid;
import javapower.storagetech.mekanism.screen.ScreenChemicalGrid;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityChemicalGrid extends NetworkNodeTile<NetworkNodeChemicalGrid>
{
	@ObjectHolder(StorageTech.MODID+":chemicalgrid")
	public static final TileEntityType<TileEntityChemicalGrid> CURRENT_TILE = null;
	
	//Sorting direction
	//Sorting type (Quantity, Name, Type)
	//Search box mode (Normal, Normal (autoselected), JEI synchronized, JEI synchronized (autoselected))
	//Size (Stretch, Small (3), Medium (5), Large (8))
	
	public static final TileDataParameter<Integer, TileEntityChemicalGrid> VIEW_CHEMICAL_TYPE = new TileDataParameter<>(DataSerializers.VARINT, -1,
			t -> t.getNode().getChemicalViewType(),
			(t, v) -> t.getNode().setChemicalViewType(v),
			(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.getGrid().filter()));
    
	public static final TileDataParameter<Integer, TileEntityChemicalGrid> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0,
    		t -> t.getNode().getSortingDirection(),
    		(t, v) -> t.getNode().setSortingDirection(v),
    		(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.getGrid().sortGrid()));
    
    public static final TileDataParameter<Integer, TileEntityChemicalGrid> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0,
    		t -> t.getNode().getSortingType(),
    		(t, v) -> t.getNode().setSortingType(v),
    		(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.getGrid().sortGrid()));
    
    public static final TileDataParameter<Integer, TileEntityChemicalGrid> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0,
    		t -> t.getNode().getSearchBoxMode(),
    		(t, v) -> t.getNode().setSearchBoxMode(v),
    		(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.getGrid().filter()));
    
    public static final TileDataParameter<Integer, TileEntityChemicalGrid> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0,
    		t -> t.getNode().getSize(),
    		(t, v) -> t.getNode().setSize(v),
    		(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.resize(screen.getMinecraft(), screen.width, screen.height)));
    
    public static final TileDataParameter<String, TileEntityChemicalGrid> SEARCH_STRING = new TileDataParameter<>(DataSerializers.STRING, "",
    		t -> t.getNode().getSearch(),
    		(t, v) -> t.getNode().setSearch(v),
    		(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.getGrid().setSearch(p)));
    
    public static final TileDataParameter<Integer, TileEntityChemicalGrid> TAB_ID = new TileDataParameter<>(DataSerializers.VARINT, 0,
    		t -> t.getNode().getTab(),
    		(t, v) -> t.getNode().setTab(v),
    		(initial, p) -> BaseScreen.executeLater(ScreenChemicalGrid.class, (screen) -> screen.getGrid().setTab()));
    
	
    public TileEntityChemicalGrid()
    {
        super(CURRENT_TILE);
        
        dataManager.addWatchedParameter(VIEW_CHEMICAL_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(SEARCH_STRING);
        dataManager.addWatchedParameter(TAB_ID); 
    }

	@Override
    @Nonnull
    public NetworkNodeChemicalGrid createNode(World world, BlockPos pos)
    {
        return new NetworkNodeChemicalGrid(world, pos);
    }
}