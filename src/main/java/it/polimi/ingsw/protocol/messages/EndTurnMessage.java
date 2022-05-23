package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

import java.io.Serial;

/**
 * Message related to the end of the player's turn.
 */
public class EndTurnMessage extends ActionMessage
{
    @Serial
    private static final long serialVersionUID = -6837807190797953796L;

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
