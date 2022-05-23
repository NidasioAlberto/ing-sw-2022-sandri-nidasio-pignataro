package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

import java.io.Serial;

/**
 * Message related to the selection of the cloud tile.
 */
public class SelectCloudTileMessage extends ActionMessage
{
    @Serial
    private static final long serialVersionUID = -2918549655992359905L;

    private int selectedCloudTile;

    public SelectCloudTileMessage(int selectedCloudTile)
    {
        this.selectedCloudTile = selectedCloudTile;
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.selectCloudTile(selectedCloudTile);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.SELECT_CLOUD_TILE;
    }
}
