package javapower.storagetech.mekanism.api;

import java.util.List;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;

public interface IChemicalViewNode
{
	//public void updateChemical(TreeMap<Chemical<?>, Long> chemicalView);
	public void updateChemical(List<ChemicalStack<?>> chemicalView);
	public void updateChemicalStack(Chemical<?> chemical, long newValue);
}
