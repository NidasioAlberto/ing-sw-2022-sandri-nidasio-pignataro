package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the end of the player's turn.
 */
public class EndTurnMessage extends ActionMessage
{
    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.endTurn();
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.END_TURN;
    }
}
