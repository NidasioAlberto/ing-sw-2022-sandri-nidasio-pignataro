package it.polimi.ingsw.model.exceptions;

public class NotEnoughCoins extends Exception
{
    public NotEnoughCoins(String str)
    {
        super(str);
    }

    public NotEnoughCoins()
    {
        super("Not enough coins");
    }
}
