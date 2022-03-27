
package it.polimi.ingsw.model.exceptions;

/**
 * The NotEnoughPlayersException
 */
public class NotEnoughPlayersException extends Exception
{
    public NotEnoughPlayersException()
    {
        super("There are currently not enough players");
    }
}
