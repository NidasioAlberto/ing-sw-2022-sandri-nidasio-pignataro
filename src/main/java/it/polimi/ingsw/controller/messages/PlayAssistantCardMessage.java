package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the played assistant card.
 */
public class PlayAssistantCardMessage extends ActionMessage
{
    protected PlayAssistantCardMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.PLAY_ASSISTANT_CARD;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);
        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.PLAY_ASSISTANT_CARD)
            throw new InvalidModuleException("[PlayAssistantCardMessage] Bad action type");

        handler.playAssistantCard(this);
    }
}
