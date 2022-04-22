package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.game.BaseGameAction;

/**
 * Message related to the end of the player's turn.
 */
public class EndTurnMessage extends ActionMessage
{
    protected EndTurnMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.END_TURN;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);
        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.END_TURN)
            throw new InvalidModuleException("[EndTurnMessage] Bad action type");

        handler.endTurn(this);
    }
}
