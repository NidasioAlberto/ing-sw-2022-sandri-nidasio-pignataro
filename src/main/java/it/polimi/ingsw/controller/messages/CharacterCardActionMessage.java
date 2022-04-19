package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the activation of the character card effect.
 */
public class CharacterCardActionMessage extends  ActionMessage
{
    protected CharacterCardActionMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.characterCardAction();
    }
}
