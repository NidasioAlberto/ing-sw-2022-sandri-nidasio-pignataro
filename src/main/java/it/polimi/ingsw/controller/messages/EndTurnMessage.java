package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the end of the player's turn.
 */
public class EndTurnMessage extends ActionMessage
{
    protected EndTurnMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.endTurn(this);
    }
}
