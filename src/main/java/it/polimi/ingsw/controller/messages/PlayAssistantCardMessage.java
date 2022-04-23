package it.polimi.ingsw.controller.messages;

import org.json.JSONException;
import org.json.JSONObject;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the played assistant card.
 */
public class PlayAssistantCardMessage extends ActionMessage
{
    int selectedCard;

    PlayAssistantCardMessage(JSONObject actionJson) throws JSONException
    {
        selectedCard = actionJson.getInt("selectedCard");
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.playAssistantCard(selectedCard);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.PLAY_ASSISTANT_CARD;
    }
}
