
package it.polimi.ingsw.model.exceptions;

public class NotEnoughPlayersException extends RuntimeException
{
    public NotEnoughPlayersException()
    {
        super("Currently there are not enough players to setup the game");
    }
}
