package javapower.storagetech.mekanism.inventory;

import com.refinedmods.refinedstorage.api.util.IFilter;

import mekanism.api.chemical.ChemicalStack;

public class ChemicalFilter implements IFilter<ChemicalStack<?>>
{
	private final ChemicalStack<?> stack;
    private final int compare;
    private final int mode;
    private final boolean modFilter;

    public ChemicalFilter(ChemicalStack<?> stack, int compare, int mode, boolean modFilter)
    {
        this.stack = stack;
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
    }
    
	@Override
	public int getCompare()
	{
		return compare;
	}

	@Override
	public int getMode()
	{
		return mode;
	}

	@Override
	public ChemicalStack<?> getStack()
	{
		return stack;
	}

	@Override
	public boolean isModFilter()
	{
		return modFilter;
	}

}
