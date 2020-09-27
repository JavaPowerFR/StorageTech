package javapower.storagetech.container;

import com.refinedmods.refinedstorage.container.BaseContainer;

import javapower.storagetech.core.StorageTech;
import javapower.storagetech.slot.SlotShowItemHandler;
import javapower.storagetech.tileentity.TileEntityPOEFurnace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerPOEFurnace extends BaseContainer
{
	@ObjectHolder(StorageTech.MODID+":poefurnace")
    public static final ContainerType<ContainerPOEFurnace> CURRENT_CONTAINER = null;
	
	public TileEntityPOEFurnace tileNode;
	
    public ContainerPOEFurnace(TileEntityPOEFurnace _tileNode, PlayerEntity player, int windowId)
    {
        super(CURRENT_CONTAINER, _tileNode, player, windowId);
        tileNode = _tileNode;
        int xoffset = 23;
        
        for (int i = 0; i < 9; ++i)
        {
            addSlot(new SlotItemHandler(tileNode.getNode().getPatternItems(), i, 8 + (18 * i) + xoffset, 20));
        }

        for (int i = 0; i < 4; ++i)
        {
            addSlot(new SlotItemHandler(tileNode.getNode().getUpgrades(), i, 187 + xoffset, 6 + (i * 18)));
        }
        
        //addSlot(new SlotShowItemHandler(tileNode.getNode().getInputs(), 0, 40, 56));
        addSlot(new SlotShowItemHandler(tileNode.getNode().getInputs(), 0, 82, 56));
        addSlot(new SlotShowItemHandler(tileNode.getNode().getInputs(), 1, 124, 56));
        //addSlot(new SlotShowItemHandler(tileNode.getNode().getInputs(), 3, 166, 56));
        
        addPlayerInventory(8 + xoffset, 106);

        transferManager.addBiTransfer(player.inventory, tileNode.getNode().getUpgrades());
        transferManager.addBiTransfer(player.inventory, tileNode.getNode().getPatternItems());
    }
}