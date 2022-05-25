package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;

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
    public void handleUpdate(Visualizer handler)
    {
        handler.setCurrentPlayer(this);
    }

    public int getCurrentPlayerIndex()
    {
        return currentPlayerIndex;
    }
}
