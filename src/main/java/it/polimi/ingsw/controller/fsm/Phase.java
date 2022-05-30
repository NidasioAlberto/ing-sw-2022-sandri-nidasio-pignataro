package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * This interface describes a single FSM node about the basic actions that a player could do. In
 * short terms, it maintains memory about what should be done following the correct game flow and
 * changes the game selected player depending on what player should do what.
 */
public interface Phase
{
    /**
     * This method changes the handler's internal state when a valid action is performed. With this
     * method we can let the FSM proceed and therefore suggest the controller whether an action is
     * legit.
     * 
     * @param handler The game handler that contains the FSM state
     */
    void onValidAction(GameActionHandler handler);

    /**
     * This method decides based on the actual state if the arrived action should be executed or
     * not.
     */
     boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction);
}
