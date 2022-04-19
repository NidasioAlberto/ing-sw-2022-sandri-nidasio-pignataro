package it.polimi.ingsw.model.exceptions;

public class NotEnoughCoinsException extends RuntimeException
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
