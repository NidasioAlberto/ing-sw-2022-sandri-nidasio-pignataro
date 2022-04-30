package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.ExpertGameAction;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message related to the activation of the character card effect.
 */
public class CharacterCardActionMessage extends ActionMessage
{
    ExpertGameAction action;

    protected CharacterCardActionMessage(JSONObject actionJson) throws JSONException
    {
        action = ExpertGameAction.valueOf(actionJson.getString("characterCardAction"));
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.characterCardAction(action);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.CHARACTER_CARD_ACTION;
    }
}
