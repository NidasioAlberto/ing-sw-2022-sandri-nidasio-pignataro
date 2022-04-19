package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the selection of the cloud tile.
 */
public class SelectCloudTileMessage extends ActionMessage
{
    protected SelectCloudTileMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.selectCloudTile(this);
    }
}
