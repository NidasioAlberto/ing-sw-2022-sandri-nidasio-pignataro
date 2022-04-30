package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message related to the movement of mother nature.
 */
public class MoveMotherNatureMessage extends ActionMessage
{
    int selectedIsland;

    protected MoveMotherNatureMessage(JSONObject actionJson) throws JSONException
    {
        selectedIsland = actionJson.getInt("selectedIsland");
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.moveMotherNature(selectedIsland);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.MOVE_MOTHER_NATURE;
    }
}
