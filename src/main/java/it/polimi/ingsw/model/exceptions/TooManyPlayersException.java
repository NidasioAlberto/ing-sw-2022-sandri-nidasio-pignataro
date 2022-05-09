package it.polimi.ingsw.model.exceptions;

public class TooManyPlayersException extends Exception
{
    private int currentPlayersNumber;

    public TooManyPlayersException(int currentPlayersNumber)
    {
        super("There are currently too many players, there are " + currentPlayersNumber
                + " players");
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public int getCurrentPlayersNumber()
    {
        return currentPlayersNumber;
    }
}
