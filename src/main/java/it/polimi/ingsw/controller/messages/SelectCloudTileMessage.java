package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.game.BaseGameAction;

/**
 * Message related to the selection of the cloud tile.
 */
public class SelectCloudTileMessage extends ActionMessage
{
    protected SelectCloudTileMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.SELECT_CLOUD_TILE;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);
        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.SELECT_CLOUD_TILE)
            throw new InvalidModuleException("[SelectCloudTileMessage] Bad action type");

        handler.selectCloudTile(this);
    }
}
