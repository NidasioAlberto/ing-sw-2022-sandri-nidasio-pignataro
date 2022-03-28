
package it.polimi.ingsw.model.exceptions;

public class NotEnoughPlayersException extends Exception
{
    public NotEnoughPlayersException()
    {
        super("There are currently not enough players");
    }
}
