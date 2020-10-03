package javapower.storagetech.mekanism.grid;

import java.util.ArrayList;
import java.util.List;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.data.STData;
import javapower.storagetech.mekanism.api.IChemicalViewNode;
import javapower.storagetech.mekanism.packet.PacketChemicalGridInitialize;
import javapower.storagetech.mekanism.packet.PacketChemicalGridStackUpdate;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class ChemicalGrid implements IChemicalViewNode
{
	/** View Type (-1 = All, 0 = Gas,  1 = Infuse, 2 = Slurry) */
	protected int view_chemical_type = -1;
	/** Sorting direction (0 = ASCENDING, 1 = DESCENDING)*/
	protected int sorting_direction = 0;
	/** Sorting type (0 = Quantity, 1 = Name, 2 = Type) */
	protected int sorting_type = 0;
	/** Search box mode (0 = Normal, 1 = Normal (autoselected), 2 = JEI synchronized, 3 = JEI synchronized (autoselected)) */
	protected int search_box_mode = 0;
	/** Size (0 = Stretch, Small (3), 1 = Medium (5), 2 = Large (8)) */
	protected int size = 0;
	
	protected String search = "";
	
	//protected TreeMap<Chemical<?>, Long> chemicals = new TreeMap<>();
	protected List<ChemicalStack<?>> chemicals = new ArrayList<>();
	//private ClientChemicalGrid client;
	
	private List<ServerPlayerEntity> playerListener = new ArrayList<>();
	
	public ChemicalGrid(World world, STData stData)
	{
		
	}
	
	@Override
	public void updateChemical(List<ChemicalStack<?>> chemicalView /*TreeMap<Chemical<?>, Long> chemicalView*/)
	{
		chemicals.clear();
		
		if(chemicalView != null)
		{
			chemicals.addAll(chemicalView);
		}
		chemicals.removeIf((a) -> a == null || a.isEmpty());
		
		sendToListeners();
	}

	@Override
	public void updateChemicalStack(Chemical<?> chemical, long newValue)
	{
		if(chemical != null)
		{
			if(newValue == 0)
			{
				boolean isprensent = false;
				int index = 0;
				
				for(ChemicalStack<?> stack : chemicals)
				{
					if(stack.getType().equals(chemical))
					{
						isprensent = true;
						break;
					}
					++index;
				}
				
				if(isprensent)
				{
					chemicals.remove(index);
					sendToListeners(chemical, newValue);
					return;
				}
			}
			else
			{
				for(ChemicalStack<?> stack : chemicals)
				{
					if(stack.getType().equals(chemical))
					{
						stack.setAmount(newValue);
						sendToListeners(chemical, newValue);
						return;
					}
				}
			}
		
			chemicals.add(chemical.getStack(newValue));
			chemicals.removeIf((a) -> a == null || a.isEmpty());
			
			sendToListeners(chemical, newValue);
		}
			//chemicals.(chemical, newValue);
		
		sendToListeners(chemical, newValue);
	}
	
	public void write(CompoundNBT tag)
	{
		
	}
	
    public void read(CompoundNBT tag)
	{
    	
	}
	
	public void writeConfiguration(CompoundNBT tag)
	{
		tag.putInt("ViewChemicalType", view_chemical_type);
		tag.putInt("SortingDirection", sorting_direction);
		tag.putInt("SortingType", sorting_type);
		tag.putInt("SearchBoxMode", search_box_mode);
		tag.putInt("GridSize", size);
		tag.putString("Search", search);
		
	}
	
	public void readConfiguration(CompoundNBT tag)
	{
		if(tag.contains("ViewChemicalType"))
			view_chemical_type = tag.getInt("ViewChemicalType");
		
		if(tag.contains("SortingDirection"))
			sorting_direction = tag.getInt("SortingDirection");
		
		if(tag.contains("SortingType"))
			sorting_type = tag.getInt("SortingType");
		
		if(tag.contains("SearchBoxMode"))
			search_box_mode = tag.getInt("SearchBoxMode");
		
		if(tag.contains("GridSize"))
			size = tag.getInt("GridSize");
		
		if(tag.contains("Search"))
			search = tag.getString("Search");
		
	}
	
	public int getChemicalViewType()
	{
		return view_chemical_type;
	}

	public void setChemicalViewType(int v)
	{
		view_chemical_type = v;
	}

	public int getSortingDirection()
	{
		return sorting_direction;
	}

	public void setSortingDirection(int v)
	{
		sorting_direction = v;
	}

	public int getSortingType()
	{
		return sorting_type;
	}

	public void setSortingType(int v)
	{
		sorting_type = v;
	}

	public int getSearchBoxMode()
	{
		return search_box_mode;
	}

	public void setSearchBoxMode(int v)
	{
		search_box_mode = v;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int v)
	{
		size = v;
	}

	public void addPlayerListener(ServerPlayerEntity player)
	{
		playerListener.add(player);
		StorageTech.sendTo(player, new PacketChemicalGridInitialize(chemicals, search));
	}

	public void removePlayerListener(ServerPlayerEntity player)
	{
		playerListener.remove(player);
	}

	public String getSearch()
	{
		return search;
	}

	public void setSearch(String v)
	{
		search = v;
	}
	
	public void sendToListeners()
	{
		for(ServerPlayerEntity player : playerListener)
			StorageTech.sendTo(player, new PacketChemicalGridInitialize(chemicals, search));
	}
	
	private void sendToListeners(Chemical<?> chemical, long amnt)
	{
		for(ServerPlayerEntity player : playerListener)
			StorageTech.sendTo(player, new PacketChemicalGridStackUpdate(chemical, amnt));
	}
	
}
