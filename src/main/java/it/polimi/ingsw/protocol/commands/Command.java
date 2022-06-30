package it.polimi.ingsw.protocol.commands;

import java.io.Serial;
import java.io.Serializable;
import it.polimi.ingsw.network.PlayerConnection;

/**
 * Commands are message used from the player before the match or to quit the game.
 */
public abstract class Command implements Serializable
{
    @Serial
    private static final long serialVersionUID = 3628236076897277859L;

    /**
     * Checks if the given player connection is valid.
     * 
     * @throws NullPointerException Thrown if the handler is null.
     */
    public void checkPlayerConnection(PlayerConnection connection) throws NullPointerException
    {
        if (connection == null)
            throw new NullPointerException("[Command] Player connection is null");
    }

    /**
     * Method that has to be called to handle the command.
     * 
     * @param connection The player's connection used to apply the command.
     * @throws Exception Every possible exception thrown while applying the command.
     */
    abstract public void applyCommand(PlayerConnection connection) throws Exception;
}
