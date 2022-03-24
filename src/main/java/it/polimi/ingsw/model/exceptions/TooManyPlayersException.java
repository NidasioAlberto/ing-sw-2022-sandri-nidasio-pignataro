package it.polimi.ingsw.model.exceptions;

/**
 * The TooManyPlayersException
 */
public class TooManyPlayersException extends Exception
{
    private int currentPlayersNumber;

    public TooManyPlayersException(int currentPlayersNumber)
    {
        super("This game can't accept any more players, currently there are " + currentPlayersNumber
                + " players");
        this.currentPlayersNumber = currentPlayersNumber;
    }

    int getCurrentPlayersNumber()
    {
        return currentPlayersNumber;
    }
}
