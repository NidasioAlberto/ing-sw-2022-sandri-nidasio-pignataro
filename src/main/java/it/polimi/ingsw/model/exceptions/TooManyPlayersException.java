package it.polimi.ingsw.model.exceptions;

/**
 * The TooManyPlayersException
 */
public class TooManyPlayersException extends Exception
{
    private int currentPlayersNumber;

    public TooManyPlayersException(int currentPlayersNumber)
    {
        super("There are currently to many players, there are " + currentPlayersNumber
                + " players");
        this.currentPlayersNumber = currentPlayersNumber;
    }

    int getCurrentPlayersNumber()
    {
        return currentPlayersNumber;
    }
}
