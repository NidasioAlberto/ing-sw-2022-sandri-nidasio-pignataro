package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.controller.messages.ActionMessage;

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
     * This method is called when an end game exception is thrown. It brings the FSM to the end game
     * state.
     * 
     * @param handler The game handler that contains the FSM state
     */
    void onEndGame(GameActionHandler handler);

    /**
     * This method decides based on the actual state if the arrived action should be executed or
     * not.
     * 
     * @param message The message to be executed
     * @return Boolean that represents the result
     */
    boolean isLegitAction(ActionMessage message, GameActionHandler handler);
}
