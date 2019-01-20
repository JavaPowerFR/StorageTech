package javapower.storagetech.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUtils
{
	public static void emit(TileEntity tile, IEnergyStorage emitter, EnumFacing output)
	{
		if(!tile.getWorld().isRemote)
		{
			int energyToSend = Math.min(emitter.getEnergyStored(), emitter.getMaxEnergyStored());

			if(energyToSend > 0)
			{
				TileEntity te_insert = tile.getWorld().getTileEntity(tile.getPos().offset(output));
				if(te_insert != null && te_insert.hasCapability(CapabilityEnergy.ENERGY, output.getOpposite()))
				{
					IEnergyStorage es = te_insert.getCapability(CapabilityEnergy.ENERGY, output.getOpposite());
					if(es.canReceive())
					{
						emitter.extractEnergy(es.receiveEnergy(energyToSend, false), false);
					}
				}
			}
		}
	}
}
