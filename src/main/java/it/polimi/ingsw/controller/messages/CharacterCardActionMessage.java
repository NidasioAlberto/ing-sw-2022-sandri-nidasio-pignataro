package it.polimi.ingsw.controller.messages;

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
    int selectedCharacterCard;

    protected CharacterCardActionMessage(JSONObject actionJson) throws JSONException
    {
        action = ExpertGameAction.valueOf(actionJson.getString("characterCardAction"));
        selectedCharacterCard = actionJson.getInt("selectedCharacterCard");
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.characterCardAction(action, selectedCharacterCard);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.CHARACTER_CARD_ACTION;
    }
}
