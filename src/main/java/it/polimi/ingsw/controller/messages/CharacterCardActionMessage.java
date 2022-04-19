package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.ExpertGameAction;

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

        //TODO CHANGE THE ACTION WITH THE CORRECT ONE
        handler.characterCardAction(this, ExpertGameAction.BASE_ACTION);
    }
}
