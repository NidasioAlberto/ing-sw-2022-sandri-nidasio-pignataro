package it.polimi.ingsw.controller.messages;

import org.json.JSONException;
import org.json.JSONObject;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the selection of the cloud tile.
 */
public class SelectCloudTileMessage extends ActionMessage
{
    int selectedCloudTile;

    protected SelectCloudTileMessage(JSONObject actionJson) throws JSONException
    {
        selectedCloudTile = actionJson.getInt("selectedCloudTile");
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
