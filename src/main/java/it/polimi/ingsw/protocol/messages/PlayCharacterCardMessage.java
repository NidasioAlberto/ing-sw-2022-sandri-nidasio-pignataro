package it.polimi.ingsw.protocol.messages;

import org.json.JSONException;
import org.json.JSONObject;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the played character card.
 */
public class PlayCharacterCardMessage extends ActionMessage
{
    int selectedCharacterCard;

    protected PlayCharacterCardMessage(JSONObject actionJson) throws JSONException
    {
        selectedCharacterCard = actionJson.getInt("selectedCharacterCard");
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.playCharacterCard(selectedCharacterCard);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.PLAY_CHARACTER_CARD;
    }
}
