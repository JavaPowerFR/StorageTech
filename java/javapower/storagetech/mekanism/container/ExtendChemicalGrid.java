package javapower.storagetech.mekanism.container;

import javapower.storagetech.mekanism.tileentity.TileEntityChemicalGrid;
import javapower.storagetech.packet.IGenericMessage;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ExtendChemicalGrid 
{
	public static void tryToInteractWidthHeldStack(Chemical<?> chemicalClicked, boolean putInGrid, boolean shift, ServerPlayerEntity player, TileEntityChemicalGrid tileNode)
	{
		//System.out.println(chemicalClicked + " "+ putInGrid + " "+shift);
		int maxAmnt = shift ? 64000 : 1000;
		
		ItemStack mouseStack = player.inventory.getItemStack();
		if(mouseStack != null && !mouseStack.isEmpty())
		{
			if(putInGrid)
			{
				IGasHandler ghandler = mouseStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).orElse(null);
				if(ghandler != null)
				{
					GasStack stackExtracted = ghandler.extractChemical(maxAmnt, Action.SIMULATE);
					if(!stackExtracted.isEmpty())
					{
						GasStack result = (GasStack) tileNode.getNode().getStData().getMekanismData().insertChemical(stackExtracted, Action.SIMULATE);
						
						long amntExt = maxAmnt - result.getAmount();
						if(amntExt > 0)
						{
							tileNode.getNode().getStData().getMekanismData().insertChemical(ghandler.extractChemical((GasStack) stackExtracted.getType().getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
							
							CompoundNBT nbt = new CompoundNBT();
							nbt.put("heldItem", player.inventory.getItemStack().write(new CompoundNBT()));
							IGenericMessage.sendToScreen(player, nbt);
							
							return;
						}
					}
				}
				
				IInfusionHandler ihandler = mouseStack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).orElse(null);
				if(ihandler != null)
				{
					InfusionStack stackExtracted = ihandler.extractChemical(maxAmnt, Action.SIMULATE);
					if(!stackExtracted.isEmpty())
					{
						InfusionStack result = (InfusionStack) tileNode.getNode().getStData().getMekanismData().insertChemical(stackExtracted, Action.SIMULATE);
						
						long amntExt = maxAmnt - result.getAmount();
						if(amntExt > 0)
						{
							tileNode.getNode().getStData().getMekanismData().insertChemical(ihandler.extractChemical((InfusionStack) stackExtracted.getType().getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
							
							CompoundNBT nbt = new CompoundNBT();
							nbt.put("heldItem", player.inventory.getItemStack().write(new CompoundNBT()));
							IGenericMessage.sendToScreen(player, nbt);
							
							return;
						}
					}
				}
				
				ISlurryHandler shandler = mouseStack.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY).orElse(null);
				if(shandler != null)
				{
					SlurryStack stackExtracted = shandler.extractChemical(maxAmnt, Action.SIMULATE);
					if(!stackExtracted.isEmpty())
					{
						SlurryStack result = (SlurryStack) tileNode.getNode().getStData().getMekanismData().insertChemical(stackExtracted, Action.SIMULATE);
						
						long amntExt = maxAmnt - result.getAmount();
						if(amntExt > 0)
						{
							tileNode.getNode().getStData().getMekanismData().insertChemical(shandler.extractChemical((SlurryStack) stackExtracted.getType().getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
							
							CompoundNBT nbt = new CompoundNBT();
							nbt.put("heldItem", player.inventory.getItemStack().write(new CompoundNBT()));
							IGenericMessage.sendToScreen(player, nbt);
							
							return;
						}
					}
				}
				
			}
			else
			{
				if(chemicalClicked != null)
				{
					if(chemicalClicked instanceof Gas)
					{
						IGasHandler handler = mouseStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).orElse(null);
						if(handler != null)
						{
							GasStack stackExtracted = (GasStack) tileNode.getNode().getStData().getMekanismData().extractChemical((GasStack) chemicalClicked.getStack(maxAmnt), Action.SIMULATE);
							GasStack result = (GasStack) handler.insertChemical(stackExtracted, Action.SIMULATE);
							long amntExt = maxAmnt - result.getAmount();
							if(amntExt > 0)
							{
								GasStack stack = handler.insertChemical((GasStack) tileNode.getNode().getStData().getMekanismData().extractChemical((GasStack) chemicalClicked.getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
								if(!stack.isEmpty())
									tileNode.getNode().getStData().getMekanismData().insertChemical(stack, Action.EXECUTE);
								
								CompoundNBT nbt = new CompoundNBT();
								nbt.put("heldItem", player.inventory.getItemStack().write(new CompoundNBT()));
								IGenericMessage.sendToScreen(player, nbt);
							}
						}
					}
					else if(chemicalClicked instanceof InfuseType)
					{
						IInfusionHandler handler = mouseStack.getCapability(Capabilities.INFUSION_HANDLER_CAPABILITY).orElse(null);
						if(handler != null)
						{
							InfusionStack stackExtracted = (InfusionStack) tileNode.getNode().getStData().getMekanismData().extractChemical((InfusionStack) chemicalClicked.getStack(maxAmnt), Action.SIMULATE);
							InfusionStack result = (InfusionStack) handler.insertChemical(stackExtracted, Action.SIMULATE);
							long amntExt = maxAmnt - result.getAmount();
							if(amntExt > 0)
							{
								InfusionStack stack = handler.insertChemical((InfusionStack) tileNode.getNode().getStData().getMekanismData().extractChemical((InfusionStack) chemicalClicked.getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
								if(!stack.isEmpty())
									tileNode.getNode().getStData().getMekanismData().insertChemical(stack, Action.EXECUTE);
								
								CompoundNBT nbt = new CompoundNBT();
								nbt.put("heldItem", player.inventory.getItemStack().write(new CompoundNBT()));
								IGenericMessage.sendToScreen(player, nbt);
							}
						}
					}
					else if(chemicalClicked instanceof Slurry)
					{
						ISlurryHandler handler = mouseStack.getCapability(Capabilities.SLURRY_HANDLER_CAPABILITY).orElse(null);
						if(handler != null)
						{
							SlurryStack stackExtracted = (SlurryStack) tileNode.getNode().getStData().getMekanismData().extractChemical((SlurryStack) chemicalClicked.getStack(maxAmnt), Action.SIMULATE);
							SlurryStack result = (SlurryStack) handler.insertChemical(stackExtracted, Action.SIMULATE);
							long amntExt = maxAmnt - result.getAmount();
							if(amntExt > 0)
							{
								SlurryStack stack = handler.insertChemical((SlurryStack) tileNode.getNode().getStData().getMekanismData().extractChemical((SlurryStack) chemicalClicked.getStack(amntExt), Action.EXECUTE), Action.EXECUTE);
								if(!stack.isEmpty())
									tileNode.getNode().getStData().getMekanismData().insertChemical(stack, Action.EXECUTE);
								
								CompoundNBT nbt = new CompoundNBT();
								nbt.put("heldItem", player.inventory.getItemStack().write(new CompoundNBT()));
								IGenericMessage.sendToScreen(player, nbt);
							}
						}
					}
				}
			}
		}
	}
}
