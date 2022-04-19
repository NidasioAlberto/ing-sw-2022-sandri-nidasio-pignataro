package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.ExpertGameAction;
import org.json.JSONObject;

/**
 * Message related to the activation of the character card effect.
 */
public class CharacterCardActionMessage extends ActionMessage
{
    protected CharacterCardActionMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        JSONObject message = new JSONObject(json);

        // Get the specific action played by the player
        ExpertGameAction action = ExpertGameAction.valueOf(
                message.getJSONObject("actionInfo").getString("characterCardAction"));

        handler.characterCardAction(this, action);
    }
}
