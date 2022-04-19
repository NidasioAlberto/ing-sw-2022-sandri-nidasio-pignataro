package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the played character card.
 */
public class PlayCharacterCardMessage extends ActionMessage
{
    protected PlayCharacterCardMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.playCharacterCard();
    }
}
