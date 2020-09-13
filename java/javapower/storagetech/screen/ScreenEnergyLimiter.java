package javapower.storagetech.screen;

import org.apache.commons.lang3.tuple.Pair;

import com.refinedmods.refinedstorage.screen.AmountSpecifyingScreen;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenEnergyLimiter extends AmountSpecifyingScreen<Container>
{
    private final TileDataParameter<Integer, ?> priority;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ScreenEnergyLimiter(BaseScreen parent, TileDataParameter<Integer, ?> priority, PlayerInventory inventory)
    {
        super(parent, new Container(null, 0)
        {
            @Override
            public boolean canInteractWith(PlayerEntity player)
            {
                return false;
            }
        }, 164, 92, inventory, new TranslationTextComponent("misc.storagetech.energylimiter"));

        this.priority = priority;
    }

    @Override
    protected int getDefaultAmount()
    {
        return priority.getValue();
    }

    @Override
    protected ITextComponent getOkButtonText()
    {
        return new TranslationTextComponent("misc.refinedstorage.set");
    }

    @Override
    protected String getTexture()
    {
        return "gui/priority.png";
    }

    @Override
    protected Pair<Integer, Integer> getAmountPos()
    {
        return Pair.of(18 + 1, 47 + 1);
    }

    @Override
    protected Pair<Integer, Integer> getOkCancelPos()
    {
        return Pair.of(107, 30);
    }

    @Override
    protected boolean canAmountGoNegative()
    {
        return true;
    }

    @Override
    protected int getMaxAmount()
    {
        return Integer.MAX_VALUE-1;
    }

    @Override
    protected int[] getIncrements()
    {
        return new int[]{
            1, 10, 100,
            -1, -10, -100
        };
    }

    @Override
    protected void onOkButtonPressed(boolean noPreview)
    {
        try
        {
            int amount = Integer.parseInt(amountField.getText());

            TileDataManager.setParameter(priority, amount);

            close();
        }
        catch (NumberFormatException e)
        {
            // NO OP
        }
    }
}