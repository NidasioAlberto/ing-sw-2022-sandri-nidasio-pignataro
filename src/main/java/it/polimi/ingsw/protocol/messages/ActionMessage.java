package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import java.io.Serializable;

/**
 * This class represents a message of the Command Pattern. Depending on the arrived message, we pass
 * via parameter the object to be commanded and the message itself calls a specific function of that
 * object to execute the command. It is used to avoid non-oop style switches.
 */
public abstract class ActionMessage implements Serializable
{
     /**
     * Checks if the given game action handler is valid.
     * 
     * @throws NullPointerException Thrown if the handler is null.
     */
    public void checkHandler(GameActionHandler handler) throws NullPointerException
    {
        if (handler == null)
            throw new NullPointerException("[ActionMessage] Handler is null");
    }

    /**
     * Method that has to be called to handle the message. Uses the given game action handler.
     * 
     * @param handler The controller handler that contains all the functions the player could call.
     * @throws NullPointerException Thrown if handler is null.
     */
    abstract public void applyAction(GameActionHandler handler) throws NullPointerException;

    abstract public BaseGameAction getBaseGameAction();
}
