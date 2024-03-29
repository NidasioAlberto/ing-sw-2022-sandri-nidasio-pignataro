package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizable;

import java.io.Serial;

/**
 * Update that contains the index of the current player always based on table order.
 */
public class CurrentPlayerUpdate extends ModelUpdate
{
    @Serial
    private static final long serialVersionUID = -885830116465852489L;

    private int currentPlayerIndex;

    public CurrentPlayerUpdate(int currentPlayerIndex)
    {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    @Override
    public void handleUpdate(Visualizable handler)
    {
        handler.setCurrentPlayer(this);
    }

    public int getCurrentPlayerIndex()
    {
        return currentPlayerIndex;
    }
}
