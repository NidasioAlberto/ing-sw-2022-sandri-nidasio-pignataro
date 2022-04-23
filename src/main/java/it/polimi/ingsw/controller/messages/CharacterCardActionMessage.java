package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.BaseGameAction;
import org.json.JSONObject;

/**
 * Message related to the activation of the character card effect.
 */
public class CharacterCardActionMessage extends ActionMessage
{
    protected CharacterCardActionMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.CHARACTER_CARD_ACTION;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.CHARACTER_CARD_ACTION)
            throw new InvalidModuleException("[CharacterCardActionMessage] Bad action type");

        JSONObject message = new JSONObject(json);

        // Get the specific action played by the player
        ExpertGameAction action = ExpertGameAction.valueOf(
                message.getJSONObject("actionInfo").getString("characterCardAction"));

        handler.characterCardAction(this, action);
    }
}
