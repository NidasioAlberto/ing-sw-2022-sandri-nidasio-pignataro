package it.polimi.ingsw.model.exceptions;

public class NotEnoughCoinsException extends Exception
{
    public NotEnoughCoinsException(String str)
    {
        super(str);
    }

    public NotEnoughCoinsException()
    {
        super("Not enough coins");
    }
}
